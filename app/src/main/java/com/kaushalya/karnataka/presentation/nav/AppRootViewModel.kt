package com.kaushalya.karnataka.presentation.nav

import androidx.lifecycle.ViewModel
import com.kaushalya.karnataka.domain.model.UserRole
import com.kaushalya.karnataka.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppRootViewModel @Inject constructor(
    auth: AuthRepository
) : ViewModel() {
    val startDestination: String = if (!auth.isSignedIn()) Routes.PHONE_ENTRY else Routes.CUSTOMER_BROWSE
    // Role-based routing happens after auth state resolves; for cold-start we land on customer browse
    // and the AppNavGraph re-routes to worker dashboard / role select if the user is a worker / unknown.
    @Suppress("unused")
    private val _t: UserRole = UserRole.UNKNOWN
}
