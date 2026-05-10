package com.example.gradebook2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gradebook2.data.model.CalendarEvent
import com.example.gradebook2.data.model.GradeRecord

// Lab 8, Task 2 — Singleton Room database; version bump needed if schema changes
@Database(
    entities = [GradeRecord::class, CalendarEvent::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gradeRecordDao(): GradeRecordDao
    abstract fun calendarEventDao(): CalendarEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gradebook.db"
                ).build().also { INSTANCE = it }
            }
    }
}
