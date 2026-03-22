package com.example.gradebook2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

// --- DATA LAYER ---
data class GradeRecord(
    val id: String = UUID.randomUUID().toString(),
    val subject: String,
    val grade: Int,
    val label: String,
    val category: String,
    val professor: String,
    val description: String = "Detailed description for this subject..."
)

data class CalendarEvent(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val deadlineMs: Long
)

val mockSubjects = listOf(
    GradeRecord(subject = "Mobile Development", grade = 95, label = "Excellent", category = "Core", professor = "Dr. Smith"),
    GradeRecord(subject = "Data Engineering", grade = 92, label = "Excellent", category = "Core", professor = "Prof. Johnson"),
    GradeRecord(subject = "UI/UX Basics", grade = 96, label = "Excellent", category = "Elective", professor = "Ms. Wilson"),
    GradeRecord(subject = "Algorithms", grade = 88, label = "Good", category = "Core", professor = "Dr. Alan"),
    GradeRecord(subject = "Machine Learning", grade = 98, label = "Excellent", category = "Project", professor = "Dr. Turing")
)

// --- ENTRY POINT ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DigitalGradebookTheme {
                RootNavigation()
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

// --- ROOT NAVIGATION ---
@Composable
fun RootNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            OnboardingScreen(navController)
        }
        composable("name_entry") {
            NameEntryScreen(navController)
        }
        composable(
            route = "main/{userName}",
            arguments = listOf(navArgument("userName") { type = NavType.StringType })
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Student"
            MainScreen(userName)
        }
    }
}

// --- TASK 1: ONBOARDING SCREEN ---
@Composable
fun OnboardingScreen(navController: NavHostController) {
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val userName by savedStateHandle?.getStateFlow<String>("userName", "")?.collectAsState(initial = "") ?: remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp),
            tint = Color(0xFF6200EA)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("Digital Gradebook", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EA))
        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { navController.navigate("name_entry") },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Enter your name")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("main/$userName") {
                    popUpTo("onboarding") { inclusive = true }
                }
            },
            enabled = userName.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5))
        ) {
            Text(if (userName.isEmpty()) "Get Started" else "Hello, $userName! Start")
        }
    }
}

// --- TASK 2: NAME ENTRY SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameEntryScreen(navController: NavHostController) {
    var text by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("How should we call you?", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("userName", text.trim())
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = text.isNotBlank()
        ) {
            Text("Save")
        }
    }
}

// --- TASK 3: TAB NAVIGATION ---
sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object List : BottomNavItem("tab_list", "List", Icons.Default.List)
    object Grid : BottomNavItem("tab_grid", "Grid", Icons.Default.Menu)
    object Profile : BottomNavItem("tab_profile", "Profile", Icons.Default.Person)
}

@Composable
fun MainScreen(initialUserName: String) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val items = listOf(BottomNavItem.List, BottomNavItem.Grid, BottomNavItem.Profile)
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route || currentRoute?.startsWith("details") == true && item == BottomNavItem.List,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF6200EA),
                            selectedTextColor = Color(0xFF6200EA),
                            indicatorColor = Color(0xFFE8DEF8)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.List.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.List.route) {
                ListTabContent(navController = bottomNavController)
            }
            composable(BottomNavItem.Grid.route) {
                GridTabContent(navController = bottomNavController)
            }
            composable(BottomNavItem.Profile.route) {
                ProfileTabContent(initialUserName)
            }
            composable(
                route = "details/{itemId}",
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId")
                val item = mockSubjects.find { it.id == itemId }
                if (item != null) {
                    DetailsScreen(item)
                }
            }
        }
    }
}

// --- TASK 4: LIST TAB ---
@Composable
fun ListTabContent(navController: NavHostController) {
    var selectedFilter by rememberSaveable { mutableStateOf("All") }

    val displayedSubjects by remember(selectedFilter) {
        derivedStateOf {
            if (selectedFilter == "All") mockSubjects
            else mockSubjects.filter { it.category == selectedFilter }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Filter by Category:", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("All", "Core", "Elective", "Project")) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                    shape = CircleShape
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(displayedSubjects, key = { it.id }) { subject ->
                SubjectListItem(subject) {
                    navController.navigate("details/${subject.id}")
                }
            }
        }
    }
}

// --- TASK 4: GRID TAB ---
@Composable
fun GridTabContent(navController: NavHostController) {
    var sortOption by rememberSaveable { mutableStateOf("By Name") }

    val sortedSubjects by remember(sortOption) {
        derivedStateOf {
            when (sortOption) {
                "By Name" -> mockSubjects.sortedBy { it.subject }
                "By Grade" -> mockSubjects.sortedByDescending { it.grade }
                else -> mockSubjects
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Sort by:", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("By Name", "By Grade")) { option ->
                FilterChip(
                    selected = sortOption == option,
                    onClick = { sortOption = option },
                    label = { Text(option) },
                    shape = CircleShape
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sortedSubjects, key = { it.id }) { subject ->
                SubjectGridItem(subject) {
                    navController.navigate("details/${subject.id}")
                }
            }
        }
    }
}

// --- DETAILS SCREEN ---
@Composable
fun DetailsScreen(subject: GradeRecord) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color(0xFF6200EA))
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
            Text("${subject.grade}", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EA))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(subject.description, fontSize = 16.sp, textAlign = TextAlign.Center)
    }
}

// --- TASK 5: PROFILE TAB (WITH TASKS) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTabContent(initialUserName: String) {
    var userName by rememberSaveable { mutableStateOf(initialUserName) }

    val eventsList = remember { mutableStateListOf<CalendarEvent>() }
    var inputText by rememberSaveable { mutableStateOf("") }
    var selectedCategoryForNewEvent by rememberSaveable { mutableStateOf("Core") }
    var selectedDeadline by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // --- User Profile Section ---
        item {
            Text("User Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // --- App Info Section ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("App Information", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Name: Digital Gradebook", color = Color.DarkGray)
                    Text("Version: 1.0.0", color = Color.DarkGray)
                    Text("Developer: Kateryna", color = Color.DarkGray)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // --- Task Management Section ---
        item {
            Text("My Tasks", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

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
        }

        // --- Task List ---
        if (eventsList.isEmpty()) {
            item {
                Text(
                    text = "No tasks yet. Add your first event above.",
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(eventsList, key = { it.id }) { event ->
                CalendarEventItem(
                    event = event,
                    onDelete = { eventsList.remove(event) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// --- CALENDAR UI COMPONENTS ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
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
                    modifier = Modifier.weight(1f).height(56.dp),
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

@Composable
fun CalendarEventItem(event: CalendarEvent, onDelete: () -> Unit) {
    val dateFormatter = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.US) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF6200EA), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = event.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Label: ${event.category}", fontSize = 12.sp, color = Color.DarkGray)
                Text(text = dateFormatter.format(Date(event.deadlineMs)), fontSize = 12.sp, color = Color(0xFFE53935), fontWeight = FontWeight.Medium)
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = Color(0xFFE53935))
        }
    }
}

// --- HELPER UI COMPONENTS ---
@Composable
fun SubjectListItem(gradeRecord: GradeRecord, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = gradeRecord.subject, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "${gradeRecord.professor} • ${gradeRecord.category}", fontSize = 12.sp, color = Color.DarkGray)
        }
        Text(text = gradeRecord.grade.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EA))
    }
}

@Composable
fun SubjectGridItem(gradeRecord: GradeRecord, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = gradeRecord.grade.toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EA))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = gradeRecord.subject, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, maxLines = 2)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = gradeRecord.category, fontSize = 12.sp, color = Color.Gray)
    }
}