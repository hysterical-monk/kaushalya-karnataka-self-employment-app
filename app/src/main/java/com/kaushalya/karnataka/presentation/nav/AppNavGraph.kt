package com.kaushalya.karnataka.presentation.nav

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kaushalya.karnataka.presentation.auth.OtpScreen
import com.kaushalya.karnataka.presentation.auth.PhoneEntryScreen
import com.kaushalya.karnataka.presentation.customer.CustomerShell
import com.kaushalya.karnataka.presentation.customer.workerdetail.WorkerDetailScreen
import com.kaushalya.karnataka.presentation.onboarding.OnboardingScreen
import com.kaushalya.karnataka.presentation.onboarding.RoleSelectScreen
import com.kaushalya.karnataka.presentation.settings.SettingsScreen
import com.kaushalya.karnataka.presentation.worker.dashboard.WorkerDashboardScreen
import com.kaushalya.karnataka.presentation.worker.portfolio.WorkerPortfolioScreen
import com.kaushalya.karnataka.presentation.worker.profile.WorkerProfileScreen
import com.kaushalya.karnataka.presentation.worker.services.WorkerServicesScreen

@Composable
fun AppNavGraph(navController: NavHostController, startDestination: String) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300)) +
                fadeIn(tween(300))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300)) +
                fadeOut(tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300)) +
                fadeIn(tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300)) +
                fadeOut(tween(300))
        }
    ) {

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Routes.PHONE_ENTRY) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PHONE_ENTRY) {
            PhoneEntryScreen(
                onCodeSent = { vid, phone ->
                    navController.navigate(Routes.otp(vid, phone))
                }
            )
        }

        composable(
            route = Routes.OTP,
            arguments = listOf(
                navArgument("verificationId") { type = NavType.StringType },
                navArgument("phone") { type = NavType.StringType }
            )
        ) { entry ->
            val vid = entry.arguments?.getString("verificationId").orEmpty()
            val phone = entry.arguments?.getString("phone").orEmpty()
            OtpScreen(
                verificationId = vid,
                phone = phone,
                onProfileNeeded = {
                    navController.navigate(Routes.ROLE_SELECT) {
                        popUpTo(Routes.PHONE_ENTRY) { inclusive = true }
                    }
                },
                onSignedInAsCustomer = {
                    navController.navigate(Routes.CUSTOMER_SHELL) {
                        popUpTo(Routes.PHONE_ENTRY) { inclusive = true }
                    }
                },
                onSignedInAsWorker = {
                    navController.navigate(Routes.WORKER_DASHBOARD) {
                        popUpTo(Routes.PHONE_ENTRY) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ROLE_SELECT) {
            RoleSelectScreen(
                onWorker = {
                    navController.navigate(Routes.WORKER_DASHBOARD) {
                        popUpTo(Routes.ROLE_SELECT) { inclusive = true }
                    }
                },
                onCustomer = {
                    navController.navigate(Routes.CUSTOMER_SHELL) {
                        popUpTo(Routes.ROLE_SELECT) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CUSTOMER_SHELL) {
            CustomerShell(
                onWorkerClick = { id -> navController.navigate(Routes.workerDetail(id)) },
                onOpenLanguage = { navController.navigate(Routes.SETTINGS) },
                onSignedOut = {
                    navController.navigate(Routes.PHONE_ENTRY) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.WORKER_DETAIL,
            arguments = listOf(navArgument("workerId") { type = NavType.StringType })
        ) { entry ->
            WorkerDetailScreen(
                workerId = entry.arguments?.getString("workerId").orEmpty(),
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.WORKER_DASHBOARD) {
            WorkerDashboardScreen(
                onProfileClick = { navController.navigate(Routes.WORKER_PROFILE) },
                onServicesClick = { navController.navigate(Routes.WORKER_SERVICES) },
                onPortfolioClick = { navController.navigate(Routes.WORKER_PORTFOLIO) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.WORKER_PROFILE) {
            WorkerProfileScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.WORKER_SERVICES) {
            WorkerServicesScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.WORKER_PORTFOLIO) {
            WorkerPortfolioScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onSignedOut = {
                    navController.navigate(Routes.PHONE_ENTRY) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
