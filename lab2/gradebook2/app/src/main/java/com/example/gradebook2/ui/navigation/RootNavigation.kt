package com.example.gradebook2.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gradebook2.GradeApplication
import com.example.gradebook2.ui.screens.onboarding.NameEntryScreen
import com.example.gradebook2.ui.screens.onboarding.OnboardingScreen
import kotlinx.coroutines.flow.first

// Lab 8, Task 1 — reads saved username from DataStore to decide start destination
@Composable
fun RootNavigation() {
    val app = LocalContext.current.applicationContext as GradeApplication

    // Lab 8, Task 1 — null means DataStore hasn't emitted yet; avoids flash of wrong screen
    var savedName: String? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        savedName = app.prefsRepository.userNameFlow.first()
    }

    // Show spinner while DataStore emits its first value
    if (savedName == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val navController = rememberNavController()
    // Lab 8, Task 1 — skip onboarding if name is already persisted
    val startDest = if (savedName!!.isNotBlank()) "main" else "onboarding"

    NavHost(navController = navController, startDestination = startDest) {
        composable("onboarding") {
            OnboardingScreen(navController)
        }
        composable("name_entry") {
            // Lab 8, Task 1 — save name to DataStore, then go to main
            NameEntryScreen(
                onSave = { name ->
                    navController.navigate("main") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("main") {
            MainScreen()
        }
    }
}
