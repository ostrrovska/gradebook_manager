package com.example.gradebook2.data.model

import java.util.UUID

data class GradeRecord(
    val id: String = UUID.randomUUID().toString(),
    val subject: String,
    val grade: Int,
    val label: String,
    val category: String,
    val professor: String,
    val description: String = "Detailed description for this subject..."
)
