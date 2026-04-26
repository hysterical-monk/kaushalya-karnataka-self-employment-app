import { onDocumentCreated } from "firebase-functions/v2/firestore";
import { logger } from "firebase-functions";
import { getFirestore } from "firebase-admin/firestore";
import { getMessaging } from "firebase-admin/messaging";

/**
 * Notify the worker via FCM when a customer creates a hire request.
 * Reads the worker's saved fcmToken from /users/{workerId}.
 */
export const onHireRequestCreated = onDocumentCreated(
  "hire_requests/{requestId}",
  async (event) => {
    const data = event.data?.data();
    if (!data) return;

    const workerId: string | undefined = data.workerId;
    const customerName: string = data.customerName ?? "Someone";
    const message: string = data.message ?? "is interested in your services";
    const serviceTitle: string | undefined = data.serviceTitle;

    if (!workerId) {
      logger.warn("hire_request created without workerId", event.params.requestId);
      return;
    }

    const userDoc = await getFirestore().collection("users").doc(workerId).get();
    const fcmToken = userDoc.get("fcmToken");
    if (!fcmToken || typeof fcmToken !== "string") {
      logger.info(`No fcmToken for worker=${workerId}; skipping push.`);
      return;
    }

    const title = serviceTitle ? `New request: ${serviceTitle}` : "New hire request";
    const body = `${customerName}: ${message}`;

    await getMessaging().send({
      token: fcmToken,
      notification: { title, body },
      data: {
        requestId: event.params.requestId,
        workerId,
      },
      android: { priority: "high" },
    });

    logger.info(`Sent hire-request push to worker=${workerId}`);
  }
);
