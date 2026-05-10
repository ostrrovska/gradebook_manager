package com.example.gradebook2.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gradebook2.ui.screens.add.AddGradeScreen
import com.example.gradebook2.ui.screens.detail.DetailsScreen
import com.example.gradebook2.ui.screens.grid.GridTabContent
import com.example.gradebook2.ui.screens.list.ListTabContent
import com.example.gradebook2.ui.screens.profile.ProfileTabContent

// Lab 8 — no longer takes userName param; Profile reads it from DataStore
// Lab 9, Task 4 — add_grade route + FAB added; Snackbar host shared with ListTabContent
@Composable
fun MainScreen() {
    val bottomNavController   = rememberNavController()
    val snackbarHostState     = remember { SnackbarHostState() }

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route

    val tabRoutes = listOf(BottomNavItem.List.route, BottomNavItem.Grid.route, BottomNavItem.Profile.route)
    val showFab   = currentRoute == BottomNavItem.List.route

    Scaffold(
        bottomBar = {
            // Hide bottom bar on detail and add screens for cleaner UX
            if (currentRoute in tabRoutes) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    val items = listOf(BottomNavItem.List, BottomNavItem.Grid, BottomNavItem.Profile)
                    items.forEach { item ->
                        NavigationBarItem(
                            icon     = { Icon(item.icon, contentDescription = item.title) },
                            label    = { Text(item.title, style = MaterialTheme.typography.labelMedium) },
                            selected = currentRoute == item.route,
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
        },
        // Lab 9, Task 4 — FAB visible only on List tab to trigger add-grade route
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick            = { bottomNavController.navigate("add_grade") },
                    containerColor     = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add grade",
                        tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        },
        // Lab 9, Task 5 — shared Snackbar host; ListTabContent writes messages here
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController    = bottomNavController,
            startDestination = BottomNavItem.List.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.List.route) {
                ListTabContent(
                    onItemClick       = { id -> bottomNavController.navigate("details/$id") },
                    snackbarHostState = snackbarHostState
                )
            }
            composable(BottomNavItem.Grid.route) {
                GridTabContent(onItemClick = { id -> bottomNavController.navigate("details/$id") })
            }
            composable(BottomNavItem.Profile.route) {
                ProfileTabContent()
            }
            composable(
                route     = "details/{itemId}",
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
                DetailsScreen(itemId = itemId, onBack = { bottomNavController.popBackStack() })
            }
            // Lab 9, Task 4 — add-grade as its own NavHost route
            composable("add_grade") {
                AddGradeScreen(onBack = { bottomNavController.popBackStack() })
            }
        }
    }
}
