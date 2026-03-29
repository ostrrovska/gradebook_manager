package com.example.gradebook2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gradebook2.ui.screens.onboarding.NameEntryScreen
import com.example.gradebook2.ui.screens.onboarding.OnboardingScreen

/**
 * ЛР №6 — Завдання 5: кореневий навігаційний граф (цілісність з ЛР №5).
 *
 * Перевірити: onboarding → main/{userName}; ViewModel тут не використовуються — лише маршрути.
 */
@Composable
fun RootNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            OnboardingScreen(navController)
        }
        composable("name_entry") {
            NameEntryScreen(navController)
        }
        composable(
            route = "main/{userName}",
            arguments = listOf(navArgument("userName") { type = NavType.StringType })
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Student"
            MainScreen(userName)
        }
    }
}
