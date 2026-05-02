package com.example.gradebook2.data.repository

import com.example.gradebook2.data.model.GradeRecord

class AppRepository {
    private val subjects = listOf(
        GradeRecord(subject = "Mobile Development", grade = 95, label = "Excellent", category = "Core", professor = "Dr. Smith"),
        GradeRecord(subject = "Data Engineering", grade = 92, label = "Excellent", category = "Core", professor = "Prof. Johnson"),
        GradeRecord(subject = "UI/UX Basics", grade = 96, label = "Excellent", category = "Elective", professor = "Ms. Wilson"),
        GradeRecord(subject = "Algorithms", grade = 88, label = "Good", category = "Core", professor = "Dr. Alan"),
        GradeRecord(subject = "Machine Learning", grade = 98, label = "Excellent", category = "Project", professor = "Dr. Turing")
    )

    fun getAllSubjects(): List<GradeRecord> = subjects

    fun getSubjectById(id: String): GradeRecord? = subjects.find { it.id == id }

    fun getCategories(): List<String> = listOf("All") + subjects.map { it.category }.distinct()
}

val appRepository = AppRepository()
