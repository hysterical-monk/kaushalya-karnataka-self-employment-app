# Architecture

Kaushalya-Karnataka follows **Clean Architecture** with three concentric layers. The dependency rule is one-way: outer layers know about inner layers, not the reverse.

```
presentation → domain ← data
              (interfaces)
```

## Layers

### domain (`com.kaushalya.karnataka.domain`)
Pure Kotlin. No Android, no Firebase imports.
- **`model/`** — immutable domain entities: `Worker`, `ServiceCard`, `Review`, `HireRequest`, `PortfolioPhoto`, `Category`, `AppUser`, `UserRole`, `PriceType`, `HireStatus`.
- **`repository/`** — interfaces only: `AuthRepository`, `WorkerRepository`, `ReviewRepository`, `HireRepository`, `BookmarkRepository`.
- **`usecase/`** — single-method classes: `ComputeAverageRatingUseCase`, `FilterByCategoryUseCase`. Use cases are unit-testable without any Android dependency.

### data (`com.kaushalya.karnataka.data`)
Implements the domain repository interfaces against Firebase.
- **`auth/FirebaseAuthRepository`** — Phone OTP via `PhoneAuthProvider`, persists `users/{uid}` profile doc.
- **`worker/WorkerRepositoryImpl`** — Firestore CRUD on `workers/{id}` and its subcollections; portfolio uploads to Storage.
- **`review/ReviewRepositoryImpl`** — write-once-per-customer review under `workers/{id}/reviews/{customerUid}`.
- **`hire/HireRepositoryImpl`** — top-level `hire_requests/{id}` collection; queries for worker dashboard and customer history.
- **`bookmark/BookmarkRepositoryImpl`** — private subcollection `bookmarks/{customerUid}/workers/{workerId}`.
- **`messaging/KaushalyaFcmService`** — receives FCM, refreshes token in Firestore, posts notification.
- **`FlowExt.kt`** — `Query.snapshotsFlow()` and `DocumentReference.snapshotsFlow()` for converting Firestore snapshot listeners to Kotlin Flows.

### presentation (`com.kaushalya.karnataka.presentation`)
Compose screens and Hilt ViewModels. Each feature is a self-contained package.
- **`auth/`** — `PhoneEntryScreen`, `OtpScreen` (+ ViewModels).
- **`onboarding/`** — `RoleSelectScreen` (worker vs. customer + display name).
- **`customer/browse/`** — `BrowseScreen` with category chips and search.
- **`customer/workerdetail/`** — `WorkerDetailScreen` with services, portfolio strip, review wall, Hire Me CTA.
- **`customer/bookmarks/`** — `BookmarksScreen`.
- **`worker/dashboard/`** — `WorkerDashboardScreen` with availability toggle and incoming hire requests.
- **`worker/profile/`** — `WorkerProfileScreen` for editing name, bio, town, locality, categories.
- **`worker/services/`** — `WorkerServicesScreen` with add/edit/delete dialogs.
- **`worker/portfolio/`** — `WorkerPortfolioScreen` with `PickVisualMedia` photo upload + delete.
- **`settings/`** — `SettingsScreen` with language switch and sign-out.
- **`nav/`** — `AppNavGraph`, `Routes`, `AppRootViewModel` (decides start destination based on auth state).

### core (`com.kaushalya.karnataka.core`)
Cross-cutting infrastructure.
- **`di/`** — Hilt modules: `FirebaseModule`, `RepositoryModule` (binds data → domain), `DispatcherModule` (qualified `Dispatchers.IO/Default/Main`).
- **`locale/AppLocaleManager`** — wraps `AppCompatDelegate.setApplicationLocales` for runtime language switch.
- **`ui/theme/`** — `KaushalyaTheme`, `Color.kt` (Karnataka-flag-inspired palette), `Type.kt`.
- **`ui/components/`** — shared composables: `WorkerCard`, `RatingStars`, `CategoryChip`, `LoadingState`, `EmptyState`, `ErrorState`.
- **`result/UiResult`** — sealed result type for screens.

## State management
- **ViewModels** expose `StateFlow<UiState>`, collected with `collectAsStateWithLifecycle()`.
- **Repositories** expose `Flow<T>` (typically backed by Firestore real-time listeners).
- **Side effects** (sign-in, navigation, snackbars) are triggered via `onEvent`-style callbacks, not `SharedFlow` channels — keeps screens stateless.

## Backend (Firebase)
- **Auth** — Phone OTP. SHA-1 of debug keystore must be registered in Firebase console.
- **Firestore** — single project, single region. Rules in `firestore.rules`, indexes in `firestore.indexes.json`.
- **Storage** — `portfolio/{workerId}/{photoId}.jpg` and `profile/{workerId}/{photoId}.jpg` paths; per-user write rules.
- **Functions** (`functions/`):
  - `onReviewWritten` — on any write to `workers/{id}/reviews/{*}`, recompute `averageRating`, `ratingCount`, `thumbsUpCount` for the worker.
  - `onHireRequestCreated` — on new `hire_requests/{id}`, look up worker's `fcmToken` from `users/{workerId}`, send FCM notification.
- **App Check** — Play Integrity provider initialized in `KaushalyaApp.onCreate()`.

## Testing strategy
- **Unit tests** (`app/src/test/`) — cover use cases and ViewModel logic with `MainDispatcherRule` + fake repositories. Already shipped: `ComputeAverageRatingUseCaseTest`, `FilterByCategoryUseCaseTest`.
- **Compose UI tests** (`app/src/androidTest/`) — Phase 2 will add tests for `BrowseScreen`, service edit, and Hire Me flow against the Firebase Emulator Suite.
- **Functions** — typed via TypeScript; integration-tested via the emulator.
