#!/usr/bin/env bash
# Same checks as .github/workflows/ci.yml — run before push.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT/backend"
./mvnw -q test
cd "$ROOT/frontend"
npm ci
npm run build
