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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gradebook2.data.repository.appRepository
import com.example.gradebook2.ui.components.SubjectGridItem

/**
 * ЛР №6 — Завдання 4 + 5: View сітки; колбек [onItemClick] — навігація на деталі (параметр View).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridTabContent(
    onItemClick: (String) -> Unit,
    vm: GridViewModel = viewModel(factory = GridViewModel.Factory(appRepository))
) {
    val isLoading by vm.isLoading.collectAsStateWithLifecycle()
    val sortedSubjects by vm.sortedSubjects.collectAsStateWithLifecycle()
    val sortOption by vm.sortOption.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Sort by:", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.sortOptions) { option ->
                FilterChip(
                    selected = sortOption == option,
                    onClick = { vm.selectSortOption(option) },
                    label = { Text(option) },
                    shape = CircleShape
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6200EA))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sortedSubjects, key = { it.id }) { subject ->
                    SubjectGridItem(subject) { onItemClick(subject.id) }
                }
            }
        }
    }
}
