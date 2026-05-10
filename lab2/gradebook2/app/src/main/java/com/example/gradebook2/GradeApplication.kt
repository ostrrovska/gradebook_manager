package com.example.gradebook2

import android.app.Application
import com.example.gradebook2.data.local.AppDatabase
import com.example.gradebook2.data.preferences.UserPreferencesRepository
import com.example.gradebook2.data.remote.RetrofitClient
import com.example.gradebook2.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

// Lab 8 — Application singleton: provides DB, DataStore, Repository to the whole app
class GradeApplication : Application() {

    // Application-scoped coroutine scope for one-time background work
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database by lazy { AppDatabase.getInstance(this) }
    val prefsRepository by lazy { UserPreferencesRepository(this) }
    val repository by lazy {
        AppRepository(
            gradeDao       = database.gradeRecordDao(),
            eventDao       = database.calendarEventDao(),
            apiService     = RetrofitClient.apiService,
            prefsRepository = prefsRepository
        )
    }

    override fun onCreate() {
        super.onCreate()
        // Lab 8, Task 4 — seed initial data once on first launch
        applicationScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }
}
