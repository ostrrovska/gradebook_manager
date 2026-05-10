package com.example.gradebook2.data.repository

import android.util.Log
import com.example.gradebook2.data.local.CalendarEventDao
import com.example.gradebook2.data.local.GradeRecordDao
import com.example.gradebook2.data.model.CalendarEvent
import com.example.gradebook2.data.model.GradeRecord
import com.example.gradebook2.data.preferences.UserPreferencesRepository
import com.example.gradebook2.data.remote.GradeApiService
import com.example.gradebook2.data.remote.toCreateDto
import com.example.gradebook2.data.remote.toGradeRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

// Lab 8, Task 2 — wraps Room DAOs as reactive source of truth
// Lab 9, Task 3 — adds network layer; Room acts as cache when offline
class AppRepository(
    private val gradeDao: GradeRecordDao,
    private val eventDao: CalendarEventDao,
    private val apiService: GradeApiService,
    private val prefsRepository: UserPreferencesRepository
) {
    // Lab 8, Task 2 — reactive grade stream; UI re-renders automatically on DB change
    fun observeGrades(): Flow<List<GradeRecord>> = gradeDao.observeAll()

    // Lab 8, Task 3 — reactive favorites-only stream
    fun observeFavorites(): Flow<List<GradeRecord>> = gradeDao.observeFavorites()

    // Lab 8, Task 2 — reactive event stream
    fun observeEvents(): Flow<List<CalendarEvent>> = eventDao.observeAll()

    // Lab 9, Task 3 — network-first fetch; caches result in Room; returns failure if offline
    suspend fun refreshGrades(): Result<Unit> {
        return try {
            val response = apiService.getGrades()
            if (response.isSuccessful) {
                val dtos = response.body() ?: emptyList()
                // Preserve isFavorite flags that exist locally
                val favoriteIds = gradeDao.observeFavorites().first().map { it.id }.toSet()
                val records = dtos.map { dto ->
                    dto.toGradeRecord(isFavorite = dto.id in favoriteIds)
                }
                gradeDao.insertAll(records)
                Result.success(Unit)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            // Network unavailable — caller will show cached Room data with offline banner
            Log.w("AppRepository", "Network unavailable, using cache: ${e.message}")
            Result.failure(e)
        }
    }

    // Lab 9, Task 3 — fetch single grade from network for detail screen, cache result
    suspend fun getGradeById(id: String): GradeRecord? {
        return try {
            val response = apiService.getGradeById(id)
            if (response.isSuccessful) {
                val dto = response.body() ?: return gradeDao.getById(id)
                val existing = gradeDao.getById(id)
                val record = dto.toGradeRecord(isFavorite = existing?.isFavorite ?: false)
                gradeDao.insert(record)
                record
            } else {
                gradeDao.getById(id)
            }
        } catch (e: Exception) {
            gradeDao.getById(id) // fall back to local cache
        }
    }

    // Lab 9, Task 3 — snapshot of all grades for "related" section in detail screen
    suspend fun getAllGradesList(): List<GradeRecord> = gradeDao.observeAll().first()

    // Lab 9, Task 4 — POST to API, then insert returned record into local cache
    suspend fun createGrade(record: GradeRecord): Result<Unit> {
        return try {
            val response = apiService.createGrade(record.toCreateDto())
            if (response.isSuccessful) {
                response.body()?.let { dto -> gradeDao.insert(dto.toGradeRecord()) }
                Result.success(Unit)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lab 9, Task 4 — DELETE on API, then remove from local cache
    suspend fun deleteGrade(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteGrade(id)
            if (response.isSuccessful) {
                gradeDao.deleteById(id)
                Result.success(Unit)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lab 8, Task 3 — toggle favorite locally; not synced to API (device-only preference)
    suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        gradeDao.setFavorite(id, isFavorite)
    }

    // Lab 8, Task 2 — event persistence
    suspend fun addEvent(event: CalendarEvent) = eventDao.insert(event)
    suspend fun deleteEvent(event: CalendarEvent) = eventDao.delete(event)

    // Lab 8, Task 4 — insert seed data only on first launch (empty DB)
    suspend fun seedInitialDataIfNeeded() {
        if (gradeDao.count() == 0) {
            gradeDao.insertAll(seedGrades)
        }
    }

    private val seedGrades = listOf(
        GradeRecord(
            subject = "Mobile Development", grade = 95, label = "Excellent",
            category = "Core", professor = "Dr. Smith",
            description = "Android Kotlin with Jetpack Compose and modern MVVM architecture."
        ),
        GradeRecord(
            subject = "Data Engineering", grade = 92, label = "Excellent",
            category = "Core", professor = "Prof. Johnson",
            description = "Data pipelines, ETL processes and distributed computing fundamentals."
        ),
        GradeRecord(
            subject = "UI/UX Basics", grade = 96, label = "Excellent",
            category = "Elective", professor = "Ms. Wilson",
            description = "User interface design principles, prototyping and usability testing."
        ),
        GradeRecord(
            subject = "Algorithms", grade = 88, label = "Good",
            category = "Core", professor = "Dr. Alan",
            description = "Algorithm design, complexity analysis and practical optimization."
        ),
        GradeRecord(
            subject = "Machine Learning", grade = 98, label = "Excellent",
            category = "Project", professor = "Dr. Turing",
            description = "Supervised and unsupervised learning with hands-on projects."
        ),
        GradeRecord(
            subject = "Database Systems", grade = 82, label = "Good",
            category = "Core", professor = "Dr. Codd",
            description = "Relational algebra, SQL design and transaction management."
        ),
        GradeRecord(
            subject = "Computer Networks", grade = 78, label = "Good",
            category = "Elective", professor = "Prof. Tanenbaum",
            description = "Network protocols, TCP/IP stack and security fundamentals."
        )
    )
}
