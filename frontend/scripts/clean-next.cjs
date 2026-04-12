#!/usr/bin/env node
/**
 * Remove Next.js + tooling caches.
 * Fixes: Cannot find module './682.js', './948.js', ./vendor-chunks/next-intl.js, spurious 404s.
 */
const fs = require("fs");
const path = require("path");
const root = path.join(__dirname, "..");

function rmIfExists(rel) {
  const p = path.join(root, rel);
  if (fs.existsSync(p)) {
    fs.rmSync(p, { recursive: true, force: true });
    console.log("Removed", rel);
  }
}

rmIfExists(".next");
rmIfExists("node_modules/.cache");
rmIfExists(".turbo");
