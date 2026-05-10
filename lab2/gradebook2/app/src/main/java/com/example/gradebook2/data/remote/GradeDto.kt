package com.example.gradebook2.data.remote

import com.example.gradebook2.data.model.GradeRecord
import com.google.gson.annotations.SerializedName

// Lab 9, Task 2 — DTO matching the MockAPI JSON structure; all fields nullable for safe deserialization
data class GradeDto(
    @SerializedName("id")          val id: String?          = null,
    @SerializedName("subject")     val subject: String?     = null,
    @SerializedName("grade")       val grade: Int?          = null,
    @SerializedName("label")       val label: String?       = null,
    @SerializedName("category")    val category: String?    = null,
    @SerializedName("professor")   val professor: String?   = null,
    @SerializedName("description") val description: String? = null
)

// Lab 9, Task 2 — deserialize network response → local model, preserving existing isFavorite
fun GradeDto.toGradeRecord(isFavorite: Boolean = false) = GradeRecord(
    id          = id ?: java.util.UUID.randomUUID().toString(),
    subject     = subject ?: "",
    grade       = grade ?: 0,
    label       = label ?: "",
    category    = category ?: "",
    professor   = professor ?: "",
    description = description ?: "",
    isFavorite  = isFavorite
)

// Lab 9, Task 4 — POST-only DTO; no id field so MockAPI assigns it automatically (null id causes 400)
data class GradeCreateDto(
    @SerializedName("subject")     val subject: String,
    @SerializedName("grade")       val grade: Int,
    @SerializedName("label")       val label: String,
    @SerializedName("category")    val category: String,
    @SerializedName("professor")   val professor: String,
    @SerializedName("description") val description: String
)

fun GradeRecord.toCreateDto() = GradeCreateDto(
    subject     = subject,
    grade       = grade,
    label       = label,
    category    = category,
    professor   = professor,
    description = description
)
