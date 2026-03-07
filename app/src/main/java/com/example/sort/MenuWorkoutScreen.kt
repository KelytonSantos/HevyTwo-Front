package com.example.sort

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sort.ui.theme.components.ExploreCard
import com.example.sort.ui.theme.components.MenuCard
import com.example.sort.ui.theme.components.RecentActivityCard
import com.example.sort.ui.theme.components.WorkoutMenuItem

@Composable
fun MenuWorkoutScreen(
    onExploreClick: () -> Unit = {},
    onDailyWorkout: () -> Unit = {},
    onMyWorkouts: () -> Unit = {},
    onCreateWorkout: () -> Unit = {},
    onStats: () -> Unit = {},
    onBack: () -> Unit = {}
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF020617),
                        Color(0xFF0C1445),
                        Color(0xFF2E1065)
                        ))
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.West,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Text(
                    text = "Menu Fitness",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 40.dp),
                    textAlign = TextAlign.Center
                    
                )
            }
        }

        // CARD GRANDE
        item(span = { GridItemSpan(2) }) {

            ExploreCard(onExploreClick)
        }

        // GRID DE CARDS
        val items = listOf(
            WorkoutMenuItem("Iniciar Rotina do Dia", Icons.Default.Bolt, onDailyWorkout),
            WorkoutMenuItem("Minhas Rotinas", Icons.Default.Folder, onMyWorkouts),
            WorkoutMenuItem("Criar Nova Rotina", Icons.Default.AddCircle, onCreateWorkout),
            WorkoutMenuItem("Estatísticas", Icons.Default.BarChart, onStats)
        )

        items(items) { item ->
            MenuCard(item)
        }

        // ATIVIDADE RECENTE
        item(span = { GridItemSpan(2) }) {

            RecentActivityCard()
        }
    }
}