package com.example.gradebook2.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

/** ЛР №5/№6 — маршрути нижньої панелі Tab (див. [MainScreen], Завдання 5). */
sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object List : BottomNavItem("tab_list", "List", Icons.Default.List)
    object Grid : BottomNavItem("tab_grid", "Grid", Icons.Default.Menu)
    object Profile : BottomNavItem("tab_profile", "Profile", Icons.Default.Person)
}
