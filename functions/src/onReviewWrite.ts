import { onDocumentWritten } from "firebase-functions/v2/firestore";
import { logger } from "firebase-functions";
import { getFirestore } from "firebase-admin/firestore";

/**
 * Recompute the worker's averageRating, ratingCount, and thumbsUpCount
 * whenever a review is created, edited, or deleted.
 */
export const onReviewWritten = onDocumentWritten(
  "workers/{workerId}/reviews/{reviewId}",
  async (event) => {
    const workerId = event.params.workerId;
    const db = getFirestore();
    const reviewsSnap = await db.collection(`workers/${workerId}/reviews`).get();

    let total = 0;
    let count = 0;
    let thumbs = 0;
    reviewsSnap.forEach((doc) => {
      const stars = doc.get("stars");
      if (typeof stars === "number") {
        total += stars;
        count += 1;
      }
      if (doc.get("thumbsUp") === true) thumbs += 1;
    });

    const average = count === 0 ? 0 : Math.round((total / count) * 10) / 10;
    await db.collection("workers").doc(workerId).set(
      {
        averageRating: average,
        ratingCount: count,
        thumbsUpCount: thumbs,
      },
      { merge: true }
    );

    logger.info(`Recomputed rating for worker=${workerId}: avg=${average} count=${count} thumbs=${thumbs}`);
  }
);
