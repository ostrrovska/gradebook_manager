package com.example.gradebook2.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gradebook2.data.model.CalendarEvent
import kotlinx.coroutines.flow.Flow

// Lab 8, Task 2 — DAO for calendar events; ordered by deadline ascending
@Dao
interface CalendarEventDao {

    @Query("SELECT * FROM calendar_events ORDER BY deadlineMs ASC")
    fun observeAll(): Flow<List<CalendarEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: CalendarEvent)

    @Delete
    suspend fun delete(event: CalendarEvent)
}
