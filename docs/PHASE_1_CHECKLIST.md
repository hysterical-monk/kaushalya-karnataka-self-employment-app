# Phase 1 — MVP Launch Checklist

Tick these off in order. Anything past `Build & smoke test` requires a real Firebase project and a Play Console account.

---

## A. One-time Firebase setup

- [ ] Create a Firebase project at https://console.firebase.google.com (`kaushalya-karnataka-prod` or similar)
- [ ] Add an Android app with package id `com.kaushalya.karnataka`
- [ ] Download `google-services.json` and drop it into `app/`
- [ ] Enable **Authentication → Sign-in method → Phone**
- [ ] Enable **Firestore Database** (Native mode, region `asia-south1`)
- [ ] Enable **Storage**
- [ ] Enable **Cloud Messaging**
- [ ] Enable **Cloud Functions** — requires upgrade to **Blaze** (pay-as-you-go) plan
- [ ] Enable **Crashlytics** and **Analytics**
- [ ] Enable **App Check** with Play Integrity provider
- [ ] Get debug keystore SHA-1 and add it to the Firebase Android app (Phone Auth needs it):
      ```
      keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey \
              -storepass android -keypass android | grep SHA1
      ```
- [ ] Replace `REPLACE_WITH_FIREBASE_PROJECT_ID` in `.firebaserc` with your project id

## B. One-time local setup

- [ ] Install JDK 17, Android Studio Ladybug+, Node 20, `firebase-tools`
- [ ] `gradle wrapper --gradle-version 8.11.1` (one-time, populates `gradle-wrapper.jar`)
- [ ] `firebase login`
- [ ] `firebase use --add` and pick your project

## C. Build & smoke test

- [ ] `./gradlew :app:assembleDebug` — debug APK builds
- [ ] `./gradlew :app:testDebugUnitTest` — unit tests pass
- [ ] Install debug APK on a device
- [ ] Manual E2E smoke test (8 steps from `README.md` § Verification):
  - [ ] Worker A signs up via Phone OTP
  - [ ] Worker A creates profile + 2 service cards + 3 portfolio photos
  - [ ] Customer B signs up
  - [ ] Customer B finds Worker A by category
  - [ ] Customer B taps Hire Me → Worker A receives FCM push within 5s
  - [ ] Customer B leaves a 5-star review → averageRating updates within 5s on both devices
  - [ ] Switch app language to Kannada → all screens localize
  - [ ] Worker A toggles Busy → Customer B sees Busy badge in browse list

## D. Backend deploy

- [ ] `firebase deploy --only firestore:rules,firestore:indexes,storage:rules`
- [ ] `cd functions && npm install && cd ..`
- [ ] `firebase deploy --only functions`
- [ ] Verify in Firebase Console that `onReviewWritten` and `onHireRequestCreated` are deployed

## E. Release signing

- [ ] Generate a release keystore once:
      ```
      keytool -genkey -v -keystore release.jks -keyalg RSA -keysize 2048 \
              -validity 10000 -alias kaushalya
      ```
- [ ] Move `release.jks` to the repo root (it's git-ignored)
- [ ] `cp keystore.properties.template keystore.properties` and fill in real values
- [ ] Add the **release** keystore SHA-1 to Firebase too (Phone Auth needs it for production)
- [ ] `./gradlew :app:bundleRelease` — produces a signed `.aab` at `app/build/outputs/bundle/release/`

## F. Play Console

- [ ] Create a developer account at https://play.google.com/console (one-time $25)
- [ ] Create the app (package: `com.kaushalya.karnataka`)
- [ ] Fill in store listing (icon, screenshots, short + full description, privacy policy URL)
- [ ] Upload the signed `.aab` to **Internal testing**
- [ ] Add ≥ 5 internal testers (email or Google group)
- [ ] Each tester completes the 8-step smoke test on their own device
- [ ] Crashlytics shows 0 unresolved crashes for 7 consecutive days

## G. Phase 1 exit gate

- [ ] All A–F boxes checked
- [ ] No P0/P1 issues open
- [ ] Crashlytics: crash-free sessions ≥ 99%
- [ ] App size < 15 MB
- [ ] Cold start < 2.5s on a Redmi 9 / equivalent

When all of G is green, **promote Internal → Closed testing** and start Phase 2 (multi-photo portfolio, verified-work badge, accessibility pass).
