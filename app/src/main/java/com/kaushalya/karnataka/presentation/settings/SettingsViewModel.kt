package com.kaushalya.karnataka.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaushalya.karnataka.core.analytics.AnalyticsEvent
import com.kaushalya.karnataka.core.analytics.AnalyticsTracker
import com.kaushalya.karnataka.core.locale.AppLocaleManager
import com.kaushalya.karnataka.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val language: String = "en"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val locale: AppLocaleManager,
    private val authRepository: AuthRepository,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState(language = locale.current()))
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun setLanguage(language: String) {
        locale.set(language)
        _state.value = _state.value.copy(language = language)
        analytics.log(AnalyticsEvent.LanguageSwitched(language))
    }

    fun signOut(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onDone()
        }
    }
}
