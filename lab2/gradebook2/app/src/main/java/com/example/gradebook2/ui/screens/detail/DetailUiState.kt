package com.example.gradebook2.ui.screens.detail

import com.example.gradebook2.data.model.GradeRecord

/**
 * ЛР №6 — Завдання 3: стан екрану деталей через `sealed interface`.
 *
 * Варіанти: [Loading], [Success], [Error] — повне моделювання гілок у `when` у [DetailsScreen].
 */
sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val subject: GradeRecord, val relatedSubjects: List<GradeRecord>) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
