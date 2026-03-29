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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

// =============================================================================
// --- ЗАВДАННЯ 1: DATA LAYER / MODEL ---
// Усі дата-класи та репозиторій винесені в окремий шар.
// AppRepository не імпортує жодного класу з androidx.compose.*
// =============================================================================

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

/**
 * ЗАВДАННЯ 1 — Репозиторій (Model layer).
 * Клас інкапсулює доступ до всіх даних застосунку.
 * Не імпортує жодного класу з androidx.compose.*.
 * Надає методи: getAll(), getById(), getCategories().
 * Тестові дані зберігаються виключно тут.
 */
class AppRepository {
    private val subjects = listOf(
        GradeRecord(subject = "Mobile Development", grade = 95, label = "Excellent", category = "Core", professor = "Dr. Smith"),
        GradeRecord(subject = "Data Engineering", grade = 92, label = "Excellent", category = "Core", professor = "Prof. Johnson"),
        GradeRecord(subject = "UI/UX Basics", grade = 96, label = "Excellent", category = "Elective", professor = "Ms. Wilson"),
        GradeRecord(subject = "Algorithms", grade = 88, label = "Good", category = "Core", professor = "Dr. Alan"),
        GradeRecord(subject = "Machine Learning", grade = 98, label = "Excellent", category = "Project", professor = "Dr. Turing")
    )

    /** Повертає повний список предметів */
    fun getAllSubjects(): List<GradeRecord> = subjects

    /** Повертає предмет за ідентифікатором або null */
    fun getSubjectById(id: String): GradeRecord? = subjects.find { it.id == id }

    /** Повертає унікальні категорії для фільтрації */
    fun getCategories(): List<String> = listOf("All") + subjects.map { it.category }.distinct()
}

// Singleton-екземпляр репозиторію (у реальному проєкті — DI)
val appRepository = AppRepository()

// =============================================================================
// --- ЗАВДАННЯ 2: ViewModel ДЛЯ ОСНОВНОГО СПИСКУ (LIST TAB) ---
// Зберігає реактивний стан, імітує асинхронне завантаження,
// надає фільтрований список. Не імпортує UI-фреймворк.
// =============================================================================

/**
 * ЗАВДАННЯ 2 — ViewModel для екрану основного списку.
 * Успадковує androidx.lifecycle.ViewModel.
 * Стан зберігається у MutableStateFlow / StateFlow.
 * Імітує асинхронне завантаження з затримкою 0.7 секунди.
 * Надає відфільтрований список через selectedFilter + filteredSubjects.
 * Не імпортує жодного UI-фреймворку.
 */
class ListViewModel(private val repository: AppRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _allSubjects = MutableStateFlow<List<GradeRecord>>(emptyList())

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _filteredSubjects = MutableStateFlow<List<GradeRecord>>(emptyList())
    val filteredSubjects: StateFlow<List<GradeRecord>> = _filteredSubjects.asStateFlow()

    val categories: List<String> = repository.getCategories()

    init {
        loadSubjects()
    }

    /** Імітує асинхронне завантаження з репозиторію */
    private fun loadSubjects() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(700) // імітація затримки мережі
            _allSubjects.value = repository.getAllSubjects()
            applyFilter()
            _isLoading.value = false
        }
    }

    /** Оновлює обраний фільтр та перераховує відфільтрований список */
    fun selectFilter(filter: String) {
        _selectedFilter.value = filter
        applyFilter()
    }

    private fun applyFilter() {
        val filter = _selectedFilter.value
        _filteredSubjects.value = if (filter == "All") {
            _allSubjects.value
        } else {
            _allSubjects.value.filter { it.category == filter }
        }
    }

    /** Factory для створення ViewModel з параметром repository */
    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ListViewModel(repository) as T
        }
    }
}

// =============================================================================
// --- ЗАВДАННЯ 3: ViewModel ДЛЯ ЕКРАНУ ДЕТАЛЕЙ ---
// Обробляє стани: Loading, Success, Error через sealed interface.
// Отримує дані через репозиторій за itemId з конструктора.
// =============================================================================

