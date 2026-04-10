# Ship this repo to GitHub

1. **Use this folder as the repo root** (`europe-bikefinder/`). Do not use a Git repo whose root is your home directory.
2. From this directory:
   ```bash
   git init
   git add -A
   git status   # confirm no .env.local or secrets
   git commit -m "EuropeBikeFinder: API, frontend, PriceSense live probes, Flyway V15"
   ```
3. On GitHub: **New repository** → empty, no README (or merge if you added one).
4. **Connect and push:**
   ```bash
   git remote add origin https://github.com/YOUR_USER/YOUR_REPO.git
   git branch -M main
   git push -u origin main
   ```
5. Confirm **Actions** → **CI** passes (backend `mvn test`, frontend `npm ci && npm run build`).

See **[DEPLOY.md](../DEPLOY.md)** for Vercel + API environment variables.
