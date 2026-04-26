# Firestore Schema

## Collections

### `users/{uid}`
Profile document mirrored to every authenticated user.

| Field         | Type      | Notes                                                       |
| :------------ | :-------- | :---------------------------------------------------------- |
| `phone`       | string    | E.164 format (e.g. `+919876543210`)                         |
| `displayName` | string    | Set after onboarding role select                            |
| `role`        | string    | `worker` \| `customer` \| `unknown`                         |
| `language`    | string    | `en` \| `kn`                                                |
| `fcmToken`    | string    | Latest FCM registration token (synced by `KaushalyaFcmService`) |
| `createdAt`   | timestamp | Server timestamp                                            |
| `updatedAt`   | timestamp | Server timestamp                                            |

### `workers/{workerId}` (where `workerId == uid`)
Public profile shown to customers.

| Field             | Type             | Notes                                                       |
| :---------------- | :--------------- | :---------------------------------------------------------- |
| `displayName`     | string           |                                                             |
| `phone`           | string           |                                                             |
| `bio`             | string           |                                                             |
| `photoUrl`        | string \| null   | Cloud Storage URL                                           |
| `town`            | string           |                                                             |
| `locality`        | string           | Free-text neighbourhood / area                              |
| `categories`      | string[]         | Lower-cased category ids (`electrician`, `plumber`, …)      |
| `availability`    | string           | `available` \| `busy`                                       |
| `averageRating`   | number           | Maintained by `onReviewWritten` Cloud Function              |
| `ratingCount`     | number           | Maintained by `onReviewWritten` Cloud Function              |
| `thumbsUpCount`   | number           | Maintained by `onReviewWritten` Cloud Function              |
| `createdAt`       | timestamp        |                                                             |
| `updatedAt`       | timestamp        |                                                             |

### `workers/{workerId}/services/{serviceId}`

| Field         | Type      | Notes                                                       |
| :------------ | :-------- | :---------------------------------------------------------- |
| `title`       | string    | e.g. "Fan Repair"                                           |
| `description` | string    |                                                             |
| `priceType`   | string    | `fixed` \| `starting_at`                                    |
| `priceInr`    | number    | Whole rupees                                                |
| `active`      | boolean   |                                                             |
| `updatedAt`   | timestamp |                                                             |

### `workers/{workerId}/portfolio/{photoId}`

| Field        | Type      | Notes                                                |
| :----------- | :-------- | :--------------------------------------------------- |
| `imageUrl`   | string    | Cloud Storage download URL                           |
| `caption`    | string    |                                                      |
| `uploadedAt` | timestamp |                                                      |

### `workers/{workerId}/reviews/{reviewId}`
**Convention**: `reviewId == customerUid` so each customer can leave at most one review per worker (write upserts).

| Field          | Type      | Notes                                              |
| :------------- | :-------- | :------------------------------------------------- |
| `customerId`   | string    | Must equal `request.auth.uid` per security rule    |
| `customerName` | string    |                                                    |
| `stars`        | number    | 1..5 (validated server-side by rules)              |
| `thumbsUp`     | boolean   |                                                    |
| `text`         | string    | ≤ 500 chars (validated)                            |
| `createdAt`    | timestamp |                                                    |

### `hire_requests/{requestId}`
Top-level so that a worker can query everything addressed to them in one shot.

| Field          | Type      | Notes                                              |
| :------------- | :-------- | :------------------------------------------------- |
| `customerId`   | string    | Must equal `request.auth.uid` on create            |
| `customerName` | string    |                                                    |
| `workerId`     | string    |                                                    |
| `serviceId`    | string?   | Optional; the specific service the customer wants  |
| `serviceTitle` | string?   | Denormalized snapshot of the service title         |
| `message`      | string    | ≤ 500 chars                                        |
| `status`       | string    | `pending` \| `seen` \| `completed` \| `cancelled`  |
| `createdAt`    | timestamp |                                                    |

### `bookmarks/{customerId}/workers/{workerId}`
Customer-private collection. The doc id is the worker's id.

| Field          | Type      |
| :------------- | :-------- |
| `bookmarkedAt` | timestamp |

## Indexes (`firestore.indexes.json`)
- `workers`: `categories array-contains` + `averageRating desc` — powers the browse query.
- `hire_requests`: `workerId asc` + `createdAt desc` — worker dashboard.
- `hire_requests`: `customerId asc` + `createdAt desc` — customer history.

## Security highlights (`firestore.rules`)
- Worker profiles are world-readable; only the worker can write to their own `workers/{uid}` subtree.
- Reviews require `request.auth.uid == reviewId` and `customerId == request.auth.uid`, plus `1 ≤ stars ≤ 5` and `text.size() ≤ 500`.
- `hire_requests` are readable only by the customer or worker on the document; the customer creates with `status == "pending"`.
- Bookmarks are entirely private to the customer.
