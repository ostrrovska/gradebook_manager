package com.example.gradebook2.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gradebook2.data.repository.appRepository
import com.example.gradebook2.ui.components.SubjectListItem
import com.example.gradebook2.ui.theme.AppTheme
import com.example.gradebook2.ui.theme.LocalExtendedColors
import com.example.gradebook2.ui.theme.PreviewBothThemes


@Composable
fun DetailsScreen(
    itemId: String,
    onBack: () -> Unit
) {
    val vm: DetailViewModel = viewModel(
        key     = itemId,
        factory = DetailViewModel.Factory(itemId, appRepository)
    )
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        IconButton(onClick = onBack, modifier = Modifier.padding(8.dp)) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        when (val state = uiState) {
            is DetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is DetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        state.message,
                        color     = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        style     = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            is DetailUiState.Success -> {
                val subject = state.subject
                val gradeColor = LocalExtendedColors.current.run {
                    when {
                        subject.grade >= 90 -> gradeExcellent
                        subject.grade >= 75 -> gradeGood
                        else                -> gradeAverage
                    }
                }

                LazyColumn(
                    modifier          = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint     = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            subject.subject,
                            style     = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            color     = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Professor: ${subject.professor}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Category: ${subject.category}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${subject.grade}",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = gradeColor
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            subject.description,
                            style     = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color     = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    if (state.relatedSubjects.isNotEmpty()) {
                        item {
                            Text(
                                "Related in \"${subject.category}\":",
                                style    = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.fillMaxWidth(),
                                color    = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(state.relatedSubjects, key = { it.id }) { related ->
                            SubjectListItem(related, onClick = {})
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}


@PreviewBothThemes
@Composable
private fun DetailsScreenPreview() {
    AppTheme {
        // Shows loading state in preview (VM initialises and triggers async load)
        DetailsScreen(itemId = "preview-id", onBack = {})
    }
}
