#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

resolve_java_home() {
  if [[ -n "${JAVA_HOME:-}" ]] && [[ -x "${JAVA_HOME}/bin/java" ]]; then
    if "${JAVA_HOME}/bin/java" -version 2>&1 | grep -q 'version "21'; then
      return 0
    fi
  fi
  if [[ -x /usr/libexec/java_home ]]; then
    local mac21
    mac21="$(/usr/libexec/java_home -v 21 2>/dev/null || true)"
    if [[ -n "$mac21" ]] && [[ -x "$mac21/bin/java" ]]; then
      export JAVA_HOME="$mac21"
      return 0
    fi
  fi
  local candidate
  candidate="$(ls -d "$ROOT"/.jdks/jdk-21*/Contents/Home 2>/dev/null | head -1)"
  if [[ -n "$candidate" ]] && [[ -x "$candidate/bin/java" ]]; then
    export JAVA_HOME="$candidate"
    return 0
  fi
  return 1
}

if ! resolve_java_home; then
  echo "JDK 21 not found."
  echo "  • Install Temurin 21: https://adoptium.net/  (or: brew install temurin@21)"
  echo "  • Or unpack Temurin macOS aarch64 under: ${ROOT}/.jdks/"
  exit 1
fi
cd "$(dirname "$0")"
exec ./mvnw "$@"
