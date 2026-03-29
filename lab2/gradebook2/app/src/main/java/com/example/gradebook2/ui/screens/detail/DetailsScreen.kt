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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gradebook2.data.repository.appRepository
import com.example.gradebook2.ui.components.SubjectListItem

/**
 * ЛР №6 — Завдання 3 + 5: View деталей + навігація назад.
 *
 * — VM: `viewModel(key = itemId, factory = DetailViewModel.Factory(itemId, ...))`;
 * — гілки UI: `when (uiState)` по [DetailUiState];
 * — [onBack] — параметр View (не метод ViewModel).
 */
@Composable
fun DetailsScreen(
    itemId: String,
    onBack: () -> Unit
) {
    val vm: DetailViewModel = viewModel(
        key = itemId,
        factory = DetailViewModel.Factory(itemId, appRepository)
    )
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // Кнопка назад — навігаційний колбек залишається у View
        IconButton(onClick = onBack, modifier = Modifier.padding(8.dp)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF6200EA))
        }

        // Обробка всіх варіантів стану через when
        when (val state = uiState) {
            is DetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6200EA))
                }
            }
            is DetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = Color.Red, textAlign = TextAlign.Center)
                }
            }
            is DetailUiState.Success -> {
                val subject = state.subject
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color(0xFF6200EA)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(subject.subject, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Professor: ${subject.professor}", fontSize = 16.sp, color = Color.Gray)
                        Text("Category: ${subject.category}", fontSize = 16.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(24.dp))
                        Box(
                            modifier = Modifier.clip(CircleShape).background(Color(0xFFE8DEF8)).padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${subject.grade}",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6200EA)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(subject.description, fontSize = 16.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Додаткові дані: пов'язані предмети (обчислені у ViewModel)
                    if (state.relatedSubjects.isNotEmpty()) {
                        item {
                            Text(
                                "Related in \"${subject.category}\":",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.fillMaxWidth()
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
