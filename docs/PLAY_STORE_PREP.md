# Play Store launch prep — Kaushalya Karnataka

Everything you need to gather before publishing to Google Play. Most items take 5-15 minutes each.

---

## 1. Listing copy

### App title (≤ 30 chars)
```
Kaushalya Karnataka
```

### Short description (≤ 80 chars)
```
Find skilled workers near you. Verified profiles, fair prices, neighbour reviews.
```

### Full description (≤ 4000 chars)
```
Kaushalya Karnataka is a hyper-local "Blue-Collar Portfolio" for Karnataka.

Skilled local workers — electricians, plumbers, carpenters, tailors, mechanics, beauticians, drivers — get a free profile to showcase their work, list fixed prices, and accept hire requests from neighbours.

Customers find verified workers in their town with real ratings from neighbours. No middleman, no commission, fair prices.

For workers
• Free profile with photo portfolio of past work
• List your services with fixed or starting-at prices
• Set your working hours (Mon-Sun) and availability
• Get hire requests directly on your phone
• Build a verified rating from real customer reviews
• See open jobs posted by customers in your town
• Print your QR profile and stick it on your shop

For customers
• Browse workers by category (electrician, plumber, carpenter, painter, mason, mechanic, AC repair, tailor, gardener, driver…) and locality
• See ratings and reviews from neighbours before hiring
• Tap "Hire me" — your worker gets notified instantly
• Bookmark favourites, chat with workers, post jobs

Built for Karnataka
• Available in English, ಕನ್ನಡ, தமிழ், తెలుగు, हिन्दी
• Pricing in ₹
• Real workers from Mysuru, Bengaluru, Mangalore, Hubballi, Belagavi, Ballari, Tumakuru, Davangere, Shivamogga and beyond

Privacy first
• Phone-OTP sign-in — no email or password
• Your phone number is never visible to other users
• Delete your account in one tap, in-app

Why Kaushalya?
"Kaushalya" means skill. We turn local laborers into discoverable micro-entrepreneurs and help neighbours find each other for honest work.
```

### What's new (≤ 500 chars)
```
v1.0 — initial release.
- Phone OTP sign-in
- Worker portfolios with services, photos, reviews
- Customer browse + hire flow with chat
- Job board (post a need, workers apply)
- 5 languages (en, kn, ta, te, hi)
- Working hours, ratings, bookmarks
```

---

## 2. Required assets

### App icon
- ✅ Already in `app/src/main/res/mipmap-*` at all densities

### Feature graphic (mandatory)
- 1024 × 500 px
- PNG or JPG, no alpha
- Suggestion: app logo on Karnataka-flag yellow + red split, "Kaushalya Karnataka — kuala neighbours, fair prices" tagline

### Screenshots (mandatory: 2-8 per device class)
Required: at least **2 phone screenshots** (16:9 or 9:16, min 320px short edge, max 3840px long edge).

Recommended set of 6 (use a real device + scrcpy or Android Studio's screenshot tool):
1. Onboarding carousel (slide 1)
2. Browse screen with workers + town picker
3. Worker detail v2 with hero photo + tabs
4. Hire Me + chat
5. Worker dashboard with stat tiles
6. Settings showing 5-language toggle

```bash
# Capture from a real device:
adb shell screencap -p /sdcard/shot1.png
adb pull /sdcard/shot1.png ./play-store/screenshots/
```

### Optional assets
- Promo video (30s YouTube link)
- Tablet screenshots (only if you target tablets)

---

## 3. Content rating questionnaire

Answer at https://play.google.com/console/app/content-rating

For Kaushalya the answers are:
- **Violence?** No
- **Sexual content?** No
- **Profanity?** No
- **Controlled substances?** No
- **Gambling?** No
- **Crude humor?** No
- **Horror?** No
- **User-generated content?** Yes (reviews, chat, profile bio)
- **Shares user location?** No (we don't capture GPS)
- **Allows users to interact?** Yes (chat, reviews)
- **Shares personal info to third parties?** No (Firebase only)
- **Digital purchases?** No (UPI deep-link is external)

Result expected: **Rated for everyone (3+)** with a "User interaction" disclosure.

---

## 4. Privacy policy + Terms URLs

Play Console **requires** publicly accessible URLs.

Quickest path: host the in-app PRIVACY/TERMS text on a free static page.

Option A — GitHub Pages (free, recommended):
1. In your repo, create `docs/site/privacy.html` and `docs/site/terms.html` with the same body text from `LegalScreens.kt`.
2. In repo Settings → Pages → enable, source = `main` / `/docs/site`.
3. URLs become `https://hysterical-monk.github.io/kaushalya-karnataka-self-employment-app/privacy.html`.

Option B — Firebase Hosting (need Blaze) — skip until Blaze.

---

## 5. Store listing checklist

- [ ] App title (30 chars)
- [ ] Short description (80 chars)
- [ ] Full description (4000 chars)
- [ ] App icon (already in app)
- [ ] Feature graphic 1024 × 500
- [ ] 2-8 phone screenshots
- [ ] Privacy policy URL (public)
- [ ] Content rating questionnaire complete
- [ ] App category: **Lifestyle** (or **Business**)
- [ ] Tags: `local services`, `gig`, `karnataka`, `marketplace`
- [ ] Contact email
- [ ] Default language: **English (United Kingdom)**

---

## 6. Release tracks

Recommended progression:

1. **Internal testing** (now): you + 5 testers, no waitlist
2. **Closed testing** (after Phase 8 of real-user feedback): 50-100 testers via Google Group
3. **Open testing** (1-2 months after closed): public, anyone can opt-in via a link
4. **Production**: full public launch

For Internal testing — you don't need any of the above review/policy steps. You can upload a signed AAB right now.

---

## 7. Build a signed AAB

```bash
cd "/Users/dev01/Library/CloudStorage/OneDrive-Personal/Tech/srinivas/projects/git repo/kaushalya-karnataka-self-employment-app"

# 1. Generate a release keystore (one-time, KEEP THIS FILE SAFE)
keytool -genkey -v -keystore release.jks -keyalg RSA -keysize 2048 \
        -validity 10000 -alias kaushalya

# 2. Wire up keystore.properties from the template
cp keystore.properties.template keystore.properties
# Then edit keystore.properties with the password you set

# 3. Build the AAB
./gradlew :app:bundleRelease

# 4. Upload to Play Console:
# app/build/outputs/bundle/release/app-release.aab
```

Add the **release** keystore SHA-1 to Firebase Console → Project Settings → Android app, otherwise Phone Auth will fail in production.

```
keytool -list -v -keystore release.jks -alias kaushalya | grep SHA1
```

---

## 8. Pricing & distribution

- **Free** app (you'll add monetization later via subscriptions / UPI)
- **Available in**: select India, then add specific countries you want later
- **Contains ads**: No (currently)
- **In-app purchases**: No (UPI deep-link is external, doesn't count)
- **Play Pass**: not eligible (free app)
- **Government / education / accessibility special programs**: skip
