#!/usr/bin/env bash
# Starts Docker (Postgres + Redis), then Spring Boot — safe to run repeatedly.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

if ! docker info >/dev/null 2>&1; then
  echo "Docker is not running. Open Docker Desktop, wait until it is ready, then run this script again."
  exit 1
fi

echo "Starting Postgres and Redis..."
docker compose up -d postgres redis

echo "Waiting for PostgreSQL..."
READY=0
for _ in $(seq 1 90); do
  if docker exec ebf-postgres pg_isready -U bikefinder -d bikefinder >/dev/null 2>&1; then
    READY=1
    break
  fi
  sleep 1
done
if [[ "$READY" -ne 1 ]]; then
  echo "Timed out waiting for PostgreSQL. Check: docker compose logs postgres"
  exit 1
fi
echo "PostgreSQL is ready."

PORT="${SERVER_PORT:-8080}"

if curl -sf --max-time 2 "http://127.0.0.1:${PORT}/actuator/health" 2>/dev/null | grep -q '"UP"'; then
  echo "BikeFinder API is already running on port ${PORT} (health OK). Nothing to do."
  exit 0
fi

if command -v lsof >/dev/null 2>&1; then
  if lsof -nP -iTCP:"${PORT}" -sTCP:LISTEN >/dev/null 2>&1; then
    echo "Port ${PORT} is already used by another program (not this API)."
    echo "Stop that program, or run: SERVER_PORT=8081 ${ROOT}/start-backend.sh"
    exit 1
  fi
fi

cd "${ROOT}/backend"
chmod +x mvnw run-dev.sh 2>/dev/null || true
exec ./run-dev.sh spring-boot:run
