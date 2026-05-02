package com.example.gradebook2.ui.screens.grid

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

class GridViewModel(private val repository: AppRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sortOption = MutableStateFlow("By Name")
    val sortOption: StateFlow<String> = _sortOption.asStateFlow()

    private val _sortedSubjects = MutableStateFlow<List<GradeRecord>>(emptyList())
    val sortedSubjects: StateFlow<List<GradeRecord>> = _sortedSubjects.asStateFlow()

    val sortOptions = listOf("By Name", "By Grade")

    init {
        loadAndSort()
    }

    private fun loadAndSort() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(700)
            applySort()
            _isLoading.value = false
        }
    }

    fun selectSortOption(option: String) {
        _sortOption.value = option
        applySort()
    }

    private fun applySort() {
        val all = repository.getAllSubjects()
        _sortedSubjects.value = when (_sortOption.value) {
            "By Name" -> all.sortedBy { it.subject }
            "By Grade" -> all.sortedByDescending { it.grade }
            else -> all
        }
    }

    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GridViewModel(repository) as T
        }
    }
}
