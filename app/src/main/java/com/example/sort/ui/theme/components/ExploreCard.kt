package com.example.sort.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ExploreCard(onClick: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF7311D4), Color(0xFF4A0B8A))
                ),
                RoundedCornerShape(20.dp)
            )
            .clickable() { onClick() }
            .padding(20.dp)
    ) {

        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {

            Icon(Icons.Default.Search, null, tint = Color.White)

            Column {
                Text(
                    "Explorar Exercícios",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Encontre centenas de treinos guiados",
                    color = Color.White.copy(.8f)
                )
            }
        }

        Icon(
            Icons.Default.FitnessCenter,
            null,
            tint = Color.White.copy(.15f),
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
        )
    }
}