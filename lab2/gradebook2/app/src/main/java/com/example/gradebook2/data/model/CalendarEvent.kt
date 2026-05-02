package com.example.gradebook2.data.model

import java.util.UUID

data class CalendarEvent(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val deadlineMs: Long
)
