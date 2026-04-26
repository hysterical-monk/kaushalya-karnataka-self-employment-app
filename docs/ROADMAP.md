# Two-Year Roadmap

A six-phase plan from MVP to platform.

---

## Phase 1 — MVP (Months 1–3)
**Goal: ship a working v1.0 to Play Store internal testing.**

- [x] Project scaffold, Hilt DI, Compose theme, navigation graph
- [x] Phone OTP auth + role selection (Worker / Customer)
- [x] Worker: profile, service cards (CRUD), portfolio photo upload, availability toggle
- [x] Customer: browse, category filter, worker detail, post review, bookmark
- [x] Hire Me → `hire_requests` doc → Cloud Function → FCM push
- [x] Cloud Function for rating recomputation
- [x] Kannada + English (full string parity)
- [x] Firestore + Storage rules, App Check
- [x] Crashlytics + basic Analytics events
- [ ] **Exit criteria**: end-to-end flows pass on a physical device; signed APK published to Play internal track.

## Phase 2 — Trust & Polish (Months 4–6)
- Multi-photo gallery, captions, reorder for portfolio
- Verified-work badge: workers submit "before/after" pairs reviewed by an ops moderator
- Cloud Function image moderation hook (Vision API SafeSearch)
- Unified empty / loading / error states; offline read cache via Firestore persistence
- Accessibility pass: TalkBack labels, contrast checks, font scaling
- Performance baseline: Baseline Profiles, startup < 1.5s on a Redmi 9
- Closed beta with 50 real workers in 1 town

## Phase 3 — Discovery & Growth (Months 7–10)
- Town / locality scoping (manual locality picker — no GPS)
- Push-notification campaigns aligned with festival demand spikes
- Deep links + Play Install Referrer for word-of-mouth growth
- Worker dashboard analytics: profile views, hire-request conversion
- App size optimization (R8 full mode, vector drawables only)
- Open beta on Play Store; target 5,000 installs

## Phase 4 — Productionization (Months 11–14)
- Production launch on Play Store
- Multi-region Firestore + cost monitoring
- Worker KYC: Aadhaar last-4 + selfie, verified by ops team (manual flow first)
- In-app messaging (Firestore-backed chat — no calls yet)
- Customer-side rating prompts after `hire_requests` move to `completed`
- A/B testing via Firebase Remote Config

## Phase 5 — Monetization & Scale (Months 15–20)
- Subscription tier for workers (priority placement, no commission on jobs)
- UPI deep-link payments (Google Pay / PhonePe intent — no in-app wallet)
- Job-board mode: customers post a need, workers apply
- Tamil + Telugu + Hindi localization to expand beyond Karnataka
- Backend hardening: BigQuery export from Firestore, Looker Studio dashboards

## Phase 6 — Platform & Ecosystem (Months 21–24)
- iOS app (Compose Multiplatform vs. native SwiftUI — decision deferred to Phase 5 retro)
- Public API for partner NGOs / government skill-development programs
- Worker training content (videos), partnered with Karnataka Skill Development Corp.
- Geo-features re-evaluated (privacy-respecting locality, not live GPS)
- Annual impact report: jobs facilitated, revenue earned by workers, ratings distribution

---

## Risks & open questions (parking lot)

- **Phone OTP cost** at scale: Firebase Auth charges per SMS in India after free tier. Phase 4 should evaluate moving to MSG91 or in-app reCAPTCHA-only flow for repeat sign-ins.
- **Image storage cost**: portfolio photos can balloon. Compress to 1280px max, WebP, < 200KB each, enforced client-side and re-checked in a Cloud Function.
- **Moderation**: a Review Wall with no moderation will attract spam. Phase 2 introduces a manual report flow; Phase 4 introduces automated text moderation (Perspective API).
- **Offline-first**: small-town connectivity is patchy. Firestore offline persistence is enabled in Phase 1, but write conflict UX needs explicit design in Phase 2.
- **Play Store policy** on real money (Phase 5): UPI deep-link path sidesteps Play Billing but must be reconfirmed at launch time.
