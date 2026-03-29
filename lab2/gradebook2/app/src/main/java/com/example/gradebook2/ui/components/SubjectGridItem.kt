package com.example.gradebook2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gradebook2.data.model.GradeRecord

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
