package com.example.gradebook2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.example.gradebook2.ui.navigation.RootNavigation
import com.example.gradebook2.ui.theme.AppTheme
import com.example.gradebook2.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {

    // Scoped to Activity — той самий екземпляр, що і viewModel() у ProfileTabContent
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDark by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
            AppTheme(darkTheme = isDark) {
                RootNavigation()
            }
        }
    }
}