/**
 * ЗАВДАННЯ 3 — Sealed interface для стану екрану деталей.
 * Моделює три стани: завантаження, успіх із даними, помилка.
 */
sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val subject: GradeRecord, val relatedSubjects: List<GradeRecord>) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

/**
 * ЗАВДАННЯ 3 — ViewModel для екрану деталей.
 * Приймає itemId через конструктор (потребує ViewModelProvider.Factory).
 * Отримує дані через репозиторій, обробляє всі стани.
 * Обчислює додаткові дані: пов'язані предмети тієї ж категорії.
 * Не імпортує жодного UI-фреймворку.
 */
class DetailViewModel(
    private val itemId: String,
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            delay(300) // коротка затримка для UX
            val subject = repository.getSubjectById(itemId)
            _uiState.value = if (subject != null) {
                // Обчислення пов'язаних предметів тієї ж категорії (без поточного)
                val related = repository.getAllSubjects()
                    .filter { it.category == subject.category && it.id != subject.id }
                DetailUiState.Success(subject = subject, relatedSubjects = related)
            } else {
                DetailUiState.Error("Subject with id=$itemId not found.")
            }
        }
    }

    /** Factory — обов'язкова для передачі itemId у конструктор ViewModel */
    class Factory(
        private val itemId: String,
        private val repository: AppRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel(itemId, repository) as T
        }
    }
}

// =============================================================================
// --- ЗАВДАННЯ 4: ViewModel ДЛЯ GRID TAB ---
// Стан sortOption перенесений із View у ViewModel.
// =============================================================================

/**
 * ЗАВДАННЯ 4 — ViewModel для Grid-вкладки.
 * Стан sortOption перенесений з remember у ViewModel.
 * Надає відсортований список через StateFlow.
 * Не імпортує жодного UI-фреймворку.
 */
class GridViewModel(private val repository: AppRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sortOption = MutableStateFlow("By Name")
    val sortOption: StateFlow<String> = _sortOption.asStateFlow()

    private val _sortedSubjects = MutableStateFlow<List<GradeRecord>>(emptyList())
    val sortedSubjects: StateFlow<List<GradeRecord>> = _sortedSubjects.asStateFlow()

    val sortOptions = listOf("By Name", "By Grade")

    init {
        loadAndSort()
    }

    private fun loadAndSort() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(700)
            applySort()
            _isLoading.value = false
        }
    }

    /** Оновлює параметр сортування і перераховує список */
    fun selectSortOption(option: String) {
        _sortOption.value = option
        applySort()
    }

    private fun applySort() {
        val all = repository.getAllSubjects()
        _sortedSubjects.value = when (_sortOption.value) {
            "By Name" -> all.sortedBy { it.subject }
            "By Grade" -> all.sortedByDescending { it.grade }
            else -> all
        }
    }

    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GridViewModel(repository) as T
        }
    }
}

// =============================================================================
// --- ЗАВДАННЯ 4: ViewModel ДЛЯ PROFILE TAB ---
// Бізнес-стан (список задач, userName) перенесений з View у ViewModel.
// =============================================================================

/**
 * ЗАВДАННЯ 4 — ViewModel для Profile-вкладки.
 * Зберігає userName, список задач (CalendarEvent), стан введення.
 * Весь бізнес-стан (eventsList, selectedCategory, selectedDeadline)
 * перенесений з remember / @State у ViewModel.
 * Не імпортує жодного UI-фреймворку.
 */
class ProfileViewModel(initialUserName: String) : ViewModel() {

    private val _userName = MutableStateFlow(initialUserName)
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _events = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val events: StateFlow<List<CalendarEvent>> = _events.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Core")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedDeadline = MutableStateFlow(System.currentTimeMillis())
    val selectedDeadline: StateFlow<Long> = _selectedDeadline.asStateFlow()

    fun onUserNameChange(name: String) { _userName.value = name }
    fun onInputTextChange(text: String) { _inputText.value = text }
    fun onCategoryChange(category: String) { _selectedCategory.value = category }
    fun onDeadlineChange(deadline: Long) { _selectedDeadline.value = deadline }

