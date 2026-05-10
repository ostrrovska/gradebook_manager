package com.example.gradebook2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gradebook2.data.model.GradeRecord
import kotlinx.coroutines.flow.Flow

// Lab 8, Task 2 — DAO with CRUD for grade records; all reads return Flow for reactive UI
@Dao
interface GradeRecordDao {

    // Emits a new list on every DB change — drives UI reactively
    @Query("SELECT * FROM grade_records ORDER BY subject ASC")
    fun observeAll(): Flow<List<GradeRecord>>

    // Lab 8, Task 3 — separate stream for favorites-only view
    @Query("SELECT * FROM grade_records WHERE isFavorite = 1 ORDER BY subject ASC")
    fun observeFavorites(): Flow<List<GradeRecord>>

    @Query("SELECT * FROM grade_records WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): GradeRecord?

    // REPLACE strategy lets network data overwrite seed/cached data by id
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: GradeRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<GradeRecord>)

    @Query("DELETE FROM grade_records WHERE id = :id")
    suspend fun deleteById(id: String)

    // Lab 8, Task 3 — toggle favorite without touching other fields
    @Query("UPDATE grade_records SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: String, isFavorite: Boolean)

    // Lab 8, Task 4 — used to detect first launch (count == 0 → seed)
    @Query("SELECT COUNT(*) FROM grade_records")
    suspend fun count(): Int
}
