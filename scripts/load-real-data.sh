#!/usr/bin/env bash
# Wait for the API, then POST /api/v1/system/crawl/rebike to import live Rebike.de listings into Postgres.
# Requires outbound HTTPS. Use with EBF_DEV_OPEN_SYSTEM_ENDPOINTS=true or X-Import-Token — see README.
set -euo pipefail

PORT="${SERVER_PORT:-8080}"
BASE="http://127.0.0.1:${PORT}"
HEALTH="${BASE}/actuator/health"
CRAWL="${BASE}/api/v1/system/crawl/rebike"

echo "==> Waiting for BikeFinder API at ${HEALTH} (up to 120s)..."
READY=0
for _ in $(seq 1 120); do
  if curl -sf --max-time 2 "${HEALTH}" 2>/dev/null | grep -q '"UP"'; then
    READY=1
    break
  fi
  sleep 1
done
if [[ "$READY" -ne 1 ]]; then
  echo "API not healthy. Start Postgres (docker compose up -d) then run start-backend.sh with JDK 21."
  exit 1
fi

echo "==> POST ${CRAWL}"
HDRS=(-H "Accept: application/json")
if [[ -n "${EBF_IMPORT_TOKEN:-}" ]]; then
  HDRS+=(-H "X-Import-Token: ${EBF_IMPORT_TOKEN}")
fi

TMP=$(mktemp)
trap 'rm -f "${TMP}"' EXIT

set +e
HTTP_CODE=$(curl -sS -o "${TMP}" -w "%{http_code}" -X POST "${CRAWL}" "${HDRS[@]}")
CURL_EC=$?
set -e
if [[ "${CURL_EC}" -ne 0 ]] || [[ -z "${HTTP_CODE}" ]]; then
  echo "curl failed (network or connection). Is the API up on port ${PORT}?"
  exit 1
fi

if [[ "${HTTP_CODE}" == "404" ]]; then
  echo "Endpoint not available. Set EBF_IMPORT_TOKEN in the API env, or for local dev:"
  echo "  export EBF_DEV_OPEN_SYSTEM_ENDPOINTS=true"
  echo "then restart the backend and run this script again."
  exit 1
fi

if [[ "${HTTP_CODE}" == "403" ]]; then
  echo "Forbidden. Use EBF_DEV_OPEN_SYSTEM_ENDPOINTS=true (localhost) or pass X-Import-Token (EBF_IMPORT_TOKEN)."
  exit 1
fi

if [[ "${HTTP_CODE}" != "200" ]]; then
  echo "Unexpected HTTP ${HTTP_CODE}"
  cat "${TMP}" 2>/dev/null || true
  exit 1
fi

if command -v jq >/dev/null 2>&1; then
  jq . "${TMP}"
else
  cat "${TMP}"
fi
echo ""

IMPORTED=""
if command -v jq >/dev/null 2>&1; then
  IMPORTED=$(jq -r '.imported // empty' "${TMP}" 2>/dev/null || echo "")
else
  IMPORTED=$(grep -o '"imported":[0-9]*' "${TMP}" 2>/dev/null | grep -o '[0-9]*$' || echo "")
fi
if [[ -n "${IMPORTED}" ]] && [[ "${IMPORTED}" != "null" ]]; then
  echo "==> Done. Refresh http://localhost:3000/de-CH (or /en) — you should see up to ${IMPORTED} new offers (non-demo)."
fi
echo "Tip: default search hides Flyway demo rows; crawled rows are real listings (is_demo=false)."