    /** Додає нову задачу до списку якщо заголовок не порожній */
    fun addEvent() {
        val title = _inputText.value.trim()
        if (title.isBlank()) return
        _events.update { current ->
            current + CalendarEvent(
                title = title,
                category = _selectedCategory.value,
                deadlineMs = _selectedDeadline.value
            )
        }
        _inputText.value = ""
    }

    /** Видаляє задачу зі списку */
    fun deleteEvent(event: CalendarEvent) {
        _events.update { it - event }
    }

    class Factory(private val initialUserName: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(initialUserName) as T
        }
    }
}

// =============================================================================
// --- ENTRY POINT ---
// =============================================================================

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

// =============================================================================
// --- ЗАВДАННЯ 5: НАВІГАЦІЯ (збережена без змін) ---
// RootNavigation та MainScreen залишаються відповідальними за навігацію.
// ViewModel не імпортує NavController та не містить навігаційних компонентів.
// Колбеки onItemClick, onBack залишаються параметрами View-функцій.
// =============================================================================

/**
 * ЗАВДАННЯ 5 — Кореневий навігаційний граф.
 * Маршрути, аргументи та структура навігації не змінені відносно ЛР №5.
 */
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

// --- ONBOARDING SCREEN (без змін, навігація на рівні View) ---
@Composable
fun OnboardingScreen(navController: NavHostController) {
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val userName by savedStateHandle?.getStateFlow<String>("userName", "")
        ?.collectAsState(initial = "") ?: remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
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

// --- NAME ENTRY SCREEN (без змін) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameEntryScreen(navController: NavHostController) {
    // UI-стан (фокус / локальний текст поля) — допустиме використання rememberSaveable у View
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

// --- TAB NAVIGATION ITEMS (без змін) ---
sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object List : BottomNavItem("tab_list", "List", Icons.Default.List)
    object Grid : BottomNavItem("tab_grid", "Grid", Icons.Default.Menu)
    object Profile : BottomNavItem("tab_profile", "Profile", Icons.Default.Person)
}

/**
 * ЗАВДАННЯ 5 — MainScreen зберігає Tab-навігацію та передачу аргументів.
 * Навігаційний граф не змінено. ViewModel не містить NavController.
 */
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
                        selected = currentRoute == item.route ||
                                currentRoute?.startsWith("details") == true && item == BottomNavItem.List,
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
                // Колбек навігації onItemClick залишається параметром View — не методом ViewModel
                ListTabContent(onItemClick = { id -> bottomNavController.navigate("details/$id") })
            }
            composable(BottomNavItem.Grid.route) {
                GridTabContent(onItemClick = { id -> bottomNavController.navigate("details/$id") })
            }
            composable(BottomNavItem.Profile.route) {
                ProfileTabContent(initialUserName = initialUserName)
            }
            composable(
                route = "details/{itemId}",
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
                // onBack — колбек навігації залишається параметром View
                DetailsScreen(
                    itemId = itemId,
                    onBack = { bottomNavController.popBackStack() }
                )
            }
        }
    }
}

// =============================================================================
// --- VIEW: LIST TAB ---
// Підписується на StateFlow через collectAsStateWithLifecycle().
// Дії користувача передаються через методи ViewModel.
// Колбек onItemClick залишається параметром View.
// =============================================================================

/**
 * ЗАВДАННЯ 2 — View для основного списку.
 * Підписується на стан ListViewModel через collectAsStateWithLifecycle().
 * Екземпляр ViewModel створюється через viewModel() з кастомною Factory.
 * View не містить бізнес-логіки; лише підписка на стан та виклик методів VM.
 */
