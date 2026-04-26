package com.kaushalya.karnataka.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.kaushalya.karnataka.core.prefs.OnboardingStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val store: OnboardingStore
) : ViewModel() {
    fun markCompleted() = store.markCompleted()
}
