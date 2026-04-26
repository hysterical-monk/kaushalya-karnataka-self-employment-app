// Seed script: pushes demo workers + services + portfolio + reviews into Firestore.
// Usage:
//   1. Download a service account JSON from
//      Firebase Console -> Project Settings -> Service accounts -> Generate new private key
//      Save it as tools/seed/service-account.json (gitignored).
//   2. cd tools/seed && npm install
//   3. npm run seed           # adds demo data (idempotent — safe to re-run)
//      npm run seed:clean     # wipes existing demo_w_* docs and re-seeds

import { readFileSync, existsSync } from "node:fs";
import { fileURLToPath } from "node:url";
import { dirname, join } from "node:path";
import { initializeApp, cert } from "firebase-admin/app";
import { getFirestore, FieldValue } from "firebase-admin/firestore";
import { WORKERS } from "./data.mjs";

const __dirname = dirname(fileURLToPath(import.meta.url));
const SA_PATH = join(__dirname, "service-account.json");

if (!existsSync(SA_PATH)) {
  console.error("Missing service-account.json at tools/seed/service-account.json");
  console.error("Download from Firebase Console -> Project Settings -> Service accounts.");
  process.exit(1);
}

const serviceAccount = JSON.parse(readFileSync(SA_PATH, "utf8"));
initializeApp({ credential: cert(serviceAccount) });

const db = getFirestore();
const args = new Set(process.argv.slice(2));
const CLEAN = args.has("--clean");

async function clean() {
  console.log("Cleaning existing demo workers (id prefix demo_w_)...");
  const snap = await db.collection("workers").get();
  let removed = 0;
  for (const doc of snap.docs) {
    if (!doc.id.startsWith("demo_w_")) continue;
    // Delete subcollections first
    for (const sub of ["services", "portfolio", "reviews"]) {
      const subSnap = await doc.ref.collection(sub).get();
      for (const s of subSnap.docs) await s.ref.delete();
    }
    await doc.ref.delete();
    removed += 1;
  }
  console.log(`Removed ${removed} demo worker(s).`);
}

async function seed() {
  console.log(`Seeding ${WORKERS.length} demo workers...`);
  for (const w of WORKERS) {
    const ref = db.collection("workers").doc(w.id);
    await ref.set({
      displayName: w.displayName,
      phone: w.phone,
      bio: w.bio,
      photoUrl: w.photoUrl,
      town: w.town,
      locality: w.locality,
      categories: w.categories,
      availability: w.availability,
      averageRating: w.averageRating,
      ratingCount: w.ratingCount,
      thumbsUpCount: w.thumbsUpCount,
      createdAt: FieldValue.serverTimestamp(),
      updatedAt: FieldValue.serverTimestamp(),
      isDemo: true
    });

    for (const [i, s] of w.services.entries()) {
      await ref.collection("services").doc(`s${i + 1}`).set({
        title: s.title,
        description: s.description,
        priceType: s.priceType,
        priceInr: s.priceInr,
        active: true,
        updatedAt: FieldValue.serverTimestamp()
      });
    }

    for (const [i, p] of w.portfolio.entries()) {
      await ref.collection("portfolio").doc(`p${i + 1}`).set({
        imageUrl: p.url,
        caption: p.caption,
        uploadedAt: FieldValue.serverTimestamp()
      });
    }

    for (const [i, r] of w.reviews.entries()) {
      const customerId = `demo_c_${w.id}_${i + 1}`;
      await ref.collection("reviews").doc(customerId).set({
        customerId,
        customerName: r.customer,
        stars: r.stars,
        thumbsUp: r.thumbsUp,
        text: r.text,
        createdAt: FieldValue.serverTimestamp()
      });
    }

    console.log(`  ✓ ${w.displayName} — ${w.services.length} services, ${w.portfolio.length} photos, ${w.reviews.length} reviews`);
  }
  console.log(`Done. ${WORKERS.length} workers seeded.`);
}

(async () => {
  try {
    if (CLEAN) await clean();
    await seed();
    process.exit(0);
  } catch (e) {
    console.error("Seed failed:", e);
    process.exit(1);
  }
})();
