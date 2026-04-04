package com.example.gradebook2.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gradebook2.ui.theme.AppTheme
import com.example.gradebook2.ui.theme.PreviewBothThemes

/** ЛР №5/№6 — кореневий onboarding. ЛР №7: усі кольори/шрифти через MaterialTheme. */
@Composable
fun OnboardingScreen(navController: NavHostController) {
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val userName by savedStateHandle?.getStateFlow<String>("userName", "")
        ?.collectAsState(initial = "") ?: remember { mutableStateOf("") }

    Column(
        modifier              = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector        = Icons.Default.AccountCircle,
            contentDescription = "Logo",
            modifier           = Modifier.size(120.dp),
            tint               = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Digital Gradebook",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick  = { navController.navigate("name_entry") },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Enter your name", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick  = {
                navController.navigate("main/$userName") {
                    popUpTo("onboarding") { inclusive = true }
                }
            },
            enabled  = userName.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                if (userName.isEmpty()) "Get Started" else "Hello, $userName! Start",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

// ── Previews (ЛР №7 — Завдання 4) ────────────────────────────────────────────

@PreviewBothThemes
@Composable
private fun OnboardingScreenLightPreview() {
    AppTheme {
        // NavHostController cannot be used in preview — show a standalone column
        Column(
            modifier              = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector        = Icons.Default.AccountCircle,
                contentDescription = "Logo",
                modifier           = Modifier.size(120.dp),
                tint               = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Digital Gradebook",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick  = {},
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Enter your name", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick  = {},
                enabled  = false,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Get Started", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
