package com.example.sort.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class WorkoutMenuItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)

@Composable
fun MenuCard(item: WorkoutMenuItem) {

    Column (
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                Color(0xFF7311D4).copy(.1f),
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                Color(0xFF7311D4).copy(.2f),
                RoundedCornerShape(20.dp)
            )
            .clickable() { item.onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF7311D4), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {

            Icon(item.icon, null, tint = Color.White)
        }

        Text(
            item.title,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}