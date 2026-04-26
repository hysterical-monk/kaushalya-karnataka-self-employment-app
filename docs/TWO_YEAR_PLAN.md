# Kaushalya-Karnataka — Two-Year Plan

**Project:** Project 92 · Self-Employment · Android
**Document:** Two-Year Phased Plan v1.0
**Owner:** Kaushalya-Karnataka team
**Last updated:** 2026-04-25

---

## Vision

Turn skilled blue-collar workers in Karnataka's small towns into discoverable micro-entrepreneurs through a hyper-local "Blue-Collar Portfolio" Android app, building a Trust Economy via neighbour ratings.

## North-star metrics

| Metric                            | Year 1 target | Year 2 target |
| :-------------------------------- | :------------ | :------------ |
| Active workers on platform        | 5,000         | 50,000        |
| Verified hire-requests / month    | 10,000        | 200,000       |
| Average worker rating (≥ 5 reviews)| 4.2           | 4.4           |
| Towns / districts covered         | 10            | 50            |
| Languages supported               | 2 (en, kn)    | 5 (+ ta, te, hi) |

---

## Phase overview

| Phase | Window         | Theme                          | Exit signal                                          |
| :---- | :------------- | :----------------------------- | :--------------------------------------------------- |
| 1     | Months 1–3     | MVP                            | Signed APK on Play **internal testing**              |
| 2     | Months 4–6     | Trust & Polish                 | Closed beta with **50 real workers**, 1 town         |
| 3     | Months 7–10    | Discovery & Growth             | **Open beta**, 5,000 installs                        |
| 4     | Months 11–14   | Productionization              | **Production launch** with KYC and in-app messaging  |
| 5     | Months 15–20   | Monetization & Scale           | First **paying subscriber**, 3 new languages live    |
| 6     | Months 21–24   | Platform & Ecosystem           | Public **partner API**, iOS app GA                   |

---

## Phase 1 — MVP (Months 1–3)

**Goal:** ship a working v1.0 to Play Store internal testing.

### Scope
- Project scaffold: Kotlin 2.1, Compose Material 3, Hilt, Coroutines + Flow.
- Phone OTP auth + role selection (Worker / Customer).
- Worker: profile, service cards (CRUD), portfolio photo upload, availability toggle.
- Customer: browse, category filter, search, worker detail, post review, bookmark.
- Hire Me → `hire_requests` doc → Cloud Function → FCM push to worker.
- Cloud Function `onReviewWritten` for rating recomputation.
- Kannada + English (full string parity), runtime language switch.
- Firestore + Storage rules, Play Integrity App Check.
- Crashlytics + basic Analytics events.

### Team
- 1 Android engineer
- 1 Backend / Functions engineer (50%)
- 1 PM / designer (50%)

### Deliverables
- [x] Code scaffold pushed
- [ ] `google-services.json` configured for staging Firebase project
- [ ] Signed AAB uploaded to Play internal track
- [ ] 5 internal testers complete a full hire flow

### Exit criteria
- End-to-end flow (Worker signs up → Customer hires → Review → Rating updates) passes on a physical device.
- App size < 15 MB.
- Cold start < 2.5s on a Redmi 9.
- Signed APK published to Play internal track.

### Risks
- **Phone OTP cost** at scale — track per-SMS spend from week 1.
- **Firestore write fan-out** when reviews ramp — confirm `onReviewWritten` runs < 1s p95.

---

## Phase 2 — Trust & Polish (Months 4–6)

**Goal:** harden trust signals and run a real-world closed beta.

### Scope
- Multi-photo portfolio gallery: captions, reorder, delete with confirmation.
- "Verified Work" badge: workers submit before/after photo pairs; ops moderator reviews via a simple Firebase admin panel.
- Cloud Function image moderation hook (Vision API SafeSearch).
- Unified empty / loading / error states across all screens.
- Offline read cache via Firestore persistence (already enabled); add visible "Offline — showing cached data" banner.
- Accessibility pass: TalkBack labels, contrast (≥ 4.5:1), font scaling, RTL safety.
- Performance: Baseline Profiles, startup < 1.5s on Redmi 9, jank-free scrolling.
- Closed beta with 50 real workers in **one** Karnataka town (Mysuru recommended).

### Team
- 1 Android engineer
- 1 Backend engineer (50%)
- 1 ops moderator (PT)
- 1 PM / designer

### Deliverables
- Verified-work moderation flow (manual)
- Portfolio v2 (multi-photo, reorder)
- Beta test report with NPS, top 5 complaints

### Exit criteria
- 50 workers onboarded, ≥ 30 with at least 1 portfolio photo.
- 100+ hire requests created during beta.
- NPS ≥ 30 from beta cohort.

