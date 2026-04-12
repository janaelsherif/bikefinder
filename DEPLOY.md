# Deploying EuropeBikeFinder (GitHub + Vercel + API)

**Scope & roadmap (keep updated):** **[docs/SCOPE_AND_PHASE2.md](./docs/SCOPE_AND_PHASE2.md)**

This repo is a **monorepo**: **Next.js** (`frontend/`) + **Spring Boot** (`backend/`).  
**Vercel only runs the Next.js app.** The API and PostgreSQL must run somewhere else (Render, Railway, Fly.io, a VPS, or your college’s server).

---

## 1. Before you push to GitHub

- **Repository root** must be this **monorepo folder** (`europe-bikefinder/`), not your user home directory. From that folder, `git status` should list only project files. If `git` seems to track your entire home folder, run **`git init`** inside **`europe-bikefinder`** so this directory has its own `.git`, then add/commit from there.
- **Do not commit secrets.** These stay out of git (already ignored): `frontend/.env*.local`, `.env`, passwords, API tokens.
- Copy env templates:
  - `frontend/.env.production.example` → set values in **Vercel Project → Environment Variables** (not in the repo).
  - Backend: set variables in your **hosting provider’s** dashboard (see below).

---

## 2. PostgreSQL (required for the API)

Provision **PostgreSQL 14+** (Neon, Supabase, Railway Postgres, AWS RDS, etc.). Note:

- **Host, port, database name, user, password**
- Allow inbound connections from your **API host** (not from Vercel — the DB is only used by Spring Boot).

---

## 3. Deploy the Spring Boot API

1. Build a JAR: from `backend/`, run `./mvnw -DskipTests package` (or CI / Docker — see **§7**).
2. Run Java **21** with the JAR, or run the **Docker** image (multi-stage build needs no local Maven).
3. Set **at minimum**:

| Variable | Purpose |
|----------|---------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://HOST:5432/DBNAME` |
| `SPRING_DATASOURCE_USERNAME` | DB user |
| `SPRING_DATASOURCE_PASSWORD` | DB password |
| `EBF_CORS_ORIGINS` | **Your Vercel URL(s)**, comma-separated, **https**, no trailing slash. Example: `https://your-app.vercel.app` |
| `SERVER_PORT` | Often `8080` (platform default) |

**Recommended for production**

| Variable | Purpose |
|----------|---------|
| `EBF_STAFF_API_TOKEN` | Random secret; same value in Vercel as `EBF_STAFF_API_TOKEN` for server-side API calls |
| `EBF_IMPORT_TOKEN` | Protects import/crawl system endpoints |
| `EBF_MAIL_ENABLED`, `SPRING_MAIL_*` | If you use email alerts |
| `EBF_COMPETITOR_WATCH_ENABLED` | Set `true` if you want **daily** competitor snapshots (default schedule **08:00 Europe/Zurich**; override with `EBF_COMPETITOR_WATCH_CRON` if needed) |
| `EBF_CRAWL_ENABLED` | Set `true` if you want the **scheduled marketplace crawl** (see `application.yml` for timing) |

**Optional — AI competitor brief (Module 5, no Telegram)**

Not required for launch. The app deploys fine without these; the **Generate brief** button on **`/competitor-watch`** stays disabled until the API has a Claude key.

| Variable | Purpose |
|----------|---------|
| `ANTHROPIC_API_KEY` | **Required** for `POST /api/v1/competitor-watch/brief` and the UI brief. Set on the **API only** (not Vercel). |
| `ANTHROPIC_MODEL` | Optional override; default in `application.yml` is `claude-3-5-sonnet-20241022`. |
| `PERPLEXITY_API_KEY` | Optional; adds web-grounded Swiss market context before Claude. |
| `PERPLEXITY_MODEL` | Optional; default `sonar`. |

After setting keys, **restart the API**. Keys are documented in repo root `.env.example` (`ebf.llm` in `application.yml`).

**Never enable in production**

- `EBF_DEV_OPEN_SYSTEM_ENDPOINTS=true` (localhost-only dev helper)

4. **Health check:** `GET https://YOUR-API-HOST/actuator/health` → `{"status":"UP"}`  
5. **Flyway** runs on startup; first boot applies migrations (including seeds).

---

## 4. Deploy the frontend on Vercel

1. **New Project** → Import the GitHub repo.
2. **Root Directory:** `frontend`  
3. **Framework Preset:** Next.js (auto).
4. **Build:** default `npm run build`; **Output:** Next.js default.
5. **Environment variables** (Production + Preview if you want previews to hit a staging API):

