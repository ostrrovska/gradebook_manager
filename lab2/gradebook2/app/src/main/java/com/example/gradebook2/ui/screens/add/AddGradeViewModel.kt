package com.example.gradebook2.ui.screens.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gradebook2.data.model.GradeRecord
import com.example.gradebook2.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Lab 9, Task 4 — ViewModel for the "Add Grade" form; POSTs to API on save
class AddGradeViewModel(private val repository: AppRepository) : ViewModel() {

    val labels     = listOf("Excellent", "Good", "Average", "Poor")
    val categories = listOf("Core", "Elective", "Project")

    private val _subject     = MutableStateFlow("")
    val subject: StateFlow<String> = _subject.asStateFlow()

    private val _grade       = MutableStateFlow("")
    val grade: StateFlow<String> = _grade.asStateFlow()

    private val _label       = MutableStateFlow("Good")
    val label: StateFlow<String> = _label.asStateFlow()

    private val _category    = MutableStateFlow("Core")
    val category: StateFlow<String> = _category.asStateFlow()

    private val _professor   = MutableStateFlow("")
    val professor: StateFlow<String> = _professor.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    // Lab 9, Task 4 — disables Save button while POST is in-flight
    private val _isSaving    = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    // Lab 9, Task 4 — triggers navigation back on success
    private val _savedSuccessfully = MutableStateFlow(false)
    val savedSuccessfully: StateFlow<Boolean> = _savedSuccessfully.asStateFlow()

    // Lab 9, Task 5 — Snackbar message for POST failure
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onSubjectChange(v: String)     { _subject.value = v }
    fun onGradeChange(v: String)       { _grade.value = v }
    fun onLabelChange(v: String)       { _label.value = v }
    fun onCategoryChange(v: String)    { _category.value = v }
    fun onProfessorChange(v: String)   { _professor.value = v }
    fun onDescriptionChange(v: String) { _description.value = v }
    fun clearError()                   { _errorMessage.value = null }

    // Lab 9, Task 4 — validate fields then POST; lock button during request
    fun save() {
        val gradeInt = _grade.value.toIntOrNull()
        when {
            _subject.value.isBlank()  -> { _errorMessage.value = "Subject name is required"; return }
            gradeInt == null || gradeInt !in 0..100 -> { _errorMessage.value = "Grade must be 0–100"; return }
            _professor.value.isBlank() -> { _errorMessage.value = "Professor name is required"; return }
        }
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null
            val result = repository.createGrade(
                GradeRecord(
                    subject     = _subject.value.trim(),
                    grade       = gradeInt!!,
                    label       = _label.value,
                    category    = _category.value,
                    professor   = _professor.value.trim(),
                    description = _description.value.trim()
                )
            )
            _isSaving.value = false
            if (result.isSuccess) {
                _savedSuccessfully.value = true
            } else {
                _errorMessage.value = "Save failed: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AddGradeViewModel(repository) as T
    }
}
