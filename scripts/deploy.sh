#!/bin/bash
# scripts/deploy.sh
set -euo pipefail

IMAGE_NAME="${IMAGE_NAME:-ghcr.io/smwu-nuda/nuda-server}"
IMAGE_TAG="${IMAGE_TAG:-latest}"
HEALTH_CHECK_URL_BLUE="http://localhost:8081/actuator/health/readiness"
HEALTH_CHECK_URL_GREEN="http://localhost:8082/actuator/health/readiness"
HEALTH_TIMEOUT=120
STABLE_WAIT=120

log() { echo "[$(date '+%H:%M:%S')] $*"; }
die() { echo "[ERROR] $*" >&2; exit 1; }

# ──────────────────────────────────────────────
# STEP -1: SSM Parameter Store에서 환경변수 로드
# ──────────────────────────────────────────────
load_ssm_params() {
  log "SSM Parameter Store 환경변수 로드 중 (/nuda/prod)..."

  aws ssm get-parameters-by-path \
    --path /nuda/prod \
    --with-decryption \
    --query "Parameters[*].[Name,Value]" \
    --output text | \
  while IFS=$'\t' read -r name value; do
    echo "${name##*/}=${value}"
  done > /tmp/.env.prod

  local param_count
  param_count=$(wc -l < /tmp/.env.prod)
  if [[ "$param_count" -lt 10 ]]; then
    die "SSM 파라미터 로드 불완전 (${param_count}개). IAM 역할 또는 파라미터 경로 확인."
  fi

  log "✓ SSM 환경변수 로드 완료 (${param_count}개)"
}

