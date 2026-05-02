package com.example.gradebook2.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gradebook2.data.model.CalendarEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel(initialUserName: String) : ViewModel() {

    private val _userName = MutableStateFlow(initialUserName)
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _events = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val events: StateFlow<List<CalendarEvent>> = _events.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Core")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedDeadline = MutableStateFlow(System.currentTimeMillis())
    val selectedDeadline: StateFlow<Long> = _selectedDeadline.asStateFlow()

    fun onUserNameChange(name: String) { _userName.value = name }
    fun onInputTextChange(text: String) { _inputText.value = text }
    fun onCategoryChange(category: String) { _selectedCategory.value = category }
    fun onDeadlineChange(deadline: Long) { _selectedDeadline.value = deadline }

    fun addEvent() {
        val title = _inputText.value.trim()
        if (title.isBlank()) return
        _events.update { current ->
            current + CalendarEvent(
                title = title,
                category = _selectedCategory.value,
                deadlineMs = _selectedDeadline.value
            )
        }
        _inputText.value = ""
    }

    fun deleteEvent(event: CalendarEvent) {
        _events.update { it - event }
    }

    class Factory(private val initialUserName: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(initialUserName) as T
        }
    }
}
