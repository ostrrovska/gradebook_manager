package com.example.gradebook2.ui.screens.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.gradebook2.ui.components.SubjectListItem

/**
 * ЛР №6 — Завдання 2: View (Composable) для списку.
 *
 * Вимоги методички:
 * — підписка на [ListViewModel] через `collectAsStateWithLifecycle()`;
 * — створення VM: `viewModel(factory = ListViewModel.Factory(...))`;
 * — дії користувача → методи ViewModel (`selectFilter`);
 * — колбек навігації [onItemClick] лишається параметром View (Завдання 5).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListTabContent(
    onItemClick: (String) -> Unit, // Колбек навігації залишається у View
    vm: ListViewModel = viewModel(factory = ListViewModel.Factory(appRepository))
) {
    val isLoading by vm.isLoading.collectAsStateWithLifecycle()
    val filteredSubjects by vm.filteredSubjects.collectAsStateWithLifecycle()
    val selectedFilter by vm.selectedFilter.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Filter by Category:", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.categories) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { vm.selectFilter(filter) }, // дія передається у ViewModel
                    label = { Text(filter) },
                    shape = CircleShape
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            // Індикатор завантаження під час імітації затримки
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6200EA))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredSubjects, key = { it.id }) { subject ->
                    SubjectListItem(subject) { onItemClick(subject.id) }
                }
            }
        }
    }
}
