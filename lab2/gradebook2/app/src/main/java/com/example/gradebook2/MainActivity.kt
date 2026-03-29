package com.example.gradebook2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.gradebook2.ui.navigation.RootNavigation
import com.example.gradebook2.ui.theme.DigitalGradebookTheme

/**
 * Точка входу застосунку (ЛР №5 + рефакторинг ЛР №6, MVVM).
 * Навігація та тема підключаються на рівні Activity; бізнес-логіка — у ViewModel і репозиторії.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DigitalGradebookTheme {
                RootNavigation()
            }
        }
    }
}
