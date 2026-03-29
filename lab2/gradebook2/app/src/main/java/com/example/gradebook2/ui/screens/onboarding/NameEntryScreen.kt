package com.example.gradebook2.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

/**
 * ЛР №5/№6 — екран вводу імені. ЛР №6, Завдання 4: `rememberSaveable` лише для локального UI-тексту поля
 * (дозволено методичкою); для бізнес-даних використовуйте ViewModel на відповідних екранах.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameEntryScreen(navController: NavHostController) {
    // UI-стан поля вводу — не бізнес-модель
    var text by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("How should we call you?", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("userName", text.trim())
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = text.isNotBlank()
        ) {
            Text("Save")
        }
    }
}
