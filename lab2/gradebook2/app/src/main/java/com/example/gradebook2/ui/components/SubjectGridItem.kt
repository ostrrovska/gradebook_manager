package com.example.gradebook2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gradebook2.data.model.GradeRecord
import com.example.gradebook2.ui.theme.AppTheme
import com.example.gradebook2.ui.theme.LocalExtendedColors
import com.example.gradebook2.ui.theme.PreviewBothThemes

@Composable
fun SubjectGridItem(gradeRecord: GradeRecord, onClick: () -> Unit) {
    val gradeColor = LocalExtendedColors.current.run {
        when {
            gradeRecord.grade >= 90 -> gradeExcellent
            gradeRecord.grade >= 75 -> gradeGood
            else                    -> gradeAverage
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text  = gradeRecord.grade.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = gradeColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text      = gradeRecord.subject,
            style     = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            maxLines  = 2,
            color     = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text  = gradeRecord.category,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ── Previews (ЛР №7 — Завдання 4) ────────────────────────────────────────────

@PreviewBothThemes
@Composable
private fun SubjectGridItemPreview() {
    AppTheme {
        SubjectGridItem(
            gradeRecord = GradeRecord(
                subject     = "Data Engineering with a Long Name",
                grade       = 88,
                label       = "Good",
                category    = "Core",
                professor   = "Prof. Johnson",
                description = "Sample"
            ),
            onClick = {}
        )
    }
}
