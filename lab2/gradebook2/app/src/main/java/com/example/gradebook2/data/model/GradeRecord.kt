package com.example.gradebook2.data.model

import java.util.UUID

/** ЛР №6 — Завдання 1: модель предмета оцінки (дані з репозиторію). */
data class GradeRecord(
    val id: String = UUID.randomUUID().toString(),
    val subject: String,
    val grade: Int,
    val label: String,
    val category: String,
    val professor: String,
    val description: String = "Detailed description for this subject..."
)
