package com.example.sort

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sort.ui.theme.components.FloatingWorkoutButton

@Composable
fun DashboardScreen(onWorkoutClick: () -> Unit = {}) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = remember { com.example.sort.data.SessionManager(context) }

    // Coletando o estado do nome do usuário
    val userName by sessionManager.userName.collectAsState(initial = "Usuário")
    LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF020617), Color(0xFF0C1445), Color(0xFF2E1065))
                    )
                )
                .padding(horizontal = 16.dp), // Padding lateral
            verticalArrangement = Arrangement.spacedBy(16.dp),   // Espaço vertical entre linhas
            horizontalArrangement = Arrangement.spacedBy(16.dp), // Espaço horizontal entre colunas
            // -----------------------------------------
            contentPadding = PaddingValues(bottom = 32.dp) // Espaço no fim da rolagem
        ) {
            // HEADER
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "WELCOME BACK",
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelMedium
                        )
                        // Simplificado para evitar erro de cast/null
                        Text(
                            userName,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .background(Color.White.copy(0.1f), RoundedCornerShape(50.dp))
                            .border(1.dp, Color.White.copy(0.2f), RoundedCornerShape(50.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF7311D4)
                        )
                    }
                }
            }

            // CALENDÁRIO
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(bottom = 24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Activity",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "October 2023",
                            color = Color(0xFF7311D4),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val dias = listOf(
                            "Mon" to "12",
                            "Tue" to "13",
                            "Wed" to "14",
                            "Thu" to "15",
                            "Fri" to "16",
                            "Sat" to "17"
                        )
                        dias.forEach { (dia, num) ->
                            DayItem(dia, num, isSelected = num == "14")
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Atividade",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // CARDS DE ESTATÍSTICAS
            item {
                StatCard("Workouts", "12", "+2 este mês", Icons.Default.FitnessCenter, Color.Green)
            }
            item {
                StatCard("Duração", "45h", "Média 1.2h/dia", Icons.Default.Schedule)
            }
            item {
                StatCard(
                    "Volume",
                    "15k kg",
                    "Top 5% usuários",
                    Icons.Default.Layers,
                    Color(0xFF8A00FF)
                )
            }
            item {
                StatCard("Sets", "120", "Completados", Icons.Default.FormatListNumbered)
            }

            // GRÁFICO
            item(span = { GridItemSpan(2) }) {
                TimeOverMonthCard()
            }

        }
        FloatingWorkoutButton(onClick = onWorkoutClick)
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    subtitleColor: Color = Color.Gray
) {
    Column (
        modifier = Modifier
            .padding(6.dp) // Reduzi um pouco o padding para caber melhor em telas menores
            .fillMaxWidth() // IMPORTANTE: Preencher a coluna do Grid
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row (verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color(0xFF7311D4)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = title, color = Color.LightGray, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge, // Reduzi levemente o tamanho da fonte
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            color = subtitleColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
@Composable
fun TimeOverMonthCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    listOf(Color(0xFF7311D4).copy(0.4f), Color(0xFF7120E3).copy(0.2f))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Text("Time over Month", color = Color.White, fontWeight = FontWeight.Bold)
        Text("Progresso de intensidade", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Área do Gráfico", color = Color.White.copy(0.3f))
            // Aqui futuramente desenhamos o Canvas com a linha rosa
        }
    }
}

@Composable
fun DayItem(day: String, number: String, isSelected: Boolean) {
    Column(
        modifier = Modifier
            .width(52.dp)
            .height(70.dp)
            .background(
                color = if (isSelected) Color(0xFF2F0157) else Color.White.copy(0.05f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else Color.White.copy(0.1f),
                shape = RoundedCornerShape(12.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(day, color = if (isSelected) Color.White else Color.Gray, style = MaterialTheme.typography.labelSmall)
        Text(number, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
    }
}