### Risks
- Spam reviews; introduce "report review" affordance; manual moderation queue.
- Image storage costs — enforce 1280px / WebP / < 200KB client-side.

---

## Phase 3 — Discovery & Growth (Months 7–10)

**Goal:** open beta and sustainable user acquisition.

### Scope
- Town / locality scoping (manual locality picker — no GPS yet).
- Push-notification campaigns aligned with festival / monsoon demand spikes.
- Deep links + Play Install Referrer → "share my profile" link for workers.
- Worker dashboard analytics: profile views, hire-request conversion rate, top-clicked services.
- App size optimization: R8 full mode, vector drawables only, strip unused locales.
- Open beta on Play Store; target **5,000 installs**.

### Team
- 1 Android engineer
- 1 Backend engineer
- 1 growth / marketing partner
- 1 PM / designer

### Deliverables
- Locality picker live in 10 Karnataka towns
- Worker analytics dashboard inside the app
- 3 push-notification campaigns shipped

### Exit criteria
- 5,000 installs.
- Day-7 retention ≥ 25%.
- 30%+ of workers have ≥ 3 reviews.

### Risks
- Discovery quality drops at scale — Phase 4 must invest in ranking.
- Notification fatigue — cap to 2 / week per user.

---

## Phase 4 — Productionization (Months 11–14)

**Goal:** production-grade launch with trust and communication.

### Scope
- **Production launch** on Play Store (open).
- Multi-region Firestore + cost monitoring dashboard (Firestore reads / Cloud Function invocations / Storage egress).
- Worker KYC: Aadhaar last-4 + selfie, verified by ops team (manual flow first; no live API integration in this phase).
- In-app messaging (Firestore-backed chat between customer and worker — no calls yet).
- Customer-side rating prompts after `hire_requests` move to `completed`.
- A/B testing infrastructure via Firebase Remote Config.

### Team
- 2 Android engineers
- 1 Backend engineer
- 1 Ops / KYC reviewer (PT)
- 1 PM / designer

### Deliverables
- KYC submission + review pipeline
- Chat (1:1) per hire request
- Remote-config-driven feature flags

### Exit criteria
- 100% of new workers complete KYC submission flow.
- ≥ 60% of completed hires lead to a posted review.
- 0 critical Crashlytics issues for 14 consecutive days.

### Risks
- KYC privacy concerns — store images encrypted; auto-delete after verification.
- Chat moderation — Phase 5 to add automated text moderation (Perspective API).

---

## Phase 5 — Monetization & Scale (Months 15–20)

**Goal:** turn on revenue and broaden the language footprint.

### Scope
- **Worker subscription tier** (priority placement, larger portfolio cap, no commission per job). Billing via Google Play Billing or UPI auto-pay.
- **UPI deep-link payments** for one-off jobs (Google Pay / PhonePe intent — no in-app wallet, sidesteps Play Billing).
- **Job-board mode**: customers post a need, workers apply.
- **Tamil + Telugu + Hindi** localization to expand beyond Karnataka.
- Backend hardening: BigQuery export from Firestore, dashboards in Looker Studio.
- Automated text moderation (Perspective API) on reviews and chat.

### Team
- 2 Android engineers
- 1 Backend engineer
- 1 Data analyst (PT)
- 1 PM / designer

### Deliverables
- First paying subscriber tier
- Localized app in 5 languages
- Job-board MVP

### Exit criteria
- ≥ 500 paying workers.
- Tamil / Telugu / Hindi each ≥ 5,000 installs.
- BigQuery dashboards covering DAU, retention, hire conversion.

### Risks
- Play policy on real-money flows — confirm UPI deep-link path before launch.
- Subscription churn — measure monthly cohort retention from week 1.

---

## Phase 6 — Platform & Ecosystem (Months 21–24)

**Goal:** become a platform partners build on.

### Scope
- **iOS app** (decision tree: Compose Multiplatform vs. native SwiftUI — re-evaluate at Phase 5 retro).
- **Public partner API** for NGOs and government skill-development programs (e.g. Karnataka Skill Development Corp).
- **Worker training content**: short-form videos, partnered curriculum.
- Geo-features re-evaluated (privacy-respecting locality, not live GPS).
- Annual impact report: jobs facilitated, revenue earned by workers, ratings distribution, gender / age cuts.

### Team
- 2 Android + 1 iOS (or 2 KMP) engineers
- 1 Backend engineer
- 1 Partnerships / BD lead
- 1 PM / designer

### Deliverables
- iOS app on App Store
- Public API + developer docs
- First partner integration live (NGO or govt program)

### Exit criteria
- iOS MAU ≥ 10% of Android MAU.
- ≥ 2 partner integrations live.
- Year-2 impact report published.

