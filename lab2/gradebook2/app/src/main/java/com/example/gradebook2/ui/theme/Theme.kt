package com.example.gradebook2.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Тема застосунку (не частина вимог ЛР №6; підключена з [com.example.gradebook2.MainActivity]). */
@Composable
fun DigitalGradebookTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(background = Color(0xFFF3F4F6)),
        content = content
    )
}
