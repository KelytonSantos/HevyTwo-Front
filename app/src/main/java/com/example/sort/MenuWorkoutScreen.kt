package com.example.sort

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    onStats: () -> Unit = {}
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF8A00FF),
                        Color(0xFF090979),
                        Color(0xFF020024)
                        ))
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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