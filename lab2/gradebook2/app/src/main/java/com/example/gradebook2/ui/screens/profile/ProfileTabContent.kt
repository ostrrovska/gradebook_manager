package com.example.gradebook2.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gradebook2.ui.components.CalendarEventItem
import com.example.gradebook2.ui.components.EventInputPanel

/**
 * ЛР №6 — Завдання 4: View профілю; дані з [ProfileViewModel] через `collectAsStateWithLifecycle`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTabContent(
    initialUserName: String,
    vm: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory(initialUserName))
) {
    val userName by vm.userName.collectAsStateWithLifecycle()
    val events by vm.events.collectAsStateWithLifecycle()
    val inputText by vm.inputText.collectAsStateWithLifecycle()
    val selectedCategory by vm.selectedCategory.collectAsStateWithLifecycle()
    val selectedDeadline by vm.selectedDeadline.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text("User Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = userName,
                onValueChange = { vm.onUserNameChange(it) },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("App Information", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Name: Digital Gradebook", color = Color.DarkGray)
                    Text("Version: 1.0.0", color = Color.DarkGray)
                    Text("Developer: Kateryna", color = Color.DarkGray)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            Text("My Tasks", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            EventInputPanel(
                inputText = inputText,
                onTextChange = { vm.onInputTextChange(it) },
                selectedCategory = selectedCategory,
                onCategoryChange = { vm.onCategoryChange(it) },
                selectedDeadline = selectedDeadline,
                onDeadlineChange = { vm.onDeadlineChange(it) },
                onAddClick = { vm.addEvent() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (events.isEmpty()) {
            item {
                Text(
                    text = "No tasks yet. Add your first event above.",
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(events, key = { it.id }) { event ->
                CalendarEventItem(
                    event = event,
                    onDelete = { vm.deleteEvent(event) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
