package com.kaushalya.karnataka.domain.repository

import com.kaushalya.karnataka.domain.model.AppUser
import com.kaushalya.karnataka.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val currentUser: Flow<AppUser?>

    fun isSignedIn(): Boolean

    suspend fun startPhoneVerification(phoneE164: String): VerificationHandle

    suspend fun verifyOtp(handle: VerificationHandle, code: String): Result<AppUser>

    suspend fun completeProfile(displayName: String, role: UserRole): Result<AppUser>

    suspend fun signOut()
}

data class VerificationHandle(
    val verificationId: String,
    val phone: String
)
