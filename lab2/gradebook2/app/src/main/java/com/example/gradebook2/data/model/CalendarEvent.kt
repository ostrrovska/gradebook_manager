package com.example.gradebook2.data.model

import java.util.UUID

/** ЛР №6 — Завдання 4: модель події/задачі для вкладки Profile (стан у ProfileViewModel). */
data class CalendarEvent(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val deadlineMs: Long
)
