package com.kaushalya.karnataka.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.kaushalya.karnataka.data.FirestorePaths
import com.kaushalya.karnataka.domain.model.AppUser
import com.kaushalya.karnataka.domain.model.UserRole
import com.kaushalya.karnataka.domain.repository.AuthRepository
import com.kaushalya.karnataka.domain.repository.VerificationHandle
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: Flow<AppUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { fb ->
            val user = fb.currentUser
            if (user == null) {
                trySend(null)
            } else {
                firestore.collection(FirestorePaths.USERS).document(user.uid).get()
                    .addOnSuccessListener { snap ->
                        trySend(
                            AppUser(
                                uid = user.uid,
                                phone = user.phoneNumber.orEmpty(),
                                displayName = snap.getString("displayName").orEmpty(),
                                role = UserRole.fromString(snap.getString("role")),
                                language = snap.getString("language") ?: "en"
                            )
                        )
                    }
                    .addOnFailureListener { trySend(null) }
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun isSignedIn(): Boolean = auth.currentUser != null

    override suspend fun startPhoneVerification(phoneE164: String): VerificationHandle =
        suspendCancellableCoroutine { cont ->
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    if (cont.isActive) cont.resume(VerificationHandle(verificationId = id, phone = phoneE164))
                }

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-resolution path: produce a synthetic handle, OTP step will reuse via verifyOtp.
                    if (cont.isActive) cont.resume(VerificationHandle(verificationId = "auto:" + credential.smsCode.orEmpty(), phone = phoneE164))
                }

                override fun onVerificationFailed(error: com.google.firebase.FirebaseException) {
                    if (cont.isActive) cont.cancel(error)
                }
            }
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneE164)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }

    override suspend fun verifyOtp(handle: VerificationHandle, code: String): Result<AppUser> = runCatching {
        val credential = PhoneAuthProvider.getCredential(handle.verificationId, code)
        val result = auth.signInWithCredential(credential).await()
        val uid = result.user?.uid ?: error("No uid after sign in")
        val docRef = firestore.collection(FirestorePaths.USERS).document(uid)
        val existing = docRef.get().await()
        if (!existing.exists()) {
            docRef.set(
                mapOf(
                    "phone" to handle.phone,
                    "role" to UserRole.UNKNOWN.asString(),
                    "language" to "en",
                    "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
            ).await()
        }
        AppUser(
            uid = uid,
            phone = handle.phone,
            displayName = existing.getString("displayName").orEmpty(),
            role = UserRole.fromString(existing.getString("role")),
            language = existing.getString("language") ?: "en"
        )
    }

    override suspend fun completeProfile(displayName: String, role: UserRole): Result<AppUser> = runCatching {
        val current = auth.currentUser ?: error("Not signed in")
        val updates = mapOf(
            "displayName" to displayName,
            "role" to role.asString(),
            "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        firestore.collection(FirestorePaths.USERS).document(current.uid).set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
        if (role == UserRole.WORKER) {
            firestore.collection(FirestorePaths.WORKERS).document(current.uid).set(
                mapOf(
                    "displayName" to displayName,
                    "phone" to current.phoneNumber.orEmpty(),
                    "availability" to "available",
                    "averageRating" to 0.0,
                    "ratingCount" to 0,
                    "thumbsUpCount" to 0,
                    "categories" to emptyList<String>(),
                    "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                ),
                com.google.firebase.firestore.SetOptions.merge()
            ).await()
        }
        AppUser(
            uid = current.uid,
            phone = current.phoneNumber.orEmpty(),
            displayName = displayName,
            role = role,
            language = "en"
        )
    }

    override suspend fun updateDisplayName(displayName: String): Result<Unit> = runCatching {
        val current = auth.currentUser ?: error("Not signed in")
        firestore.collection(FirestorePaths.USERS).document(current.uid).set(
            mapOf(
                "displayName" to displayName,
                "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            ),
            com.google.firebase.firestore.SetOptions.merge()
        ).await()
        // also mirror onto the worker doc if it exists
        val workerRef = firestore.collection(FirestorePaths.WORKERS).document(current.uid)
        if (workerRef.get().await().exists()) {
            workerRef.update("displayName", displayName).await()
        }
    }

    override suspend fun deleteAccount(): Result<Unit> = runCatching {
        val current = auth.currentUser ?: error("Not signed in")
        val uid = current.uid
        // Best-effort cleanup of user-owned data
        runCatching {
            firestore.collection(FirestorePaths.WORKERS).document(uid).delete().await()
        }
        runCatching {
            firestore.collection(FirestorePaths.USERS).document(uid).delete().await()
        }
        runCatching {
            firestore.collection(FirestorePaths.BOOKMARKS).document(uid)
                .collection(FirestorePaths.BOOKMARK_WORKERS).get().await()
                .documents.forEach { it.reference.delete() }
        }
        // Delete the auth user (may require recent sign-in; surface error to caller)
        current.delete().await()
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}
