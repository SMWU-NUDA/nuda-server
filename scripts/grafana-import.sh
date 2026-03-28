#!/bin/bash
# Grafana에 대시보드를 grafana.com에서 가져와 import하는 스크립트
# 사용법: GRAFANA_PASSWORD=<비밀번호> bash scripts/grafana-import.sh

set -euo pipefail

GRAFANA_URL="${GRAFANA_URL:-http://localhost:3000}"
GRAFANA_USER="${GRAFANA_USER:-admin}"
GRAFANA_PASSWORD="${GRAFANA_PASSWORD:?GRAFANA_PASSWORD 환경변수를 설정하세요}"

# 대시보드 ID 목록
# 4701: JVM Micrometer (Micrometer + Prometheus)
# 19004: Spring Boot 3.x Statistics
DASHBOARD_IDS=(4701 19004)

wait_for_grafana() {
  echo "Grafana 기동 대기 중..."
  for i in $(seq 1 30); do
    if curl -sf "${GRAFANA_URL}/api/health" > /dev/null 2>&1; then
      echo "Grafana 준비 완료"
      return 0
    fi
    sleep 2
  done
  echo "Grafana가 60초 내에 기동되지 않았습니다." >&2
  exit 1
}

get_folder_uid() {
  local folder_name=$1
  curl -sf \
    -u "${GRAFANA_USER}:${GRAFANA_PASSWORD}" \
    "${GRAFANA_URL}/api/folders" \
    | jq -r ".[] | select(.title == \"${folder_name}\") | .uid"
}

import_dashboard() {
  local id=$1
  local folder_uid=$2
  echo "대시보드 ${id} import 중..."

  local json
  json=$(curl -sf "https://grafana.com/api/dashboards/${id}/revisions/latest/download")

  local payload
  payload=$(jq -n \
    --argjson dashboard "$json" \
    --arg folderUid "$folder_uid" \
    '{
      dashboard: $dashboard,
      overwrite: true,
      folderUid: $folderUid,
      inputs: [
        {
          name: "DS_PROMETHEUS",
          type: "datasource",
          pluginId: "prometheus",
          value: "Prometheus"
        }
      ]
    }')

  local result
  result=$(curl -sf -X POST \
    -H "Content-Type: application/json" \
    -u "${GRAFANA_USER}:${GRAFANA_PASSWORD}" \
    -d "$payload" \
    "${GRAFANA_URL}/api/dashboards/import")

  echo "대시보드 ${id} import 완료: $(echo "$result" | jq -r '.importedUrl // .message')"
}

wait_for_grafana

FOLDER_UID=$(get_folder_uid "Nuda")
if [ -z "$FOLDER_UID" ]; then
  echo "Nuda 폴더를 찾을 수 없습니다. General 폴더에 import합니다."
  FOLDER_UID=""
fi

for id in "${DASHBOARD_IDS[@]}"; do
  import_dashboard "$id" "$FOLDER_UID"
done

echo ""
echo "모든 대시보드 import 완료"
echo "Grafana: ${GRAFANA_URL}"