### Risks
- iOS engineering lag — staff before Phase 5 ends.
- API abuse — enforce per-partner rate limits + signed JWT auth from day 1.

---

## Cross-cutting workstreams

These run across every phase, not in any single one.

### Quality
- Unit tests on use cases and ViewModels (already started in Phase 1).
- Compose UI tests against Firebase Emulator (added in Phase 2).
- Crashlytics SLO: crash-free sessions ≥ 99.5%.
- Performance SLO: cold start < 1.5s p75 on a Redmi 9 from Phase 2 onward.

### Security & privacy
- App Check (Play Integrity) from Phase 1.
- Phone-number minimization: never expose raw `phone` in worker docs read by other users.
- KYC data encrypted at rest from Phase 4; ops access logged.
- Annual security review starting Phase 4.

### Cost control
- Phase 1: track Firestore reads, Storage egress, FCM, Auth SMS spend weekly.
- Phase 3: cost-per-install dashboard.
- Phase 5: switch heavy aggregation reads to BigQuery exports to reduce Firestore reads.

### Localization
- Phase 1: en + kn (full parity).
- Phase 5: ta, te, hi.
- Translations professionally reviewed, not machine-only.

### Accessibility
- Phase 2: full TalkBack pass.
- Phase 4: re-audit after KYC and chat ship.
- Phase 6: WCAG 2.2 AA target.

---

## Decision log

| Date       | Decision                                           | Why                                                           |
| :--------- | :------------------------------------------------- | :------------------------------------------------------------ |
| 2026-04-25 | Tech stack: Kotlin + Jetpack Compose               | Modern, Google-recommended, best long-term hireability        |
| 2026-04-25 | Backend: Firebase-only for v1                      | Fastest to ship, real-time built-in, matches PRD              |
| 2026-04-25 | Auth: Phone OTP only                               | Best fit for blue-collar audience; emails are uncommon        |
| 2026-04-25 | Pull 4 "Good-to-Have" features into v1.0           | Kannada toggle + FCM + availability + bookmarks materially raise the v1 bar without significant cost |
| 2026-04-25 | Defer GPS / live tracking past v1.0                | Privacy concerns + minimal value vs. manual locality picker   |
| 2026-04-25 | Defer iOS to Phase 6                               | Android-first is correct for target audience; iOS is a follow |

---

## Appendix — Phase ↔ Feature Matrix

| Feature                                    | P1 | P2 | P3 | P4 | P5 | P6 |
| :----------------------------------------- | :- | :- | :- | :- | :- | :- |
| Phone OTP auth                             | ✅ |    |    |    |    |    |
| Worker profile + service cards             | ✅ |    |    |    |    |    |
| Portfolio (single photo)                   | ✅ |    |    |    |    |    |
| Portfolio (multi-photo + reorder)          |    | ✅ |    |    |    |    |
| Verified-work badge                        |    | ✅ |    |    |    |    |
| Browse + category filter                   | ✅ |    |    |    |    |    |
| Locality picker                            |    |    | ✅ |    |    |    |
| Hire Me + FCM                              | ✅ |    |    |    |    |    |
| Review wall + rating                       | ✅ |    |    |    |    |    |
| Bookmarks                                  | ✅ |    |    |    |    |    |
| Kannada + English                          | ✅ |    |    |    |    |    |
| Tamil / Telugu / Hindi                     |    |    |    |    | ✅ |    |
| Crashlytics + Analytics                    | ✅ |    |    |    |    |    |
| App Check (Play Integrity)                 | ✅ |    |    |    |    |    |
| Offline persistence + UX                   |    | ✅ |    |    |    |    |
| Accessibility (TalkBack, contrast)         |    | ✅ |    |    |    |    |
| Baseline Profiles                          |    | ✅ |    |    |    |    |
| Worker analytics dashboard                 |    |    | ✅ |    |    |    |
| Push campaigns                             |    |    | ✅ |    |    |    |
| KYC                                        |    |    |    | ✅ |    |    |
| In-app messaging                           |    |    |    | ✅ |    |    |
| Remote Config + A/B                        |    |    |    | ✅ |    |    |
| Worker subscription                        |    |    |    |    | ✅ |    |
| UPI deep-link payments                     |    |    |    |    | ✅ |    |
| Job-board mode                             |    |    |    |    | ✅ |    |
| Automated text moderation                  |    |    |    |    | ✅ |    |
| BigQuery export + dashboards               |    |    |    |    | ✅ |    |
| iOS app                                    |    |    |    |    |    | ✅ |
| Public partner API                         |    |    |    |    |    | ✅ |
| Worker training content                    |    |    |    |    |    | ✅ |
| Annual impact report                       |    |    |    |    |    | ✅ |
