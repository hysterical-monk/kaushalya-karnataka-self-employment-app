# Seed demo data

Pushes 10 realistic Karnataka workers (electrician, plumber, tailor, carpenter, painter, gardener, mechanic, mason, AC repair, driver) with services, portfolio photos, and reviews into Firestore. Photos are Unsplash URLs — no Storage upload required.

## One-time setup

1. **Get a service account key**
   - https://console.firebase.google.com → your project → ⚙ Settings → **Service accounts**
   - Click **Generate new private key** → confirm → JSON downloads
   - Save it as `tools/seed/service-account.json` (this file is gitignored — never commit it)

2. **Install deps**
   ```bash
   cd tools/seed
   npm install
   ```

## Run

```bash
# from tools/seed/
npm run seed              # idempotent — safe to re-run, overwrites existing demo_w_* docs
npm run seed:clean        # wipe existing demo_w_* docs first, then re-seed
```

## What it creates

10 workers with doc IDs like `demo_w_ravi_kumar`. Each has:
- profile fields (name, bio, town, locality, categories, availability, rating)
- 1–4 services in `workers/{id}/services/`
- 0–3 portfolio entries in `workers/{id}/portfolio/`
- 1–3 reviews in `workers/{id}/reviews/`
- `isDemo: true` flag so you can filter or wipe later

## To remove all demo data

```bash
npm run seed:clean
# Then ctrl+C right after the "Cleaning..." line, OR let it re-seed and run npm run seed:clean again
```

Or simpler — delete every doc whose id starts with `demo_w_` directly in the Firebase Console.
