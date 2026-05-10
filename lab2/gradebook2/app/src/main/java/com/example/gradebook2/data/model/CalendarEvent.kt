package com.example.gradebook2.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

// Lab 8, Task 2 — Room entity; maps to "calendar_events" table
@Entity(tableName = "calendar_events")
data class CalendarEvent(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val deadlineMs: Long
)
