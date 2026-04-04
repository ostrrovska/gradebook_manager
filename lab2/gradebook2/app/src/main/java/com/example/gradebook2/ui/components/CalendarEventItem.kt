package com.example.gradebook2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.gradebook2.data.model.CalendarEvent
import com.example.gradebook2.ui.theme.AppTheme
import com.example.gradebook2.ui.theme.LocalExtendedColors
import com.example.gradebook2.ui.theme.PreviewBothThemes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CalendarEventItem(event: CalendarEvent, onDelete: () -> Unit) {
    val dateFormatter = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.US) }
    val deadlineColor = LocalExtendedColors.current.deadlineHighlight

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Icon(
            Icons.Default.DateRange,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = event.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text  = "Label: ${event.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text  = dateFormatter.format(Date(event.deadlineMs)),
                    style = MaterialTheme.typography.labelMedium,
                    color = deadlineColor
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete Task",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

// ── Previews (ЛР №7 — Завдання 4) ────────────────────────────────────────────

@PreviewBothThemes
@Composable
private fun CalendarEventItemPreview() {
    AppTheme {
        CalendarEventItem(
            event = CalendarEvent(
                title      = "Project Deadline",
                category   = "Core",
                deadlineMs = System.currentTimeMillis() + 86_400_000L
            ),
            onDelete = {}
        )
    }
}
