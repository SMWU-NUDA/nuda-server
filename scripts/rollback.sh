#!/bin/bash
# scripts/rollback.sh

set -euo pipefail

log() { echo "[ROLLBACK][$(date '+%H:%M:%S')] $*"; }
die() { echo "[ERROR] $*" >&2; exit 1; }

cd "$(dirname "$0")/.."

UPSTREAM_FILE="./nginx/upstream.conf"
ROLLBACK_READY_TIMEOUT=60

current=$(cat "$UPSTREAM_FILE")

if echo "$current" | grep -q "app-blue"; then
  rollback_target="app-green"
  rollback_slot="green"
  rollback_port=8082
else
  rollback_target="app-blue"
  rollback_slot="blue"
  rollback_port=8081
fi

# 정지 상태면 재기동
if ! docker ps --filter "name=nuda-app-${rollback_slot}" --filter "status=running" | grep -q "nuda-app-${rollback_slot}"; then
  log "롤백 대상(nuda-app-${rollback_slot}) 정지 상태. 재기동 시도..."
  docker compose -f docker-compose.app.yml --profile "${rollback_slot}" start "${rollback_target}" 2>/dev/null || \
    die "롤백 대상 컨테이너 재기동 실패. 수동 복구 필요."
fi

# Readiness 대기
log "롤백 대상 Readiness 대기 (최대 ${ROLLBACK_READY_TIMEOUT}초)..."
local_elapsed=0
until wget -qO- "http://localhost:${rollback_port}/actuator/health/readiness" | grep -q '"status":"UP"'; do
  if [[ $local_elapsed -ge $ROLLBACK_READY_TIMEOUT ]]; then
    die "롤백 대상(${rollback_target}) Readiness 타임아웃. 수동 복구 필요."
  fi
  sleep 5
  local_elapsed=$((local_elapsed + 5))
  log "  대기 중... (${local_elapsed}/${ROLLBACK_READY_TIMEOUT}s)"
done

log "롤백: 현재=$(echo "$current" | grep -o 'app-[a-z]*') → 대상=${rollback_target}"
echo "server ${rollback_target}:8080;" > "$UPSTREAM_FILE"
docker exec nuda-nginx nginx -s reload
log "✓ 롤백 완료 (${rollback_target} 활성화)"
