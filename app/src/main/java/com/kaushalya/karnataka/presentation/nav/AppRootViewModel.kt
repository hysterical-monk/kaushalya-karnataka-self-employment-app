package com.kaushalya.karnataka.presentation.nav

import androidx.lifecycle.ViewModel
import com.kaushalya.karnataka.core.prefs.OnboardingStore
import com.kaushalya.karnataka.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppRootViewModel @Inject constructor(
    auth: AuthRepository,
    onboardingStore: OnboardingStore
) : ViewModel() {
    val startDestination: String = when {
        !onboardingStore.isCompleted() -> Routes.ONBOARDING
        !auth.isSignedIn() -> Routes.PHONE_ENTRY
        else -> Routes.CUSTOMER_SHELL
    }
}
