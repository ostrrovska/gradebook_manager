package com.example.gradebook2.ui.screens.profile

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gradebook2.ui.components.CalendarEventItem
import com.example.gradebook2.ui.components.EventInputPanel
import com.example.gradebook2.ui.theme.AppTheme
import com.example.gradebook2.ui.theme.PreviewBothThemes
import com.example.gradebook2.ui.theme.ThemeViewModel


@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTabContent(
    initialUserName: String,
    vm: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory(initialUserName))
) {
    val userName         by vm.userName.collectAsStateWithLifecycle()
    val events           by vm.events.collectAsStateWithLifecycle()
    val inputText        by vm.inputText.collectAsStateWithLifecycle()
    val selectedCategory by vm.selectedCategory.collectAsStateWithLifecycle()
    val selectedDeadline by vm.selectedDeadline.collectAsStateWithLifecycle()

    val activity = LocalActivity.current as ComponentActivity
    val themeVm: ThemeViewModel = viewModel(viewModelStoreOwner = activity)
    val isDark by themeVm.isDarkTheme.collectAsStateWithLifecycle()

    LazyColumn(
        modifier       = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text(
                "User Profile",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value         = userName,
                onValueChange = { vm.onUserNameChange(it) },
                label         = { Text("Username") },
                modifier      = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ── Theme toggle ──────────────────────────────────────────────────────
        item {
            Card(
                modifier  = Modifier.fillMaxWidth(),
                colors    = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Dark Mode",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            if (isDark) "Увімкнено" else "Вимкнено",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked         = isDark,
                        onCheckedChange = { themeVm.toggle() }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── App info ──────────────────────────────────────────────────────────
        item {
            Card(
                modifier  = Modifier.fillMaxWidth(),
                colors    = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "App Information",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Name: Digital Gradebook",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Version: 1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Developer: Kateryna",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // ── Tasks ─────────────────────────────────────────────────────────────
        item {
            Text(
                "My Tasks",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            EventInputPanel(
                inputText        = inputText,
                onTextChange     = { vm.onInputTextChange(it) },
                selectedCategory = selectedCategory,
                onCategoryChange = { vm.onCategoryChange(it) },
                selectedDeadline = selectedDeadline,
                onDeadlineChange = { vm.onDeadlineChange(it) },
                onAddClick       = { vm.addEvent() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (events.isEmpty()) {
            item {
                Text(
                    text      = "No tasks yet. Add your first event above.",
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier  = Modifier.fillMaxWidth().padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(events, key = { it.id }) { event ->
                CalendarEventItem(
                    event    = event,
                    onDelete = { vm.deleteEvent(event) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}


@PreviewBothThemes
@Composable
private fun ProfileTabContentPreview() {
    AppTheme {
        ProfileTabContent(initialUserName = "Kateryna")
    }
}
