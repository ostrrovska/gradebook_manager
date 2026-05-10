package com.example.gradebook2.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Lab 8, Task 1 — DataStore extension on Context; single file-backed instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_prefs")

// Lab 8, Task 1 — all user preferences with explicit typed keys; read via Flow → StateFlow in VMs
class UserPreferencesRepository(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        // Lab 8, Task 1 — explicit typed keys as required
        val KEY_USER_NAME     = stringPreferencesKey("user_name")
        val KEY_SORT_MODE     = stringPreferencesKey("sort_mode")    // Lab 8, Task 5 — grid sort
        val KEY_SHOW_FAVORITES = booleanPreferencesKey("show_favorites") // Lab 8, Task 5 — list filter
    }

    // Lab 8, Task 1 — empty string means first launch (onboarding not yet completed)
    val userNameFlow: Flow<String> = dataStore.data.map { it[KEY_USER_NAME] ?: "" }

    // Lab 8, Task 1, Task 5 — default sort preference applied to grid tab
    val sortModeFlow: Flow<String> = dataStore.data.map { it[KEY_SORT_MODE] ?: "By Name" }

    // Lab 8, Task 1, Task 5 — favorites-only filter applied to list tab
    val showFavoritesFlow: Flow<Boolean> = dataStore.data.map { it[KEY_SHOW_FAVORITES] ?: false }

    suspend fun saveUserName(name: String) {
        dataStore.edit { it[KEY_USER_NAME] = name }
    }

    suspend fun saveSortMode(mode: String) {
        dataStore.edit { it[KEY_SORT_MODE] = mode }
    }

    suspend fun saveShowFavorites(show: Boolean) {
        dataStore.edit { it[KEY_SHOW_FAVORITES] = show }
    }
}
