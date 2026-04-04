package com.example.gradebook2.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Reference palette ────────────────────────────────────────────────────────

// Purple family (primary brand)
val Purple10  = Color(0xFF21005D)
val Purple20  = Color(0xFF381E72)
val Purple30  = Color(0xFF4F378B)
val Purple40  = Color(0xFF6650A4)
val Purple80  = Color(0xFFD0BCFF)
val Purple90  = Color(0xFFE8DEF8)
val Purple95  = Color(0xFFF6EDFF)
val Purple100 = Color(0xFFFFFFFF)

// Deep purple (accent — kept from original brand color 0xFF6200EA)
val DeepPurple20 = Color(0xFF4A00B0)  // darker brand primary for light theme
val DeepPurple40 = Color(0xFF6200EA)
val DeepPurple80 = Color(0xFFD0BCFF)
val DeepPurple90 = Color(0xFFE8DEF8)

// Teal family (secondary)
val Teal20  = Color(0xFF003734)
val Teal30  = Color(0xFF004F4B)
val Teal40  = Color(0xFF018786)
val Teal80  = Color(0xFF80CBC4)
val Teal90  = Color(0xFFB2DFDB)

// Rose family (tertiary)
val Rose10  = Color(0xFF31111D)
val Rose20  = Color(0xFF492532)
val Rose30  = Color(0xFF633B48)
val Rose40  = Color(0xFF7D5260)
val Rose80  = Color(0xFFEFB8C8)
val Rose90  = Color(0xFFFFD8E4)

// Neutral / surface
val Neutral10  = Color(0xFF1C1B1F)
val Neutral20  = Color(0xFF2D2D2D)
val Neutral90  = Color(0xFFE6E1E5)
val Neutral95  = Color(0xFFF3F4F6)   // original light background
val Neutral99  = Color(0xFFFFFFFF)   // white

// Neutral-variant
val NeutralVar30 = Color(0xFF49454F)
val NeutralVar50 = Color(0xFF79747E)
val NeutralVar60 = Color(0xFF938F99)
val NeutralVar80 = Color(0xFFCAC4D0)
val NeutralVar90 = Color(0xFFE7E0EC)

// Error
val Error10  = Color(0xFF410002)
val Error30  = Color(0xFF93000A)
val Error40  = Color(0xFFE53935)
val Error80  = Color(0xFFFF6B6B)
val Error90  = Color(0xFFFFDAD6)

// ── Semantic color schemes (used by MaterialTheme) ────────────────────────────

// lightColorScheme / darkColorScheme are assembled in Theme.kt

// ── App-specific extended color tokens ────────────────────────────────────────
// These are gradebook-domain colours (grades, categories, deadlines) exposed
// through [LocalExtendedColors] / [AppTheme] so that no composable needs to
// hard-code a colour literal.

data class ExtendedColors(
    /** Colour for a grade ≥ 90 (Excellent) */
    val gradeExcellent: Color,
    /** Colour for a grade 75–89 (Good) */
    val gradeGood: Color,
    /** Colour for a grade < 75 (Average) */
    val gradeAverage: Color,
    /** Badge / chip colour for "Core" category */
    val categoryCore: Color,
    /** Badge / chip colour for "Elective" category */
    val categoryElective: Color,
    /** Badge / chip colour for "Project" category */
    val categoryProject: Color,
    /** Deadline / urgency highlight colour */
    val deadlineHighlight: Color,
)

val LightExtendedColors = ExtendedColors(
    gradeExcellent    = Color(0xFF2E7D32),   // green-800
    gradeGood         = Color(0xFF1565C0),   // blue-800
    gradeAverage      = Color(0xFFE65100),   // orange-900
    categoryCore      = DeepPurple40,
    categoryElective  = Teal40,
    categoryProject   = Rose40,
    deadlineHighlight = Error40,
)

val DarkExtendedColors = ExtendedColors(
    gradeExcellent    = Color(0xFF81C784),   // green-300
    gradeGood         = Color(0xFF64B5F6),   // blue-300
    gradeAverage      = Color(0xFFFFB74D),   // orange-300
    categoryCore      = Purple80,
    categoryElective  = Teal80,
    categoryProject   = Rose80,
    deadlineHighlight = Error80,
)

/** Composition local that carries the current [ExtendedColors] instance. */
val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }
