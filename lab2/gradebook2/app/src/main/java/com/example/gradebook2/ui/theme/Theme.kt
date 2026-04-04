package com.example.gradebook2.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview

// ── Light colour scheme ───────────────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary              = DeepPurple20,       // 0xFF4A00B0 — darker brand accent
    onPrimary            = Purple100,
    primaryContainer     = DeepPurple90,       // 0xFFE8DEF8 — light purple container
    onPrimaryContainer   = Purple10,
    secondary            = Teal40,             // 0xFF018786 — teal accent
    onSecondary          = Purple100,
    secondaryContainer   = Teal90,             // 0xFFB2DFDB
    onSecondaryContainer = Teal20,
    tertiary             = Rose40,             // 0xFF7D5260 — rose
    onTertiary           = Purple100,
    tertiaryContainer    = Rose90,             // 0xFFFFD8E4
    onTertiaryContainer  = Rose10,
    background           = Neutral95,          // 0xFFF3F4F6 — light grey bg (kept from original)
    onBackground         = Neutral10,
    surface              = Neutral99,          // 0xFFFFFFFF — white cards
    onSurface            = Neutral10,
    surfaceVariant       = NeutralVar90,       // 0xFFE7E0EC
    onSurfaceVariant     = NeutralVar30,       // 0xFF49454F — dark grey secondary text
    error                = Error40,            // 0xFFE53935
    onError              = Neutral99,
    errorContainer       = Error90,            // 0xFFFFDAD6
    onErrorContainer     = Error10,
    outline              = NeutralVar50,       // 0xFF79747E
    outlineVariant       = NeutralVar80,       // 0xFFCAC4D0
    scrim                = Neutral10,
    inverseSurface       = Neutral20,
    inverseOnSurface     = Neutral90,
    inversePrimary       = Purple80,
)

// ── Dark colour scheme ────────────────────────────────────────────────────────

private val DarkColorScheme = darkColorScheme(
    primary              = Purple80,           // 0xFFD0BCFF — light purple on dark
    onPrimary            = Purple10,           // 0xFF21005D
    primaryContainer     = Purple30,           // 0xFF4F378B — medium purple container
    onPrimaryContainer   = Purple90,           // 0xFFE8DEF8
    secondary            = Teal80,             // 0xFF80CBC4 — light teal on dark
    onSecondary          = Teal20,             // 0xFF003734
    secondaryContainer   = Teal30,             // 0xFF004F4B
    onSecondaryContainer = Teal90,             // 0xFFB2DFDB
    tertiary             = Rose80,             // 0xFFEFB8C8 — light rose on dark
    onTertiary           = Rose20,             // 0xFF492532
    tertiaryContainer    = Rose30,             // 0xFF633B48
    onTertiaryContainer  = Rose90,             // 0xFFFFD8E4
    background           = Neutral10,          // 0xFF1C1B1F — very dark bg
    onBackground         = Neutral90,          // 0xFFE6E1E5
    surface              = Neutral20,          // 0xFF2D2D2D — dark surface / cards
    onSurface            = Neutral90,
    surfaceVariant       = NeutralVar30,       // 0xFF49454F
    onSurfaceVariant     = NeutralVar80,       // 0xFFCAC4D0 — lighter grey secondary text
    error                = Error80,            // 0xFFFF6B6B — lighter red
    onError              = Error10,            // 0xFF690005
    errorContainer       = Error30,            // 0xFF93000A
    onErrorContainer     = Error90,            // 0xFFFFDAD6
    outline              = NeutralVar60,       // 0xFF938F99
    outlineVariant       = NeutralVar30,       // 0xFF49454F
    scrim                = Neutral10,
    inverseSurface       = Neutral90,          // 0xFFE6E1E5
    inverseOnSurface     = Neutral20,          // 0xFF313033
    inversePrimary       = Purple40,           // 0xFF6650A4
)

// ── App theme ─────────────────────────────────────────────────────────────────

/**
 * ЛР №7 — Завдання 3: головна тема застосунку.
 *
 * Об'єднує [LightColorScheme]/[DarkColorScheme], [AppTypography] та стандартні [Shapes].
 * Перемикання між світлою та темною темою відбувається автоматично через [isSystemInDarkTheme].
 * Розширені токени ([ExtendedColors]) передаються через [LocalExtendedColors].
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = AppTypography,
            shapes      = Shapes(),          // default Material 3 corner shapes
            content     = content
        )
    }
}

/** Backward-compatibility alias used by [com.example.gradebook2.MainActivity]. */
@Composable
fun DigitalGradebookTheme(content: @Composable () -> Unit) = AppTheme(content = content)

// ── Reusable preview annotation (ЛР №7 — Завдання 4) ─────────────────────────

/**
 * Custom multi-preview annotation that renders a composable in both Light and Dark themes
 * simultaneously, as required by ЛР №7, Завдання 4.
 *
 * Usage:
 * ```kotlin
 * @PreviewBothThemes
 * @Composable
 * fun MyComposablePreview() {
 *     AppTheme { MyComposable() }
 * }
 * ```
 */
@Preview(name = "Light Theme", showBackground = true)
@Preview(
    name = "Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class PreviewBothThemes
