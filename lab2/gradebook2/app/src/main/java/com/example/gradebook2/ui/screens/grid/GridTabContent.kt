package com.example.gradebook2.ui.screens.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gradebook2.GradeApplication
import com.example.gradebook2.ui.components.SubjectGridItem
import com.example.gradebook2.ui.theme.AppTheme
import com.example.gradebook2.ui.theme.PreviewBothThemes

// Lab 8, Task 5 — sort option persisted in DataStore; restored on next app launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridTabContent(
    onItemClick: (String) -> Unit
) {
    val app = LocalContext.current.applicationContext as GradeApplication
    val vm: GridViewModel = viewModel(
        factory = GridViewModel.Factory(app.repository, app.prefsRepository)
    )

    val isLoading      by vm.isLoading.collectAsStateWithLifecycle()
    val sortedSubjects by vm.sortedSubjects.collectAsStateWithLifecycle()
    val sortOption     by vm.sortOption.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Sort by:", style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.sortOptions) { option ->
                FilterChip(
                    selected = sortOption == option,
                    // Lab 8, Task 5 — persists selection to DataStore
                    onClick  = { vm.selectSortOption(option) },
                    label    = { Text(option, style = MaterialTheme.typography.labelMedium) },
                    shape    = CircleShape
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyVerticalGrid(
                columns               = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp)
            ) {
                items(sortedSubjects, key = { it.id }) { subject ->
                    SubjectGridItem(subject) { onItemClick(subject.id) }
                }
            }
        }
    }
}

@PreviewBothThemes
@Composable
private fun GridTabContentPreview() {
    AppTheme {
        GridTabContent(onItemClick = {})
    }
}
