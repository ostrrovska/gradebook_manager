package com.example.gradebook2.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gradebook2.data.model.CalendarEvent
import com.example.gradebook2.data.preferences.UserPreferencesRepository
import com.example.gradebook2.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Lab 8, Task 1 — all profile settings read from / written to DataStore via Flow → StateFlow
class ProfileViewModel(
    private val repository: AppRepository,
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    // Lab 8, Task 1 — userName persisted in DataStore; shown immediately on next launch
    val userName: StateFlow<String> = prefsRepository.userNameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    // Lab 8, Task 1, Task 5 — sort mode setting; changes applied to grid tab reactively
    val sortMode: StateFlow<String> = prefsRepository.sortModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "By Name")

    // Lab 8, Task 1, Task 5 — favorites filter setting; changes applied to list tab reactively
    val showFavorites: StateFlow<Boolean> = prefsRepository.showFavoritesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Lab 8, Task 2 — calendar events now come from Room, not in-memory list
    val events: StateFlow<List<CalendarEvent>> = repository.observeEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _inputText        = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Core")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedDeadline = MutableStateFlow(System.currentTimeMillis())
    val selectedDeadline: StateFlow<Long> = _selectedDeadline.asStateFlow()

    fun onInputTextChange(text: String) { _inputText.value = text }
    fun onCategoryChange(cat: String)   { _selectedCategory.value = cat }
    fun onDeadlineChange(ms: Long)      { _selectedDeadline.value = ms }

    // Lab 8, Task 1 — persist name change immediately to DataStore
    fun saveUserName(name: String) {
        viewModelScope.launch { prefsRepository.saveUserName(name) }
    }

    // Lab 8, Task 1, Task 5 — persist sort mode; GridViewModel picks it up reactively
    fun saveSortMode(mode: String) {
        viewModelScope.launch { prefsRepository.saveSortMode(mode) }
    }

    // Lab 8, Task 1, Task 5 — persist favorites toggle; ListViewModel picks it up reactively
    fun saveShowFavorites(show: Boolean) {
        viewModelScope.launch { prefsRepository.saveShowFavorites(show) }
    }

    // Lab 8, Task 2 — events now persist to Room
    fun addEvent() {
        val title = _inputText.value.trim()
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addEvent(
                CalendarEvent(title = title, category = _selectedCategory.value, deadlineMs = _selectedDeadline.value)
            )
            _inputText.value = ""
        }
    }

    // Lab 8, Task 2 — delete from Room
    fun deleteEvent(event: CalendarEvent) {
        viewModelScope.launch { repository.deleteEvent(event) }
    }

    class Factory(
        private val repository: AppRepository,
        private val prefsRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ProfileViewModel(repository, prefsRepository) as T
    }
}
