package com.example.gradebook2.ui.screens.grid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gradebook2.data.model.GradeRecord
import com.example.gradebook2.data.preferences.UserPreferencesRepository
import com.example.gradebook2.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Lab 8, Task 2 — grid reads from Room; Lab 8, Task 5 — sort persisted in DataStore
class GridViewModel(
    private val repository: AppRepository,
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Lab 8, Task 5 — sort option starts from DataStore, then follows user interaction
    private val _sortOption = MutableStateFlow("By Name")
    val sortOption: StateFlow<String> = _sortOption.asStateFlow()

    val sortOptions = listOf("By Name", "By Grade")

    // Lab 8, Task 2 + Task 5 — reactive combination of Room stream and sort preference
    val sortedSubjects: StateFlow<List<GradeRecord>> = combine(
        repository.observeGrades(),
        _sortOption
    ) { grades, sort ->
        when (sort) {
            "By Name"  -> grades.sortedBy { it.subject }
            "By Grade" -> grades.sortedByDescending { it.grade }
            else       -> grades
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Lab 8, Task 5 — load persisted sort preference so grid opens with saved setting
        viewModelScope.launch {
            prefsRepository.sortModeFlow.collect { _sortOption.value = it }
        }
    }

    // Lab 8, Task 5 — persist chosen sort so it survives app restarts
    fun selectSortOption(option: String) {
        _sortOption.value = option
        viewModelScope.launch { prefsRepository.saveSortMode(option) }
    }

    class Factory(
        private val repository: AppRepository,
        private val prefsRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            GridViewModel(repository, prefsRepository) as T
    }
}
