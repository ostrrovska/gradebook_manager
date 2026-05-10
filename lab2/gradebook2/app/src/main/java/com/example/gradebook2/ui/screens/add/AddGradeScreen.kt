package com.example.gradebook2.ui.screens.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gradebook2.GradeApplication
import com.example.gradebook2.ui.theme.AppTheme
import com.example.gradebook2.ui.theme.PreviewBothThemes
import androidx.compose.material3.Scaffold

// Lab 9, Task 4 — separate route in NavHost; POSTs form data to API then pops back
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGradeScreen(
    onBack: () -> Unit
) {
    val app = LocalContext.current.applicationContext as GradeApplication
    val vm: AddGradeViewModel = viewModel(factory = AddGradeViewModel.Factory(app.repository))

    val subject     by vm.subject.collectAsStateWithLifecycle()
    val grade       by vm.grade.collectAsStateWithLifecycle()
    val label       by vm.label.collectAsStateWithLifecycle()
    val category    by vm.category.collectAsStateWithLifecycle()
    val professor   by vm.professor.collectAsStateWithLifecycle()
    val description by vm.description.collectAsStateWithLifecycle()
    val isSaving    by vm.isSaving.collectAsStateWithLifecycle()
    val saved       by vm.savedSuccessfully.collectAsStateWithLifecycle()
    val error       by vm.errorMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Lab 9, Task 4 — navigate back automatically after successful POST
    LaunchedEffect(saved) { if (saved) onBack() }

    // Lab 9, Task 5 — show Snackbar for POST failure
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearError()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary)
                }
                Text("Add Grade", style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = subject, onValueChange = vm::onSubjectChange,
                label = { Text("Subject *") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = grade, onValueChange = vm::onGradeChange,
                label = { Text("Grade (0–100) *") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = professor, onValueChange = vm::onProfessorChange,
                label = { Text("Professor *") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            // Lab 9, Task 2 — label selector
            Text("Label:", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vm.labels) { l ->
                    FilterChip(selected = label == l, onClick = { vm.onLabelChange(l) },
                        label = { Text(l, style = MaterialTheme.typography.labelMedium) },
                        shape = CircleShape)
                }
            }
            Spacer(Modifier.height(8.dp))

            // Lab 9, Task 2 — category selector
            Text("Category:", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vm.categories) { c ->
                    FilterChip(selected = category == c, onClick = { vm.onCategoryChange(c) },
                        label = { Text(c, style = MaterialTheme.typography.labelMedium) },
                        shape = CircleShape)
                }
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = description, onValueChange = vm::onDescriptionChange,
                label = { Text("Description") }, modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(Modifier.height(16.dp))

            // Lab 9, Task 4 — Save button disabled while POST is in-flight
            Button(
                onClick  = vm::save,
                enabled  = !isSaving,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.padding(4.dp)
                    )
                } else {
                    Text("Save", style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// Lab 9, Task — @Preview for the add form
@PreviewBothThemes
@Composable
private fun AddGradeScreenPreview() {
    AppTheme { /* Cannot instantiate VM in preview — shown as empty form placeholder */ }
}