# ──────────────────────────────────────────────
# STEP 0: 인프라 컨테이너 헬스 확인
# ──────────────────────────────────────────────
check_infra() {
  log "인프라 컨테이너 상태 확인..."

  if ! docker exec nuda-redis redis-cli ping | grep -q "PONG"; then
    die "Redis 응답 없음. 배포를 중단합니다."
  fi
  log "✓ Redis 정상"

  local es_status
  es_status=$(curl -sf http://localhost:9200/_cluster/health | python3 -c "import sys,json; print(json.load(sys.stdin)['status'])" 2>/dev/null || echo "error")
  if [[ "$es_status" == "red" || "$es_status" == "error" ]]; then
    die "Elasticsearch 상태 이상 (status=$es_status). 배포를 중단합니다."
  fi
  log "✓ Elasticsearch 정상 (status=$es_status)"
}

# ──────────────────────────────────────────────
# STEP 1: 현재 활성 슬롯 판단
# ──────────────────────────────────────────────
get_active_slot() {
  local upstream
  upstream=$(cat ./nginx/upstream.conf)
  if echo "$upstream" | grep -q "app-blue"; then
    echo "blue"
  else
    echo "green"
  fi
}

# ──────────────────────────────────────────────
# STEP 2: 비활성 슬롯에 신규 이미지 기동
# ──────────────────────────────────────────────
start_new_slot() {
  local new_slot="$1"
  local new_port new_container

  if [[ "$new_slot" == "green" ]]; then
    new_port=8082
    new_container="nuda-app-green"
  else
    new_port=8081
    new_container="nuda-app-blue"
  fi

  log "이미지 Pull: ${IMAGE_NAME}:${IMAGE_TAG}"
  docker pull "${IMAGE_NAME}:${IMAGE_TAG}"

  log "${new_slot} 슬롯 기동 (port: ${new_port})..."
  IMAGE_NAME="$IMAGE_NAME" IMAGE_TAG="$IMAGE_TAG" \
    docker compose -f docker-compose.app.yml \
      --profile "${new_slot}" \
      up -d "${new_container}"
}

# ──────────────────────────────────────────────
# STEP 3: Readiness Probe 통과 대기
# ──────────────────────────────────────────────
wait_for_ready() {
  local slot="$1"
  local url

  if [[ "$slot" == "green" ]]; then
    url="$HEALTH_CHECK_URL_GREEN"
  else
    url="$HEALTH_CHECK_URL_BLUE"
  fi

  log "${slot} Readiness 대기 (최대 ${HEALTH_TIMEOUT}초)..."
  local elapsed=0
  until wget -qO- "$url" | grep -q '"status":"UP"'; do
    if [[ $elapsed -ge $HEALTH_TIMEOUT ]]; then
      die "${slot} 헬스체크 타임아웃. 롤백 필요."
    fi
    sleep 5
    elapsed=$((elapsed + 5))
    log "  대기 중... (${elapsed}/${HEALTH_TIMEOUT}s)"
  done
  log "✓ ${slot} Readiness 통과"
}

# ──────────────────────────────────────────────
# STEP 4: Nginx 트래픽 전환
# ──────────────────────────────────────────────
switch_traffic() {
  local new_slot="$1"
  local new_container

  if [[ "$new_slot" == "green" ]]; then
    new_container="app-green"
  else
    new_container="app-blue"
  fi

  log "Nginx upstream → ${new_slot} (${new_container}:8080)"
  echo "server ${new_container}:8080;" > ./nginx/upstream.conf
  docker exec nuda-nginx nginx -s reload
  log "✓ 트래픽 전환 완료"
}

# ──────────────────────────────────────────────
# STEP 5: 전환 후 안정성 확인
# ──────────────────────────────────────────────
verify_stable() {
  local new_slot="$1"
  log "전환 후 ${STABLE_WAIT}초 안정성 확인 (Prometheus rate[2m] 윈도우 채우기)..."
  sleep "$STABLE_WAIT"

  if ! wget -qO- "http://localhost:9090/-/healthy" | grep -q "Prometheus"; then
    log "⚠ Prometheus 응답 없음. 에러율 자동 확인 불가 — 수동으로 Grafana 확인 후 계속하세요."
    return 0
  fi

  local error_rate
  error_rate=$(wget -qO- \
    "http://localhost:9090/api/v1/query?query=sum(rate(http_server_requests_seconds_count%7Bstatus%3D~%225..%22%7D%5B2m%5D))%2Fsum(rate(http_server_requests_seconds_count%5B2m%5D))*100" \
    2>/dev/null \
    | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    result = data.get('data', {}).get('result', [])
    print(float(result[0]['value'][1]) if result else -1)
except Exception:
    print(-1)
" 2>/dev/null || echo "-1")

  if [[ "$error_rate" == "-1" ]]; then
    log "⚠ 에러율 데이터 없음 (스크레이프 대기 중). 수동 Grafana 확인 권장."
    return 0
  fi

  if python3 -c "import sys; sys.exit(0 if float('${error_rate}') > 10 else 1)" 2>/dev/null; then
    log "⚠ 에러율 ${error_rate}% 감지. 자동 롤백 실행..."
    bash scripts/rollback.sh
    die "에러율 초과로 롤백 완료. 배포 실패."
  fi

  log "✓ 안정성 확인 완료 (에러율: ${error_rate}%)"
}

# ──────────────────────────────────────────────
# STEP 6: 구 슬롯 정리 (5분 후 백그라운드)
# ──────────────────────────────────────────────
cleanup_old_slot() {
  local old_slot="$1"
  local old_container="nuda-app-${old_slot}"

  log "구 슬롯(${old_slot}) 5분 후 정리 예약 (롤백 창 확보)..."

  (
    sleep 300
    log "구 슬롯(${old_slot}) 컨테이너 정리 시작..."
    docker compose -f docker-compose.app.yml --profile "${old_slot}" stop "${old_container#nuda-}" 2>/dev/null || true
    docker image prune -f
    log "✓ 구 슬롯 정리 완료"
  ) &

  log "✓ 구 슬롯 정리 예약됨 (5분 후, PID: $!). 그 전까지 즉각 롤백 가능."
}

# ──────────────────────────────────────────────
# 메인 실행
# ──────────────────────────────────────────────
LOCK_FILE="/tmp/nuda-deploy.lock"

main() {
  if ! mkdir "$LOCK_FILE" 2>/dev/null; then
    die "다른 배포가 진행 중입니다 (lock: ${LOCK_FILE}). 완료 후 재시도하세요."
  fi
  trap "rmdir '$LOCK_FILE' 2>/dev/null; exit" EXIT INT TERM

  log "=== Blue-Green 배포 시작 ==="
  log "이미지: ${IMAGE_NAME}:${IMAGE_TAG}"

  load_ssm_params
  check_infra

  local active_slot new_slot
  active_slot=$(get_active_slot)
  new_slot=$([ "$active_slot" == "blue" ] && echo "green" || echo "blue")

  log "현재 활성 슬롯: ${active_slot} → 신규 슬롯: ${new_slot}"

  start_new_slot "$new_slot"
  wait_for_ready "$new_slot"
  switch_traffic "$new_slot"
  verify_stable "$new_slot"
  cleanup_old_slot "$active_slot"

  log "=== 배포 완료: ${new_slot} 슬롯 활성화 ==="
}

main "$@"
