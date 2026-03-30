#!/bin/bash
# scripts/switch.sh — 수동 트래픽 전환 (테스트/비상용)
set -euo pipefail

TARGET="${1:-}"
if [[ "$TARGET" != "blue" && "$TARGET" != "green" ]]; then
  echo "사용법: $0 <blue|green>"
  exit 1
fi

CONTAINER="nuda-app-${TARGET}"
PORT=$([[ "$TARGET" == "blue" ]] && echo "8081" || echo "8082")

if ! docker ps --filter "name=nuda-app-${TARGET}" --filter "status=running" | grep -q "nuda-"; then
  echo "[ERROR] nuda-app-${TARGET} 컨테이너가 실행 중이 아닙니다."
  exit 1
fi

echo "[SWITCH] upstream → ${CONTAINER}:8080"
echo "server ${CONTAINER}:8080;" > ./nginx/upstream.conf
docker exec nuda-nginx nginx -s reload
echo "[SWITCH] ✓ 완료. 현재 활성: ${TARGET} (port ${PORT})"
