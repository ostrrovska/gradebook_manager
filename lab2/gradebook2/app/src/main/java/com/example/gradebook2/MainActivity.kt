package com.example.gradebook2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

data class GradeRecord(
    val id: String = UUID.randomUUID().toString(),
    val subject: String,
    val grade: Int,
    val label: String,
    val category: String,
    val professor: String = ""
)

data class CalendarEvent(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val deadlineMs: Long
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DigitalGradebookTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun DigitalGradebookTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(background = Color(0xFFF3F4F6)),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    var isLoading by remember { mutableStateOf(true) }

    val subjectsList = remember { mutableStateListOf<GradeRecord>() }
    val eventsList = remember { mutableStateListOf<CalendarEvent>() }
    var selectedTabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {//managing side effects
        delay(1500)

        subjectsList.addAll(
            listOf(
                GradeRecord(subject = "Mobile Development", grade = 95, label = "Excellent", category = "Core", professor = "Dr. Smith"),
                GradeRecord(subject = "Data Engineering", grade = 92, label = "Excellent", category = "Core", professor = "Prof. Johnson"),
                GradeRecord(subject = "UI/UX Basics", grade = 96, label = "Excellent", category = "Elective", professor = "Ms. Wilson")
            )
        )

        val now = System.currentTimeMillis()
        eventsList.addAll(
            listOf(
                CalendarEvent(title = "Submit Lab 4", category = "Core", deadlineMs = now + 86400000),
                CalendarEvent(title = "Read chapter 5", category = "Elective", deadlineMs = now + 172800000)
            )
        )
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
            .padding(top = 16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF6200EA)
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    StudentProfileCard()
                }

                Spacer(modifier = Modifier.height(16.dp))

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF6200EA)
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Subjects", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("My Calendar", fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    when (selectedTabIndex) {
                        0 -> SubjectsContent(subjectsList)
                        1 -> CalendarContent(eventsList)
                    }
                }
            }
        }
    }
}

@Composable
//parent
fun SubjectsContent(subjectsList: List<GradeRecord>) {
    var selectedFilter by remember { mutableStateOf("All") }

    val displayedSubjects by remember(selectedFilter, subjectsList) {
        derivedStateOf {
            if (selectedFilter == "All") subjectsList
            else subjectsList.filter { it.category == selectedFilter }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Filter by Category:", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))

        FilterPanel(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it },
            options = listOf("All", "Core", "Elective", "Project")
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(displayedSubjects, key = { it.id }) { subject ->
                SubjectItem(subject)
            }
        }
    }
}

@Composable
//parent
fun CalendarContent(eventsList: MutableList<CalendarEvent>) {
    var inputText by remember { mutableStateOf("") }
    var selectedCategoryForNewEvent by remember { mutableStateOf("Core") }
    var selectedDeadline by remember { mutableStateOf(System.currentTimeMillis()) }

    var sortOption by remember { mutableStateOf("Deadline") }

    val sortedEvents by remember(sortOption, eventsList) {
        derivedStateOf {
            when (sortOption) {
                "Deadline" -> eventsList.sortedBy { it.deadlineMs }
                "Category" -> eventsList.sortedBy { it.category }
                else -> eventsList
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total tasks: ${eventsList.size}",
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            if (eventsList.size > 5) {
                Badge(
                    containerColor = Color(0xFFE53935),
                    contentColor = Color.White
                ) {
                    Text("Overloaded!", modifier = Modifier.padding(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // State Hoisting
        EventInputPanel(
            inputText = inputText,
            onTextChange = { inputText = it },
            selectedCategory = selectedCategoryForNewEvent,
            onCategoryChange = { selectedCategoryForNewEvent = it },
            selectedDeadline = selectedDeadline,
            onDeadlineChange = { selectedDeadline = it },
            onAddClick = {
                if (inputText.isNotBlank()) {
                    eventsList.add(
                        CalendarEvent(
                            title = inputText.trim(),
                            category = selectedCategoryForNewEvent,
                            deadlineMs = selectedDeadline
                        )
                    )
                    inputText = ""
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SortDropdownMenu(
            currentSort = sortOption,
            onSortChange = { sortOption = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (sortedEvents.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "The list is empty. Add your first event.",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(sortedEvents, key = { it.id }) { event ->
                    CalendarEventItem(
                        event = event,
                        onDelete = { eventsList.remove(event) }
                    )
                }
            }
        }
    }
}
@Composable
//parent - CalendarContent
fun SortDropdownMenu(currentSort: String, onSortChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Deadline", "Category")

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        OutlinedCard(
            shape = CircleShape,
            modifier = Modifier.clickable { expanded = true },
            colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Sort by: $currentSort", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSortChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// State Hoisted Input Component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
//parent - CalendarContent
fun EventInputPanel(
    inputText: String,
    onTextChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    selectedDeadline: Long,
    onDeadlineChange: (Long) -> Unit,
    onAddClick: () -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onTextChange,
                    placeholder = { Text("New Event Title") },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onAddClick,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA)),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Add", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Deadline: ${dateFormatter.format(Date(selectedDeadline))}",
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )

                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = selectedDeadline

                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                calendar.set(year, month, dayOfMonth)
                                TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                        calendar.set(Calendar.MINUTE, minute)
                                        onDeadlineChange(calendar.timeInMillis)
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true
                                ).show()
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text("Set Time", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val categories = listOf("Core", "Elective", "Project")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategoryChange(category) },
                        label = { Text(category, fontSize = 12.sp) },
                        shape = CircleShape
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//parent - SubjectsContent
fun FilterPanel(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    options: List<String>
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFE8DEF8),
                    selectedLabelColor = Color(0xFF6200EA)
                ),
                shape = CircleShape
            )
        }
    }
}

@Composable
fun SubjectItem(gradeRecord: GradeRecord) {
    //parent - SubjectsContent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = gradeRecord.subject, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${gradeRecord.professor} • ${gradeRecord.category}",
                fontSize = 12.sp,
                color = Color.DarkGray
            )
        }
        Text(
            text = gradeRecord.grade.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6200EA)
        )
    }
}

@Composable
//parent - CalendarContent
fun CalendarEventItem(
    event: CalendarEvent,
    onDelete: () -> Unit // Callback
) {
    val dateFormatter = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.US) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            tint = Color(0xFF6200EA),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = event.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Label: ${event.category}",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = dateFormatter.format(Date(event.deadlineMs)),
                    fontSize = 12.sp,
                    color = Color(0xFFE53935),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Task",
                tint = Color(0xFFE53935)
            )
        }
    }
}

@Composable
fun StudentProfileCard() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x336200EA))
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.my_avatar),
                contentDescription = "Student photo",
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFF6200EA), CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = "Kateryna Ostrovska", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Software Engineering", fontSize = 14.sp, color = Color.Gray)
            Text(
                text = "GPA: 91.5 | 3rd Year",
                fontSize = 13.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(Color(0xFF6200EA), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}