package com.example.gradebook2.data.repository

import com.example.gradebook2.data.model.GradeRecord

/**
 * ЛР №6 — Завдання 1: шар Model (репозиторій).
 *
 * Вимоги методички:
 * — клас інкапсулює доступ до даних;
 * — немає імпорту `androidx.compose.*`;
 * — методи повертають `List<T>` / елемент за id / допоміжні дані (категорії для фільтра);
 * — тестові дані лише тут (не в Composable / View).
 *
 * Реалізація: `getAllSubjects()`, `getSubjectById()`, `getCategories()`.
 */
class AppRepository {
    private val subjects = listOf(
        GradeRecord(subject = "Mobile Development", grade = 95, label = "Excellent", category = "Core", professor = "Dr. Smith"),
        GradeRecord(subject = "Data Engineering", grade = 92, label = "Excellent", category = "Core", professor = "Prof. Johnson"),
        GradeRecord(subject = "UI/UX Basics", grade = 96, label = "Excellent", category = "Elective", professor = "Ms. Wilson"),
        GradeRecord(subject = "Algorithms", grade = 88, label = "Good", category = "Core", professor = "Dr. Alan"),
        GradeRecord(subject = "Machine Learning", grade = 98, label = "Excellent", category = "Project", professor = "Dr. Turing")
    )

    /** Повертає повний список предметів */
    fun getAllSubjects(): List<GradeRecord> = subjects

    /** Повертає предмет за ідентифікатором або null */
    fun getSubjectById(id: String): GradeRecord? = subjects.find { it.id == id }

    /** Повертає унікальні категорії для фільтрації */
    fun getCategories(): List<String> = listOf("All") + subjects.map { it.category }.distinct()
}

// Singleton-екземпляр репозиторію (у реальному проєкті — DI)
val appRepository = AppRepository()