| Name | Example |
|------|---------|
| `NEXT_PUBLIC_API_BASE_URL` | `https://your-api.onrender.com` (public URL of Spring Boot, **no** trailing slash) |
| `EBF_STAFF_API_TOKEN` | Same as backend `EBF_STAFF_API_TOKEN` if you use staff-protected APIs |
| `STAFF_UI_PASSWORD` | Optional shared password for the staff UI. When set, the browser gate applies to **all locale routes** except **`/…/staff-login`** (sign-in first). Use a **strong** secret in production (not the demo value from `.env.local.example`). |

`NEXT_PUBLIC_*` is embedded at **build time**. After changing it, **redeploy** on Vercel.

6. Add your **Vercel production URL** (and preview URL if needed) to the backend **`EBF_CORS_ORIGINS`** so browser calls to the API are allowed.

---

## 5. Scheduled jobs (API host must stay running)

**FX, crawls, competitor watch** all run **inside Spring Boot**. The Vercel site does **not** run these. Use a **long-running** API deployment (VM, Render/Railway/Fly **web service**, etc.).

- **Competitor Watch:** set `EBF_COMPETITOR_WATCH_ENABLED=true` on the API. Default: **daily 08:00** `Europe/Zurich` (cron in `application.yml`). First snapshots appear after the first run; optional manual trigger: `POST /api/v1/system/competitor-watch/run` (same auth rules as other system endpoints — see README).
- **AI brief (Claude / Perplexity):** optional; see **§3** table *Optional — AI competitor brief*. Snapshots work without LLM keys; brief generation needs `ANTHROPIC_API_KEY` on the API.
- **Crawls:** optional; enable with `EBF_CRAWL_ENABLED` and tokens as documented in README / `docs/CRAWL_RUNBOOK.md`.

---

## 6. Checklist before you call it done

- [ ] API health returns UP (`/actuator/health`).
- [ ] `NEXT_PUBLIC_API_BASE_URL` points to that API on **Production** Vercel env (redeploy after changing it).
- [ ] `EBF_CORS_ORIGINS` includes your Vercel URL(s).
- [ ] DB reachable from API; Flyway completed on first boot.
- [ ] If you use staff features: `EBF_STAFF_API_TOKEN` matches between API and Vercel; optional `STAFF_UI_PASSWORD` set if you want the browser gate.
- [ ] Open `https://YOUR-VERCEL-APP.vercel.app/en` (or `/de-CH`) — listings appear once the DB has data (run an import/crawl from the API side if empty).
- [ ] If you want **Competitor Watch** live: `EBF_COMPETITOR_WATCH_ENABLED=true` on the API (see §5).
- [ ] **(Optional)** If Patrick wants the **AI competitor brief** on **`/competitor-watch`**: set `ANTHROPIC_API_KEY` (and optionally `PERPLEXITY_API_KEY`) on the **API** host, then restart the API. Skip on first deploy if you are not ready — everything else works without these keys.
- [ ] No dev-only flags in production (`EBF_DEV_OPEN_SYSTEM_ENDPOINTS`, etc.).
- [ ] Read **[docs/SCOPE_AND_PHASE2.md](./docs/SCOPE_AND_PHASE2.md)** so scope and Phase 2 expectations are clear.

**Local parity with CI:** from repo root, `./scripts/verify-build.sh` (same as GitHub Actions).

---

## 7. Docker image (build without local JDK)

From repo root, multi-stage **`backend/Dockerfile`** builds the JAR inside Docker:

```bash
docker build -f backend/Dockerfile -t bikefinder-api:latest backend
```

Run (set `SPRING_DATASOURCE_*` and `EBF_CORS_ORIGINS`):

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:5432/bikefinder \
  -e SPRING_DATASOURCE_USERNAME=… -e SPRING_DATASOURCE_PASSWORD=… \
  -e EBF_CORS_ORIGINS=https://your-app.vercel.app \
  bikefinder-api:latest
```

See **`infra/render.yaml`** for a Render.com blueprint (Web Service + env placeholders).

---

## 8. Repo layout reminder

| Path | Deployed where |
|------|----------------|
| `frontend/` | **Vercel** |
| `backend/` | **Your Java host** + PostgreSQL |

If the project path on disk contains **spaces** (e.g. `fl p2`), clone the repo to a path **without spaces** on machines where you build locally — avoids occasional Node/Webpack issues.
