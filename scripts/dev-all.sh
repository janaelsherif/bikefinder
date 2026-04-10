#!/usr/bin/env bash
# One-shot local stack prep: Docker DB + env for open system endpoints (no import token on localhost).
# Does NOT start long-running processes — run start-backend.sh and start-frontend.sh after this.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

if ! docker info >/dev/null 2>&1; then
  echo "Docker is not running. Open Docker Desktop and retry."
  exit 1
fi

echo "==> Starting Postgres + Redis"
docker compose up -d postgres redis

echo "==> Waiting for PostgreSQL..."
READY=0
for _ in $(seq 1 90); do
  if docker exec ebf-postgres pg_isready -U bikefinder -d bikefinder >/dev/null 2>&1; then
    READY=1
    break
  fi
  sleep 1
done
if [[ "$READY" -ne 1 ]]; then
  echo "Timed out. Check: docker compose logs postgres"
  exit 1
fi

echo "==> OK. Next (two terminals):"
echo ""
echo "  export EBF_DEV_OPEN_SYSTEM_ENDPOINTS=true   # crawl/import without X-Import-Token (localhost only)"
echo "  $ROOT/start-backend.sh"
echo ""
echo "  $ROOT/start-frontend.sh"
echo ""
echo "Then load live Rebike listings into Postgres (needs network, ~1–2 min):"
echo "  chmod +x $ROOT/scripts/load-real-data.sh   # once"
echo "  $ROOT/scripts/load-real-data.sh"
echo ""
echo "  (or without health wait: $ROOT/scripts/rebike-crawl-local.sh)"
echo ""
