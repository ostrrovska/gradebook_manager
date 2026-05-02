package com.example.gradebook2.ui.screens.detail

import com.example.gradebook2.data.model.GradeRecord

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val subject: GradeRecord, val relatedSubjects: List<GradeRecord>) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
