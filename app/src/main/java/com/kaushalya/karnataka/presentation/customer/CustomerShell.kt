package com.kaushalya.karnataka.presentation.customer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kaushalya.karnataka.domain.model.Category
import com.kaushalya.karnataka.presentation.customer.browse.BrowseScreen
import com.kaushalya.karnataka.presentation.customer.home.HomeScreen
import com.kaushalya.karnataka.presentation.customer.jobs.CustomerJobsScreen
import com.kaushalya.karnataka.presentation.customer.profile.CustomerProfileScreen
import com.kaushalya.karnataka.presentation.customer.requests.CustomerRequestsScreen

private enum class CustomerTab(
    val route: String,
    val label: String,
    val outlined: ImageVector,
    val filled: ImageVector
) {
    HOME("home", "Home", Icons.Outlined.Home, Icons.Filled.Home),
    SEARCH("search", "Search", Icons.Outlined.Search, Icons.Filled.Search),
    JOBS("jobs", "Jobs", Icons.Outlined.WorkOutline, Icons.Filled.Work),
    REQUESTS("requests", "Requests", Icons.Outlined.Inbox, Icons.Filled.Inbox),
    PROFILE("profile", "Profile", Icons.Outlined.Person, Icons.Filled.Person)
}

@Composable
fun CustomerShell(
    onWorkerClick: (String) -> Unit,
    onOpenLanguage: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenImpact: () -> Unit,
    onOpenAppearance: () -> Unit,
    onChatClick: (customerId: String, workerId: String, title: String) -> Unit,
    onSignedOut: () -> Unit
) {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                CustomerTab.entries.forEach { tab ->
                    val selected = currentRoute?.startsWith(tab.route) == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                nav.navigate(tab.route) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(if (selected) tab.filled else tab.outlined, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = CustomerTab.HOME.route,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            composable(CustomerTab.HOME.route) {
                HomeScreen(
                    onWorkerClick = onWorkerClick,
                    onCategoryClick = { cat ->
                        // jump to the search tab pre-filtered
                        nav.navigate("${CustomerTab.SEARCH.route}?cat=${cat.id}") {
                            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onSeeAll = {
                        nav.navigate(CustomerTab.SEARCH.route) {
                            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(
                route = "${CustomerTab.SEARCH.route}?cat={cat}",
                arguments = listOf(androidx.navigation.navArgument("cat") {
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { entry ->
                val catId = entry.arguments?.getString("cat")
                BrowseScreen(
                    onWorkerClick = onWorkerClick,
                    initialCategory = catId?.let { Category.fromId(it) }
                )
            }
            composable(CustomerTab.SEARCH.route) {
                BrowseScreen(onWorkerClick = onWorkerClick, initialCategory = null)
            }
            composable(CustomerTab.JOBS.route) {
                CustomerJobsScreen()
            }
            composable(CustomerTab.REQUESTS.route) {
                CustomerRequestsScreen(
                    onWorkerClick = onWorkerClick,
                    onChatClick = onChatClick
                )
            }
            composable(CustomerTab.PROFILE.route) {
                CustomerProfileScreen(
                    onLanguageClick = onOpenLanguage,
                    onSignOut = onSignedOut,
                    onOpenPrivacy = onOpenPrivacy,
                    onOpenTerms = onOpenTerms,
                    onOpenBookmarks = onOpenBookmarks,
                    onOpenNotifications = onOpenNotifications,
                    onOpenImpact = onOpenImpact,
                    onOpenAppearance = onOpenAppearance
                )
            }
        }
    }
}
