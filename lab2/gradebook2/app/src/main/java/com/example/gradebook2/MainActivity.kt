package com.example.gradebook2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            StudentProfileCard()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Мої оцінки",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
            )

            GradeItem(subject = "Мобільна розробка", grade = 95, label = "Відмінно")
            GradeItem(subject = "Інженерія даних", grade = 92, label = "Відмінно")
            GradeItem(subject = "Архітектура ПЗ", grade = 88, label = "Добре")
            GradeItem(subject = "Алгоритми та структури даних", grade = 90, label = "Відмінно")
            GradeItem(subject = "Основи UI/UX", grade = 96, label = "Відмінно")
            GradeItem(subject = "Бази даних", grade = 85, label = "Добре")
            GradeItem(subject = "Комп'ютерні мережі", grade = 82, label = "Добре")
            GradeItem(subject = "Дискретна математика", grade = 91, label = "Відмінно")

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StudentProfileCard() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.my_avatar),
                contentDescription = "Фото студента",
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = "Катерина Островська",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Інженерія програмного забезпечення",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "3 курс",
                fontSize = 14.sp,
                color = Color(0xFF6200EA),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun GradeItem(subject: String, grade: Int, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = subject, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Оцінка",
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = grade.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6200EA)
            )
        }
    }
}