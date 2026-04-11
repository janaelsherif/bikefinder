#!/usr/bin/env bash
# POST /api/v1/system/crawl/marketplace-all — Shopify + heuristic + classified skips.
set -euo pipefail
PORT="${SERVER_PORT:-8080}"
BASE="http://127.0.0.1:${PORT}"
HEALTH="${BASE}/actuator/health"
URL="${BASE}/api/v1/system/crawl/marketplace-all"
echo "==> Waiting for ${HEALTH}..."
READY=0
for _ in $(seq 1 120); do
  if curl -sf --max-time 2 "${HEALTH}" 2>/dev/null | grep -q '"UP"'; then READY=1; break; fi
  sleep 1
done
[[ "$READY" -eq 1 ]] || { echo "API not up."; exit 1; }
HDRS=(-H "Accept: application/json")
[[ -n "${EBF_IMPORT_TOKEN:-}" ]] && HDRS+=(-H "X-Import-Token: ${EBF_IMPORT_TOKEN}")
HTTP=$(curl -sS -o /tmp/ebf_mp.json -w "%{http_code}" -X POST "${URL}" "${HDRS[@]}")
[[ "$HTTP" == "200" ]] || { echo "HTTP $HTTP"; cat /tmp/ebf_mp.json 2>/dev/null; exit 1; }
command -v jq >/dev/null && jq . /tmp/ebf_mp.json || cat /tmp/ebf_mp.json
rm -f /tmp/ebf_mp.json
