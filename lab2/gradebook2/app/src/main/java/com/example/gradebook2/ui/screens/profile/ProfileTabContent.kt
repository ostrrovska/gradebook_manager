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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.example.gradebook2.GradeApplication
import com.example.gradebook2.ui.components.CalendarEventItem
import com.example.gradebook2.ui.components.EventInputPanel
import com.example.gradebook2.ui.theme.AppTheme
import com.example.gradebook2.ui.theme.PreviewBothThemes
import com.example.gradebook2.ui.theme.ThemeViewModel

// Lab 8, Task 1 — profile reads all settings from DataStore via ProfileViewModel
// Lab 8, Task 5 — sort/favorites changes here are reflected in grid/list tabs immediately
@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTabContent() {
    val app = LocalContext.current.applicationContext as GradeApplication
    val vm: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(app.repository, app.prefsRepository)
    )

    val userName      by vm.userName.collectAsStateWithLifecycle()
    val sortMode      by vm.sortMode.collectAsStateWithLifecycle()
    val showFavorites by vm.showFavorites.collectAsStateWithLifecycle()
    val events        by vm.events.collectAsStateWithLifecycle()
    val inputText     by vm.inputText.collectAsStateWithLifecycle()
    val selCategory   by vm.selectedCategory.collectAsStateWithLifecycle()
    val selDeadline   by vm.selectedDeadline.collectAsStateWithLifecycle()

    val activity = LocalActivity.current as ComponentActivity
    val themeVm: ThemeViewModel = viewModel(viewModelStoreOwner = activity)
    val isDark by themeVm.isDarkTheme.collectAsStateWithLifecycle()

    LazyColumn(
        modifier       = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // ── User profile ──────────────────────────────────────────────────────
        item {
            Text("User Profile", style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(12.dp))

            // Lab 8, Task 1 — username field; changes persisted to DataStore on each keystroke
            OutlinedTextField(
                value         = userName,
                onValueChange = { vm.saveUserName(it) },
                label         = { Text("Username") },
                modifier      = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))
        }

        // ── Settings (Lab 8, Task 1 — at least 2 persistent params) ──────────
        item {
            Text("Settings", style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(12.dp))
        }

        // ── Dark Mode ─────────────────────────────────────────────────────────
        item {
            SettingsCard(label = "Dark Mode", subtitle = if (isDark) "On" else "Off") {
                Switch(checked = isDark, onCheckedChange = { themeVm.toggle() })
            }
            Spacer(Modifier.height(8.dp))
        }

        // ── Lab 8, Task 1 / Task 5 — Default sort mode; persisted to DataStore ──
        item {
            Card(modifier = Modifier.fillMaxWidth(),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Default Sort Mode", style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text("Applied to Grid tab on launch",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("By Name", "By Grade").forEach { opt ->
                            FilterChip(
                                selected = sortMode == opt,
                                onClick  = { vm.saveSortMode(opt) },  // Lab 8, Task 5 — persists
                                label    = { Text(opt, style = MaterialTheme.typography.labelMedium) },
                                shape    = CircleShape
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // ── Lab 8, Task 1 / Task 5 — Show Favorites Only; persisted to DataStore ──
        item {
            SettingsCard(
                label    = "Show Favorites Only",
                subtitle = "Filters the List tab to starred grades"
            ) {
                Switch(
                    checked         = showFavorites,
                    onCheckedChange = { vm.saveShowFavorites(it) }  // Lab 8, Task 5 — persists
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        // ── App info ──────────────────────────────────────────────────────────
        item {
            Card(modifier = Modifier.fillMaxWidth(),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("App Information", style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(8.dp))
                    Text("Name: Digital Gradebook", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Version: 1.0.0", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Developer: Kateryna", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // ── Tasks / Events ────────────────────────────────────────────────────
        item {
            Text("My Tasks", style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(12.dp))
            EventInputPanel(
                inputText        = inputText,
                onTextChange     = vm::onInputTextChange,
                selectedCategory = selCategory,
                onCategoryChange = vm::onCategoryChange,
                selectedDeadline = selDeadline,
                onDeadlineChange = vm::onDeadlineChange,
                onAddClick       = vm::addEvent
            )
            Spacer(Modifier.height(16.dp))
        }

        if (events.isEmpty()) {
            item {
                Text("No tasks yet. Add your first event above.",
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier  = Modifier.fillMaxWidth().padding(top = 16.dp),
                    textAlign = TextAlign.Center)
            }
        } else {
            // Lab 8, Task 2 — events from Room, persisted between launches
            items(events, key = { it.id }) { event ->
                CalendarEventItem(event = event, onDelete = { vm.deleteEvent(event) })
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun SettingsCard(label: String, subtitle: String, trailing: @Composable () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(label, style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            trailing()
        }
    }
}

@PreviewBothThemes
@Composable
private fun ProfileTabContentPreview() {
    AppTheme {
        ProfileTabContent()
    }
}
