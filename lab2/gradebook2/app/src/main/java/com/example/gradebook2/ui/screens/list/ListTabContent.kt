package com.example.gradebook2.ui.screens.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gradebook2.GradeApplication
import com.example.gradebook2.ui.components.SubjectListItem
import com.example.gradebook2.ui.theme.AppTheme
import com.example.gradebook2.ui.theme.PreviewBothThemes

// Lab 8, Task 2 + Lab 9, Tasks 3-5 — list driven by Room + network; offline banner + error/retry
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListTabContent(
    onItemClick: (String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val app = LocalContext.current.applicationContext as GradeApplication
    val vm: ListViewModel = viewModel(
        factory = ListViewModel.Factory(app.repository, app.prefsRepository)
    )

    val isLoading       by vm.isLoading.collectAsStateWithLifecycle()
    val isOffline       by vm.isOffline.collectAsStateWithLifecycle()
    val errorMessage    by vm.errorMessage.collectAsStateWithLifecycle()
    val snackbarMessage by vm.snackbarMessage.collectAsStateWithLifecycle()
    val visibleItems    by vm.visibleItems.collectAsStateWithLifecycle()
    val selectedFilter  by vm.selectedFilter.collectAsStateWithLifecycle()
    val showFavs        by vm.showFavoritesOnly.collectAsStateWithLifecycle()
    val categories      by vm.categories.collectAsStateWithLifecycle()
    val deletingIds     by vm.deletingIds.collectAsStateWithLifecycle()

    // Lab 9, Task 5 — show Snackbar for failed delete/add from ListViewModel
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearSnackbar()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Lab 9, Task 5 — offline banner at top; amber container colour from Material 3 scheme
        if (isOffline && visibleItems.isNotEmpty()) {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.SignalWifiOff, contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint     = MaterialTheme.colorScheme.onTertiaryContainer)
                Text(
                    "Offline — showing cached data",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            // ── Filter chips ──────────────────────────────────────────────────
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Filter by Category:", style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)

                // Lab 8, Task 3 — favorites toggle; state persisted in DataStore (Task 5)
                IconToggleButton(
                    checked         = showFavs,
                    onCheckedChange = { vm.toggleShowFavorites() }
                ) {
                    Icon(
                        imageVector        = if (showFavs) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (showFavs) "Show all" else "Show favorites",
                        tint               = if (showFavs) MaterialTheme.colorScheme.tertiary
                                             else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick  = { vm.selectFilter(filter) },
                        label    = { Text(filter, style = MaterialTheme.typography.labelMedium) },
                        shape    = CircleShape
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            // ── Content area ──────────────────────────────────────────────────
            when {
                // Loading spinner while first network request is in-flight
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                // Lab 9, Task 5 — full-screen error + retry button (no cached data available)
                errorMessage != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text      = errorMessage ?: "Unknown error",
                                style     = MaterialTheme.typography.bodyLarge,
                                color     = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = vm::loadData) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(Modifier.height(4.dp))
                                Text("Retry")
                            }
                        }
                    }
                }

                // Lab 8, Task 3 — empty state (favorites filter with no favorites yet)
                visibleItems.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text      = if (showFavs) "No favorites yet. Tap ☆ on a grade to save it."
                                        else "No grades found.",
                            style     = MaterialTheme.typography.bodyMedium,
                            color     = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Lab 8, Task 2 — main list from Room; Lab 8 Task 3 — favorite/delete buttons
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding      = PaddingValues(bottom = 80.dp)
                    ) {
                        items(visibleItems, key = { it.id }) { subject ->
                            SubjectListItem(
                                gradeRecord = subject,
                                onClick     = { onItemClick(subject.id) },
                                onFavorite  = { vm.toggleFavorite(it) },   // Lab 8, Task 3
                                onDelete    = { vm.deleteGrade(it) },       // Lab 9, Task 4
                                isDeleting  = subject.id in deletingIds     // Lab 9, Task 4
                            )
                        }
                    }
                }
            }
        }
    }
}

// Lab 9 — @Preview for loading state
@PreviewBothThemes
@Composable
private fun ListLoadingPreview() {
    AppTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

// Lab 9 — @Preview for error state
@PreviewBothThemes
@Composable
private fun ListErrorPreview() {
    AppTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                Text("No connection and no cached data.", color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                Button(onClick = {}) {
                    Icon(Icons.Default.Refresh, null)
                    Spacer(Modifier.height(4.dp))
                    Text("Retry")
                }
            }
        }
    }
}
