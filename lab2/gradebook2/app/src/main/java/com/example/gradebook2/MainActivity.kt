package com.example.gradebook2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign

data class GradeRecord(
    val id: String,
    val subject: String,
    val grade: Int,
    val label: String,
    val category: String,
    val professor: String = ""
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DigitalGradebookTheme {
                GradebookScreen()
            }
        }
    }
}

@Composable
fun DigitalGradebookTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            background = Color(0xFFF3F4F6)
        ),
        content = content
    )
}

@Composable
fun GradebookScreen() {

    val rawGradesList = remember {
        listOf(
            GradeRecord("1", "Mobile Development", 95, "Excellent", "Core"),
            GradeRecord("2", "Data Engineering", 92, "Excellent", "Core"),
            GradeRecord("3", "Software Architecture", 88, "Good", "Core"),
            GradeRecord("4", "Algorithms", 90, "Excellent", "Core"),
            GradeRecord("5", "UI/UX Basics", 96, "Excellent", "Elective"),
            GradeRecord("6", "3D Modeling", 80, "Good", "Elective"),
            GradeRecord("7", "Databases", 85, "Good", "Core"),
            GradeRecord("8", "Startup Project", 100, "Excellent", "Project")
        )
    }

    val professorMap = remember {
        mapOf(
            "Mobile Development" to "Dr. Smith",
            "Data Engineering" to "Prof. Johnson",
            "Software Architecture" to "On Leave",
            "Algorithms" to "Prof. Davis",
            "UI/UX Basics" to "Ms. Wilson",
            "3D Modeling" to "Mr. White",
            "Databases" to "Dr. Miller",
            "Startup Project" to "Mentorship Team"
        )
    }
    val activeCategoriesSet = remember {
        setOf("Core", "Elective", "Project")
    }


    // HOF for Map
    val activeProfessors = remember(professorMap) {
        professorMap.filterValues { it != "On Leave" }
    }

    // HOF for List
    val topGrades = remember(rawGradesList) {
        rawGradesList
            .filter { it.grade >= 90 }
            .sortedByDescending { it.grade }
    }

    // HOF for List
    val groupedGradesMap = remember(rawGradesList, activeCategoriesSet, activeProfessors) {
        rawGradesList
            .filter { activeCategoriesSet.contains(it.category) }
            .map { grade ->
                grade.copy(professor = activeProfessors[grade.subject] ?: "TBA")
            }
            .groupBy { it.category }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
    ) {
        // TASK 3: (LazyColumn)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F4F6)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { StudentProfileCard() }

            // TASK 4: LazyHorizontalGrid
            item {
                SectionHeader("Top Grades 🏆")

                LazyHorizontalGrid(
                    rows = GridCells.Fixed(1),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(topGrades) { gradeRecord ->
                        GridGradeItem(gradeRecord)
                    }
                }
            }

            item { SectionHeader("All Subjects 📚") }

            groupedGradesMap.forEach { (category, grades) ->
                item { CategoryHeader(category) }

                items(grades) { gradeRecord ->
                    ListGradeItem(gradeRecord)
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}


@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF1F2937),
        modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun CategoryHeader(categoryName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF6200EA),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = categoryName.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 1.sp
        )
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

// TASK 2: Component 1 (Елемент списку - Горизонтальна композиція)
@Composable
fun ListGradeItem(gradeRecord: GradeRecord) {
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
                text = "${gradeRecord.professor} • ${gradeRecord.label}",
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

// TASK 2: Component 2 (Елемент сітки - Вертикальна композиція)
@Composable
fun GridGradeItem(gradeRecord: GradeRecord) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(110.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF3E8FF))
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Top Grade",
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = gradeRecord.grade.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF6200EA)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = gradeRecord.subject,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF4B5563),
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}