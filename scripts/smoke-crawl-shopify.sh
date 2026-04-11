#!/usr/bin/env bash
# Wait for API health, then POST /api/v1/system/crawl/shopify-all (all configured Shopify sources).
# Same auth as load-real-data.sh: EBF_DEV_OPEN_SYSTEM_ENDPOINTS=true or X-Import-Token.
set -euo pipefail

PORT="${SERVER_PORT:-8080}"
BASE="http://127.0.0.1:${PORT}"
HEALTH="${BASE}/actuator/health"
CRAWL="${BASE}/api/v1/system/crawl/shopify-all"

echo "==> Waiting for API at ${HEALTH} (up to 120s)..."
READY=0
for _ in $(seq 1 120); do
  if curl -sf --max-time 2 "${HEALTH}" 2>/dev/null | grep -q '"UP"'; then
    READY=1
    break
  fi
  sleep 1
done
if [[ "$READY" -ne 1 ]]; then
  echo "API not healthy. Start Postgres, then the backend."
  exit 1
fi

HDRS=(-H "Accept: application/json")
if [[ -n "${EBF_IMPORT_TOKEN:-}" ]]; then
  HDRS+=(-H "X-Import-Token: ${EBF_IMPORT_TOKEN}")
fi

echo "==> POST ${CRAWL}"
HTTP_CODE=$(curl -sS -o /tmp/ebf_shopify_all.json -w "%{http_code}" -X POST "${CRAWL}" "${HDRS[@]}")
if [[ "${HTTP_CODE}" == "404" ]]; then
  echo "Endpoint unavailable. Set EBF_IMPORT_TOKEN or EBF_DEV_OPEN_SYSTEM_ENDPOINTS=true on the API."
  exit 1
fi
if [[ "${HTTP_CODE}" == "403" ]]; then
  echo "Forbidden — use dev token or localhost dev-open flag."
  exit 1
fi
if [[ "${HTTP_CODE}" != "200" ]]; then
  echo "HTTP ${HTTP_CODE}"
  cat /tmp/ebf_shopify_all.json 2>/dev/null || true
  exit 1
fi

if command -v jq >/dev/null 2>&1; then
  jq . /tmp/ebf_shopify_all.json
else
  cat /tmp/ebf_shopify_all.json
fi
rm -f /tmp/ebf_shopify_all.json
