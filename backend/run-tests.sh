#!/usr/bin/env bash
# Run unit tests (requires JDK 21 — same resolution as run-dev.sh).
set -euo pipefail
cd "$(dirname "$0")"
exec ./run-dev.sh test "$@"
