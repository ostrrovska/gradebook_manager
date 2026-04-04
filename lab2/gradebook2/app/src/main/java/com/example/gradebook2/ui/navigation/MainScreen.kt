package com.example.gradebook2.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gradebook2.ui.screens.detail.DetailsScreen
import com.example.gradebook2.ui.screens.grid.GridTabContent
import com.example.gradebook2.ui.screens.list.ListTabContent
import com.example.gradebook2.ui.screens.profile.ProfileTabContent

/**
 * ЛР №6 — Завдання 5: Tab-навігація, `details/{itemId}`, збереження стану вкладок.
 * ЛР №7 — Завдання 3: кольори NavigationBar через MaterialTheme (мінімальні правки навігації).
 */
@Composable
fun MainScreen(initialUserName: String) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val items = listOf(BottomNavItem.List, BottomNavItem.Grid, BottomNavItem.Profile)
                items.forEach { item ->
                    NavigationBarItem(
                        icon     = { Icon(item.icon, contentDescription = item.title) },
                        label    = { Text(item.title, style = MaterialTheme.typography.labelMedium) },
                        selected = currentRoute == item.route ||
                            currentRoute?.startsWith("details") == true && item == BottomNavItem.List,
                        onClick  = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = MaterialTheme.colorScheme.primary,
                            selectedTextColor   = MaterialTheme.colorScheme.primary,
                            indicatorColor      = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = bottomNavController,
            startDestination = BottomNavItem.List.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.List.route) {
                ListTabContent(onItemClick = { id -> bottomNavController.navigate("details/$id") })
            }
            composable(BottomNavItem.Grid.route) {
                GridTabContent(onItemClick = { id -> bottomNavController.navigate("details/$id") })
            }
            composable(BottomNavItem.Profile.route) {
                ProfileTabContent(initialUserName = initialUserName)
            }
            composable(
                route     = "details/{itemId}",
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
                DetailsScreen(
                    itemId = itemId,
                    onBack = { bottomNavController.popBackStack() }
                )
            }
        }
    }
}
