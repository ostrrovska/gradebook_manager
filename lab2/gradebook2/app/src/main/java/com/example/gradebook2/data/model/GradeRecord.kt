package com.example.gradebook2.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

// Lab 8, Task 2 — Room entity; maps to "grade_records" table
@Entity(tableName = "grade_records")
data class GradeRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val subject: String,
    val grade: Int,
    val label: String,
    val category: String,
    val professor: String,
    val description: String = "",
    // Lab 8, Task 3 — persistent favorite flag stored in local DB only
    val isFavorite: Boolean = false
)
