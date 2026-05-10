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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

@Composable
private fun gradeColor(grade: Int) = LocalExtendedColors.current.run {
    when {
        grade >= 90 -> gradeExcellent
        grade >= 75 -> gradeGood
        else        -> gradeAverage
    }
}

// Lab 8, Task 3 — onFavorite toggles isFavorite in Room
// Lab 9, Task 4 — onDelete triggers DELETE API call; isDeleting disables button during request
@Composable
fun SubjectListItem(
    gradeRecord: GradeRecord,
    onClick: () -> Unit,
    onFavorite: ((GradeRecord) -> Unit)? = null,
    onDelete: ((GradeRecord) -> Unit)? = null,
    isDeleting: Boolean = false
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        // Lab 8, Task 3 — star icon reflects persisted isFavorite from Room
        if (onFavorite != null) {
            IconButton(
                onClick  = { onFavorite(gradeRecord) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector        = if (gradeRecord.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (gradeRecord.isFavorite) "Unfavorite" else "Favorite",
                    tint               = if (gradeRecord.isFavorite) MaterialTheme.colorScheme.tertiary
                                         else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
            Text(
                text  = gradeRecord.subject,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = "${gradeRecord.professor} • ${gradeRecord.category}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text  = gradeRecord.grade.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = gradeColor(gradeRecord.grade)
        )

        // Lab 9, Task 4 — delete button; shows spinner while DELETE request is in-flight
        if (onDelete != null) {
            if (isDeleting) {
                CircularProgressIndicator(
                    modifier    = Modifier.padding(start = 8.dp).size(20.dp),
                    strokeWidth = 2.dp,
                    color       = MaterialTheme.colorScheme.error
                )
            } else {
                IconButton(
                    onClick  = { onDelete(gradeRecord) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint               = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@PreviewBothThemes
@Composable
private fun SubjectListItemPreview() {
    AppTheme {
        Column {
            SubjectListItem(
                gradeRecord = GradeRecord(subject = "Mobile Development", grade = 95,
                    label = "Excellent", category = "Core", professor = "Dr. Smith"),
                onClick     = {},
                onFavorite  = {},
                onDelete    = {}
            )
            Spacer(Modifier.height(8.dp))
            SubjectListItem(
                gradeRecord = GradeRecord(subject = "Algorithms", grade = 88, label = "Good",
                    category = "Core", professor = "Dr. Alan", isFavorite = true),
                onClick     = {},
                onFavorite  = {},
                onDelete    = {}
            )
        }
    }
}
