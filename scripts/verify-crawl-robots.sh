#!/usr/bin/env bash
# Fetch robots.txt for each Shopify crawl host and show Allow/Disallow lines (quick check vs our crawler).
set -euo pipefail

HOSTS=(
  rebike.com
  www.rebike.de
  www.upway.de
  upway.co
  www.upway.nl
  upway.ch
  likedbikes.com
  rebike.ch
  www.bibibike.ch
  www.veloplus.ch
  velocorner.ch
)

echo "==> robots.txt snapshot (User-agent * lines)"
for h in "${HOSTS[@]}"; do
  url="https://${h}/robots.txt"
  echo ""
  echo "--- ${url} ---"
  code=$(curl -sS -o /tmp/ebf_robots.txt -w "%{http_code}" --max-time 15 "$url" || echo "000")
  if [[ "${code}" != "200" ]]; then
    echo "HTTP ${code}"
    continue
  fi
  grep -E '^(User-agent:|Disallow:|Allow:)' /tmp/ebf_robots.txt | head -40 || true
done
rm -f /tmp/ebf_robots.txt 2>/dev/null || true
echo ""
echo "Tip: compare with RobotsAllowService and docs/CRAWL_RUNBOOK.md."
