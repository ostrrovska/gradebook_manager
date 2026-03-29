package com.example.gradebook2.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gradebook2.data.model.GradeRecord
import com.example.gradebook2.data.repository.AppRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ЛР №6 — Завдання 2: ViewModel для першої вкладки Tab (список предметів).
 *
 * Вимоги методички:
 * — успадковує [androidx.lifecycle.ViewModel];
 * — реактивний стан через [MutableStateFlow] / [StateFlow];
 * — імітація async-завантаження з затримкою в діапазоні 0.5–1 с (тут 700 ms) + індикатор у View;
 * — відфільтрований список за категорією;
 * — без імпорту UI-фреймворку (Compose тощо).
 *
 * Див. підписку у View: [ListTabContent] (`collectAsStateWithLifecycle`, `viewModel(factory = ...)`).
 */
class ListViewModel(private val repository: AppRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _allSubjects = MutableStateFlow<List<GradeRecord>>(emptyList())

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _filteredSubjects = MutableStateFlow<List<GradeRecord>>(emptyList())
    val filteredSubjects: StateFlow<List<GradeRecord>> = _filteredSubjects.asStateFlow()

    val categories: List<String> = repository.getCategories()

    init {
        loadSubjects()
    }

    /** Імітує асинхронне завантаження з репозиторію */
    private fun loadSubjects() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(700) // імітація затримки мережі
            _allSubjects.value = repository.getAllSubjects()
            applyFilter()
            _isLoading.value = false
        }
    }

    /** Оновлює обраний фільтр та перераховує відфільтрований список */
    fun selectFilter(filter: String) {
        _selectedFilter.value = filter
        applyFilter()
    }

    private fun applyFilter() {
        val filter = _selectedFilter.value
        _filteredSubjects.value = if (filter == "All") {
            _allSubjects.value
        } else {
            _allSubjects.value.filter { it.category == filter }
        }
    }

    /** Factory для створення ViewModel з параметром repository */
    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ListViewModel(repository) as T
        }
    }
}
