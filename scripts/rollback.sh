#!/bin/bash
# scripts/rollback.sh
set -euo pipefail

log() { echo "[ROLLBACK][$(date '+%H:%M:%S')] $*"; }
die() { echo "[ERROR] $*" >&2; exit 1; }

UPSTREAM_FILE="./nginx/upstream.conf"

current=$(cat "$UPSTREAM_FILE")

if echo "$current" | grep -q "app-blue"; then
  rollback_target="app-green"
  rollback_port=8082
else
  rollback_target="app-blue"
  rollback_port=8081
fi

if ! docker ps --filter "name=nuda-${rollback_target#app-}" --filter "status=running" | grep -q "nuda-"; then
  die "롤백 대상 컨테이너(nuda-${rollback_target#app-})가 실행 중이 아닙니다. 수동 복구 필요."
fi

if ! wget -qO- "http://localhost:${rollback_port}/actuator/health/readiness" | grep -q '"status":"UP"'; then
  die "롤백 대상(${rollback_target}:${rollback_port}) Readiness 실패. 수동 복구 필요."
fi

log "롤백: 현재=$(echo $current | grep -o 'app-[a-z]*') → 대상=${rollback_target}"
echo "server ${rollback_target}:8080;" > "$UPSTREAM_FILE"
docker exec nuda-nginx nginx -s reload
log "✓ 롤백 완료 (${rollback_target} 활성화)"
