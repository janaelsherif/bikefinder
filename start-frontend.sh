#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "${ROOT}/frontend"

if [[ ! -f .env.local ]] && [[ -f .env.local.example ]]; then
  cp .env.local.example .env.local
  echo "Created frontend/.env.local from .env.local.example"
fi

if [[ ! -d node_modules ]]; then
  npm install
fi

exec npm run dev
