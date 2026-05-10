package com.example.gradebook2.ui.screens.list

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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Lab 8, Task 2 + Lab 9, Task 3 — manages list: Room as source of truth, network refresh on open
class ListViewModel(
    private val repository: AppRepository,
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    // Lab 9, Task 5 — three distinct visual states for the UI
    private val _isLoading      = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isOffline      = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    private val _errorMessage   = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Lab 9, Task 4/5 — Snackbar message for failed add/delete; null = no message
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Lab 9, Task 4 — tracks which item IDs are currently being deleted (disables buttons)
    private val _deletingIds    = MutableStateFlow<Set<String>>(emptySet())
    val deletingIds: StateFlow<Set<String>> = _deletingIds.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    // Lab 8, Task 3 + Task 5 — show-favorites toggle backed by DataStore
    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    // Lab 8, Task 2 — reactive category list derived from DB; updates when data changes
    val categories: StateFlow<List<String>> = repository.observeGrades()
        .map { grades -> listOf("All") + grades.map { it.category }.distinct().sorted() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("All"))

    // Lab 8, Task 2 + Task 3 + Task 5 — combine Room streams + UI filters reactively
    val visibleItems: StateFlow<List<GradeRecord>> = combine(
        repository.observeGrades(),
        repository.observeFavorites(),
        _showFavoritesOnly,
        _selectedFilter
    ) { all, favorites, showFavs, filter ->
        val source = if (showFavs) favorites else all
        if (filter == "All") source else source.filter { it.category == filter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Lab 8, Task 5 — restore persisted favorites preference from DataStore
        viewModelScope.launch {
            prefsRepository.showFavoritesFlow.collect { _showFavoritesOnly.value = it }
        }
        loadData()
    }

    // Lab 9, Task 3 — network-first load; offline → show cache with banner
    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.refreshGrades()
            _isLoading.value = false
            // IOException = true connectivity loss (UnknownHost, ConnectException, etc.)
            // HTTP 4xx/5xx responses are NOT offline — server was reachable
            _isOffline.value = result.isFailure && result.exceptionOrNull() is java.io.IOException
            if (result.isFailure && visibleItems.value.isEmpty()) {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Network error"
            }
        }
    }

    fun selectFilter(filter: String) { _selectedFilter.value = filter }

    // Lab 8, Task 3 + Task 5 — persist toggle; GridViewModel reacts via same DataStore key
    fun toggleShowFavorites() {
        viewModelScope.launch {
            val newValue = !_showFavoritesOnly.value
            prefsRepository.saveShowFavorites(newValue)
            _showFavoritesOnly.value = newValue
        }
    }

    // Lab 8, Task 3 — flip isFavorite in Room; both list and favorites streams update
    fun toggleFavorite(record: GradeRecord) {
        viewModelScope.launch {
            repository.toggleFavorite(record.id, !record.isFavorite)
        }
    }

    // Lab 9, Task 4 — DELETE via API then refresh; locks button during request
    fun deleteGrade(record: GradeRecord) {
        viewModelScope.launch {
            _deletingIds.update { it + record.id }
            val result = repository.deleteGrade(record.id)
            _deletingIds.update { it - record.id }
            if (result.isFailure) {
                _snackbarMessage.value = "Delete failed: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun clearSnackbar() { _snackbarMessage.value = null }

    class Factory(
        private val repository: AppRepository,
        private val prefsRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ListViewModel(repository, prefsRepository) as T
    }
}
