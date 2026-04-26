# Kaushalya-Karnataka

A hyper-local "Blue-Collar Portfolio" Android app that helps skilled local workers (electricians, plumbers, carpenters, painters, …) showcase their work, publish fixed price lists, and find customers in their neighbourhood — building a "Trust Economy" through neighbour ratings.

> Project 92 · Self-Employment · Android · Built with GenAI

---

## Highlights

- **Kotlin + Jetpack Compose** (Material 3) — modern, declarative UI.
- **Firebase backend** — Auth (Phone OTP), Firestore (real-time), Storage, Cloud Messaging, Cloud Functions.
- **Clean Architecture** — `data` / `domain` / `presentation` layers, MVVM, Hilt DI, Coroutines + Flow.
- **Bilingual** — English + ಕನ್ನಡ (full string parity, in-app language switch).
- **Production-grade extras** — App Check (Play Integrity), Crashlytics, Analytics, offline persistence, FCM push.

---

## Features (v1.0)

| Area     | What it does                                                                |
| :------- | :-------------------------------------------------------------------------- |
| Auth     | Phone OTP sign-in via Firebase Auth                                         |
| Worker   | Profile · editable Service Cards · Portfolio photo gallery · Availability toggle |
| Customer | Browse workers · Category filter · Search · Bookmark favourites             |
| Trust    | Star ratings · Text reviews · Thumbs Up · Rating recomputed by Cloud Function |
| Hire     | "Hire Me" → writes `hire_requests` doc → Cloud Function → FCM push to worker |
| L10n     | English + Kannada with runtime language switch                              |
| Backend  | Firestore security rules · Storage rules · Composite indexes                |

---

## Project structure

```
.
├── app/                                # Android app module (Kotlin + Compose)
│   ├── src/main/java/com/kaushalya/karnataka/
│   │   ├── core/                       # DI, theme, locale, shared components
│   │   ├── data/                       # Firebase repository implementations
│   │   ├── domain/                     # models, repository interfaces, use cases
│   │   └── presentation/               # screens (auth, customer, worker, settings)
│   └── src/main/res/                   # strings (en + kn), drawables, manifest
├── functions/                          # Firebase Cloud Functions (TypeScript)
│   └── src/
│       ├── onReviewWrite.ts            # recomputes worker averageRating
│       └── onHireRequestCreate.ts      # sends FCM on Hire Me
├── firestore.rules
├── firestore.indexes.json
├── storage.rules
├── firebase.json
├── .github/workflows/ci.yml
└── docs/
    ├── ARCHITECTURE.md
    ├── FIRESTORE_SCHEMA.md
    └── ROADMAP.md
```

---

## Tech stack

| Concern          | Choice                                                              |
| :--------------- | :------------------------------------------------------------------ |
| Language         | Kotlin 2.1                                                          |
| UI               | Jetpack Compose, Material 3, Compose Navigation                     |
| Architecture     | Clean Architecture (data / domain / presentation), MVVM             |
| DI               | Hilt 2.53 (KSP)                                                     |
| Async            | Coroutines + Flow / StateFlow                                       |
| Image loading    | Coil 3                                                              |
| Image picker     | `PickVisualMedia` Activity Result API                                |
| Backend          | Firebase: Auth · Firestore · Storage · Messaging · Functions · Crashlytics · Analytics · App Check |
| Functions runtime| Node 20 (TypeScript)                                                |
| Min SDK / Target | 24 / 35                                                             |
| Build            | Gradle 8.11.1 + AGP 8.7.3                                           |

---

## Setup

### 1. Prerequisites

- **Android Studio Ladybug** (2024.2+) or newer
- **JDK 17**
- **Node 20+** (for Firebase CLI and Cloud Functions)
- **Firebase CLI**: `npm i -g firebase-tools`

### 2. Firebase project setup

1. Create a project at https://console.firebase.google.com
2. Add an Android app with package id `com.kaushalya.karnataka`
3. Download `google-services.json` and drop it into `app/`
4. Enable: **Authentication → Phone**, **Firestore**, **Storage**, **Cloud Messaging**, **Functions** (requires Blaze pay-as-you-go plan for Functions)
5. Add your debug keystore SHA-1 to the Firebase Android app settings (required for Phone Auth):
   ```bash
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey \
           -storepass android -keypass android | grep SHA1
   ```
6. Update `.firebaserc` with your project id (replace `REPLACE_WITH_FIREBASE_PROJECT_ID`).

> The repo ships an `app/google-services.template.json` so the build doesn't break before you've configured Firebase. Don't commit your real `google-services.json` — `.gitignore` excludes it.

### 3. Build & run the app

```bash
git clone <repo-url>
cd kaushalya-karnataka-self-employment-app

# Generate the gradle wrapper jar (one-time)
gradle wrapper --gradle-version 8.11.1

./gradlew :app:assembleDebug
# or open the project in Android Studio and Run on a device/emulator (API 24+)
```

### 4. Deploy backend (Firestore rules, indexes, Functions)

```bash
firebase login
firebase use --add                         # link the Firebase project

# Rules + indexes
firebase deploy --only firestore:rules,firestore:indexes,storage:rules

# Cloud Functions
cd functions
npm install
cd ..
firebase deploy --only functions
```

### 5. Local development with Firebase Emulator Suite

```bash
firebase emulators:start
# Auth          → localhost:9099
# Firestore     → localhost:8080
# Storage       → localhost:9199
# Functions     → localhost:5001
# Emulator UI   → http://localhost:4000
```

### 6. Release to Play Store

```bash
./gradlew :app:bundleRelease
# Sign the AAB with your release keystore (Android Studio: Build > Generate Signed Bundle).
# Upload to Play Console → Internal testing → promote to Closed/Open/Production.
```

> **Launcher icon note**: this scaffold ships an adaptive icon (API 26+). Before shipping to Play, generate density-specific raster fallbacks via Android Studio → File → New → Image Asset (Launcher Icons).

---

## Verification

### Unit tests

```bash
./gradlew :app:testDebugUnitTest
```

Covers `ComputeAverageRatingUseCase` and `FilterByCategoryUseCase`.

### Manual end-to-end (release acceptance)

1. Worker A signs in via Phone OTP, creates profile + 2 service cards + 3 portfolio photos.
2. Customer B signs in, finds Worker A by category, taps **Hire Me**.
3. Worker A receives an FCM push within 5s, sees the request in their dashboard.
4. Customer B leaves a 5-star review → `averageRating` updates within 5s on both screens.
5. Switch app language to Kannada in Settings → every screen renders Kannada strings.
6. Toggle worker availability to **Busy** → customer browse list shows the Busy badge.

### Backend verification

```bash
firebase emulators:start          # run instrumented tests against emulators
firebase deploy --dry-run         # ensure rules and indexes lint clean
```

---

## Documentation

- **[docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)** — layer-by-layer architecture overview
- **[docs/FIRESTORE_SCHEMA.md](docs/FIRESTORE_SCHEMA.md)** — collection layout and indexes
- **[docs/ROADMAP.md](docs/ROADMAP.md)** — 2-year phased plan from MVP to Platform & Ecosystem

---

## Impact goals

- **Entrepreneurship** — turn laborers into discoverable micro-entrepreneurs.
- **Dignity of Labor** — professionalize local services through digital portfolios.
- **Local Economy** — keep money circulating within the local community.

---

## License

To be decided by the project owner. By default, all rights reserved.
