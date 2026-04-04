package com.example.gradebook2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import com.example.gradebook2.data.model.GradeRecord
import com.example.gradebook2.ui.theme.AppTheme
import com.example.gradebook2.ui.theme.LocalExtendedColors
import com.example.gradebook2.ui.theme.PreviewBothThemes

/** Determines grade display colour from [ExtendedColors] based on score. */
@Composable
private fun gradeColor(grade: Int) = LocalExtendedColors.current.run {
    when {
        grade >= 90 -> gradeExcellent
        grade >= 75 -> gradeGood
        else        -> gradeAverage
    }
}

@Composable
fun SubjectListItem(gradeRecord: GradeRecord, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = gradeRecord.subject,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${gradeRecord.professor} • ${gradeRecord.category}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = gradeRecord.grade.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = gradeColor(gradeRecord.grade)
        )
    }
}

// ── Previews (ЛР №7 — Завдання 4) ────────────────────────────────────────────

@PreviewBothThemes
@Composable
private fun SubjectListItemPreview() {
    AppTheme {
        SubjectListItem(
            gradeRecord = GradeRecord(
                subject     = "Mobile Development",
                grade       = 95,
                label       = "Excellent",
                category    = "Core",
                professor   = "Dr. Smith",
                description = "Sample description"
            ),
            onClick = {}
        )
    }
}
