package com.example.gradebook2.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gradebook2.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Lab 9, Task 3 — fetches detail from network (GET by id), falls back to Room cache
class DetailViewModel(
    private val itemId: String,
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init { loadDetail() }

    // Lab 9, Task 3 — separate GET request per detail open; not passed through navigation
    fun loadDetail() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            val subject = repository.getGradeById(itemId)
            _uiState.value = if (subject != null) {
                val related = repository.getAllGradesList()
                    .filter { it.category == subject.category && it.id != subject.id }
                DetailUiState.Success(subject = subject, relatedSubjects = related)
            } else {
                DetailUiState.Error("Grade not found. Check network connection.")
            }
        }
    }

    class Factory(
        private val itemId: String,
        private val repository: AppRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DetailViewModel(itemId, repository) as T
    }
}