@Composable
fun ListTabContent(
    onItemClick: (String) -> Unit, // Колбек навігації залишається у View
    vm: ListViewModel = viewModel(factory = ListViewModel.Factory(appRepository))
) {
    val isLoading by vm.isLoading.collectAsStateWithLifecycle()
    val filteredSubjects by vm.filteredSubjects.collectAsStateWithLifecycle()
    val selectedFilter by vm.selectedFilter.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Filter by Category:", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.categories) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { vm.selectFilter(filter) }, // дія передається у ViewModel
                    label = { Text(filter) },
                    shape = CircleShape
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            // Індикатор завантаження під час імітації затримки
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6200EA))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredSubjects, key = { it.id }) { subject ->
                    SubjectListItem(subject) { onItemClick(subject.id) }
                }
            }
        }
    }
}

// =============================================================================
// --- VIEW: GRID TAB ---
// =============================================================================

/**
 * ЗАВДАННЯ 4 — View для Grid-вкладки.
 * Підписується на GridViewModel; sortOption більше не зберігається у View.
 */
@Composable
fun GridTabContent(
    onItemClick: (String) -> Unit,
    vm: GridViewModel = viewModel(factory = GridViewModel.Factory(appRepository))
) {
    val isLoading by vm.isLoading.collectAsStateWithLifecycle()
    val sortedSubjects by vm.sortedSubjects.collectAsStateWithLifecycle()
    val sortOption by vm.sortOption.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Sort by:", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.sortOptions) { option ->
                FilterChip(
                    selected = sortOption == option,
                    onClick = { vm.selectSortOption(option) },
                    label = { Text(option) },
                    shape = CircleShape
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6200EA))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sortedSubjects, key = { it.id }) { subject ->
                    SubjectGridItem(subject) { onItemClick(subject.id) }
                }
            }
        }
    }
}

// =============================================================================
// --- VIEW: DETAILS SCREEN ---
// Обробляє всі стани DetailUiState через when.
// =============================================================================

/**
 * ЗАВДАННЯ 3 — View екрану деталей.
 * ViewModel створюється з кастомною Factory (передається itemId).
 * View використовує when для обробки Loading / Success / Error.
 * Колбек onBack залишається параметром View — не методом ViewModel.
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

// =============================================================================
// --- VIEW: PROFILE TAB ---
// =============================================================================

/**
 * ЗАВДАННЯ 4 — View для Profile-вкладки.
 * Весь бізнес-стан перенесений у ProfileViewModel.
 * View не містить remember для бізнес-даних.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTabContent(
    initialUserName: String,
    vm: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory(initialUserName))
) {
    val userName by vm.userName.collectAsStateWithLifecycle()
    val events by vm.events.collectAsStateWithLifecycle()
    val inputText by vm.inputText.collectAsStateWithLifecycle()
    val selectedCategory by vm.selectedCategory.collectAsStateWithLifecycle()
    val selectedDeadline by vm.selectedDeadline.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text("User Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = userName,
                onValueChange = { vm.onUserNameChange(it) },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

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

        item {
            Text("My Tasks", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            EventInputPanel(
                inputText = inputText,
                onTextChange = { vm.onInputTextChange(it) },
                selectedCategory = selectedCategory,
                onCategoryChange = { vm.onCategoryChange(it) },
                selectedDeadline = selectedDeadline,
                onDeadlineChange = { vm.onDeadlineChange(it) },
                onAddClick = { vm.addEvent() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (events.isEmpty()) {
            item {
                Text(
                    text = "No tasks yet. Add your first event above.",
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(events, key = { it.id }) { event ->
                CalendarEventItem(
                    event = event,
                    onDelete = { vm.deleteEvent(event) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// =============================================================================
// --- ДОПОМІЖНІ UI-КОМПОНЕНТИ (без змін у функціоналі) ---
// =============================================================================

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
            Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = Color(0xFFE53935))
        }
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
            .background(Color.White, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = gradeRecord.subject, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "${gradeRecord.professor} • ${gradeRecord.category}", fontSize = 12.sp, color = Color.DarkGray)
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
fun SubjectGridItem(gradeRecord: GradeRecord, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = gradeRecord.grade.toString(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6200EA)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = gradeRecord.subject,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = gradeRecord.category, fontSize = 12.sp, color = Color.Gray)
    }
}