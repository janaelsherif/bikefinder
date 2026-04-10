# Deploying EuropeBikeFinder (GitHub + Vercel + API)

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

1. Build a JAR: from `backend/`, run `./mvnw -DskipTests package` (or use CI).
2. Run Java **21** with the JAR, or build the image from `backend/`:

   ` ./mvnw -DskipTests package && docker build -t bikefinder-api .`
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
| `EBF_PRICESENSE_LIVE` | Set to `true` only if you want **PriceSense** to hit competitor sites on each request (see `ebf.pricesense.live-competitor-search` in `application.yml`). Default **`false`**. Confirm **robots.txt** / terms and API load before enabling. |

**PriceSense live tuning (optional, API only)**

| Variable | Purpose |
|----------|---------|
| `EBF_PRICESENSE_LIVE_TIMEOUT` | Per-target probe timeout (seconds). |
| `EBF_PRICESENSE_LIVE_MIN_OK` | Minimum successful live CHF prices before using live median (else DB fallback). |
| `EBF_PRICESENSE_LIVE_DELAY_MS` | Delay between HTTP steps inside a probe (politeness). |

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
| `STAFF_UI_PASSWORD` | Optional; gates `/suche` and `/competitive-pricing` in the browser |

`NEXT_PUBLIC_*` is embedded at **build time**. After changing it, **redeploy** on Vercel.

6. Add your **Vercel production URL** (and preview URL if needed) to the backend **`EBF_CORS_ORIGINS`** so browser calls to the API are allowed.

---

## 5. Optional: scheduled jobs on the API host

If you use **Rebike crawl**, **competitor watch**, **FX**, **mail digest**, the Spring process must stay **always on** (not serverless). A small VM or Render/Railway **web service** is appropriate.

---

## 6. Checklist (“nothing off” for class demo)

- [ ] API health returns UP.
- [ ] `NEXT_PUBLIC_API_BASE_URL` points to that API on **Production** Vercel env.
- [ ] `EBF_CORS_ORIGINS` includes your Vercel URL(s).
- [ ] DB reachable from API; Flyway completed.
- [ ] Open `https://YOUR-VERCEL-APP.vercel.app/de-CH` — listings load if DB has data (run Rebike crawl or import on the API side).
- [ ] No dev-only flags enabled in production (`EBF_DEV_OPEN_SYSTEM_ENDPOINTS`, etc.).
- [ ] If using **live PriceSense** (`EBF_PRICESENSE_LIVE=true`), you accepted load/legal implications; otherwise leave it **`false`**.

---

## 7. Repo layout reminder

| Path | Deployed where |
|------|----------------|
| `frontend/` | **Vercel** |
| `backend/` | **Your Java host** + PostgreSQL |

If the project path on disk contains **spaces** (e.g. `fl p2`), clone the repo to a path **without spaces** on machines where you build locally — avoids occasional Node/Webpack issues.
