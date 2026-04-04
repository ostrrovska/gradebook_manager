package com.example.gradebook2.ui.theme

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel для збереження і перемикання теми (Light / Dark).
 * Стан зберігається у SharedPreferences — живе між перезапусками застосунку.
 * Скопований у ViewModelStore Activity, тому той самий екземпляр доступний і з
 * [com.example.gradebook2.MainActivity] і з ProfileTabContent через viewModel().
 */
class ThemeViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean(KEY_DARK, false))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggle() {
        val newValue = !_isDarkTheme.value
        _isDarkTheme.value = newValue
        prefs.edit().putBoolean(KEY_DARK, newValue).apply()
    }

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_DARK   = "dark_theme"
    }
}
