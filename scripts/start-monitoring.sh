#!/bin/bash
# 모니터링 스택 기동 스크립트
# 사용법: GRAFANA_PASSWORD=<pw> DISCORD_WEBHOOK_URL=<url> bash scripts/start-monitoring.sh

set -euo pipefail

: "${GRAFANA_PASSWORD:?GRAFANA_PASSWORD 환경변수를 설정하세요}"
: "${DISCORD_WEBHOOK_URL:?DISCORD_WEBHOOK_URL 환경변수를 설정하세요}"

# alertmanager config에 discord 웹훅 URL 주입
sed "s#__DISCORD_WEBHOOK_URL__#${DISCORD_WEBHOOK_URL}#g" \
  monitoring/alertmanager/alertmanager.yml > /tmp/alertmanager-resolved.yml

echo "Alertmanager config 생성 완료: /tmp/alertmanager-resolved.yml"

# 모니터링 스택 기동
GRAFANA_PASSWORD="${GRAFANA_PASSWORD}" \
docker compose -f docker-compose.monitoring.yml up -d

echo "모니터링 스택 기동 완료"
echo "Prometheus: http://localhost:9090"
echo "Grafana:    http://localhost:3000 (admin / ${GRAFANA_PASSWORD})"
