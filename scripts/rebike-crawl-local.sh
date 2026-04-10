#!/usr/bin/env bash
# POST /api/v1/system/crawl/rebike — works when API runs with EBF_DEV_OPEN_SYSTEM_ENDPOINTS=true or valid X-Import-Token.
set -euo pipefail
PORT="${SERVER_PORT:-8080}"
URL="http://127.0.0.1:${PORT}/api/v1/system/crawl/rebike"
echo "POST $URL"
if [[ -n "${EBF_IMPORT_TOKEN:-}" ]]; then
  curl -sS -X POST "$URL" -H "X-Import-Token: ${EBF_IMPORT_TOKEN}" -H "Accept: application/json" | tee /dev/stderr
else
  curl -sS -X POST "$URL" -H "Accept: application/json" | tee /dev/stderr
fi
echo ""
