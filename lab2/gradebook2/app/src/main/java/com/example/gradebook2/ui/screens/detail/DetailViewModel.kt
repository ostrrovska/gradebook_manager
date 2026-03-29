package com.example.gradebook2.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gradebook2.data.repository.AppRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ЛР №6 — Завдання 3: ViewModel екрану деталей.
 *
 * Вимоги методички:
 * — дані через репозиторій за id;
 * — стани завантаження / успіх / помилка (не знайдено);
 * — додаткові дані: пов’язані предмети тієї ж категорії;
 * — `itemId` у конструкторі → [Factory] ([ViewModelProvider.Factory]);
 * — без NavController / Compose UI.
 */
class DetailViewModel(
    private val itemId: String,
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            delay(300) // коротка затримка для UX
            val subject = repository.getSubjectById(itemId)
            _uiState.value = if (subject != null) {
                // Обчислення пов'язаних предметів тієї ж категорії (без поточного)
                val related = repository.getAllSubjects()
                    .filter { it.category == subject.category && it.id != subject.id }
                DetailUiState.Success(subject = subject, relatedSubjects = related)
            } else {
                DetailUiState.Error("Subject with id=$itemId not found.")
            }
        }
    }

    /** Factory — обов'язкова для передачі itemId у конструктор ViewModel */
    class Factory(
        private val itemId: String,
        private val repository: AppRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel(itemId, repository) as T
        }
    }
}
