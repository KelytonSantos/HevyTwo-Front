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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.example.sort.data.TimeAndDays
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sort.ui.theme.components.FloatingWorkoutButton
import com.example.sort.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(onWorkoutClick: () -> Unit = {}) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = remember { com.example.sort.data.SessionManager(context) }

    // Coletando o estado do nome do usuário
    val userName by sessionManager.userName.collectAsState(initial = "Usuário")

    val dashboardViewModel: DashboardViewModel = viewModel()
    val dash = dashboardViewModel.dashboard

    LaunchedEffect(Unit) {
        dashboardViewModel.load()
    }
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
                val workoutsValue = dash?.workouts?.toString() ?: "--"
                val surplusBalance = dash?.surplusBalance ?: 0
                val isSurplus = dash?.isSurplus ?: true
                val workoutsSubtitle = if (isSurplus) "+$surplusBalance este mês" else "-$surplusBalance este mês"
                val workoutsColor = if (isSurplus) Color.Green else Color.Red
                StatCard("Workouts", workoutsValue, workoutsSubtitle, Icons.Default.FitnessCenter, workoutsColor)
            }
            item {
                val totalHours = if (dash != null) "${dash.totalDuration / 3600}h" else "--"
                val avgHoursStr = if (dash != null) "Média ${"%.1f".format(dash.avgHours)}h/dia" else "Carregando..."
                StatCard("Duração", totalHours, avgHoursStr, Icons.Default.Schedule)
            }
            item {
                val volumeValue = if (dash != null) {
                    if (dash.volume >= 1000) "${dash.volume / 1000}k kg" else "${dash.volume} kg"
                } else "--"
                val volumeSubtitle = if (dash?.topUsers != null) "Top ${dash.topUsers}% usuários" else "Top N/D"
                StatCard(
                    "Volume",
                    volumeValue,
                    volumeSubtitle,
                    Icons.Default.Layers,
                    Color(0xFF8A00FF)
                )
            }
            item {
                val setsValue = dash?.totalSets?.toString() ?: "--"
                StatCard("Sets", setsValue, "Completados", Icons.Default.FormatListNumbered)
            }

            // GRÁFICO
            item(span = { GridItemSpan(2) }) {
                TimeOverMonthCard(graphData = dashboardViewModel.graph)
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
fun TimeOverMonthCard(graphData: List<TimeAndDays> = emptyList()) {
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
        Spacer(modifier = Modifier.height(16.dp))
        if (graphData.isEmpty() || graphData.all { it.hours == 0.0 }) {
            Box(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Sem treinos registrados", color = Color.White.copy(0.3f))
            }
        } else {
            LineChart(
                data = graphData,
                modifier = Modifier.fillMaxWidth().height(180.dp)
            )
        }
    }
}

@Composable
private fun LineChart(
    data: List<TimeAndDays>,
    modifier: Modifier = Modifier
) {
    val goldColor = Color(0xFFD4AF37)
    val maxH = maxOf(data.maxOf { it.hours }, 1.0)
    val minDay = data.minOf { it.days }
    val maxDay = data.maxOf { it.days }
    val dayRange = (maxDay - minDay).toFloat().coerceAtLeast(1f)

    Canvas(modifier = modifier) {
        val yLabelWidth = 34.dp.toPx()
        val xLabelHeight = 16.dp.toPx()
        val chartLeft = yLabelWidth
        val chartTop = 4.dp.toPx()
        val chartBottom = size.height - xLabelHeight
        val chartW = size.width - chartLeft
        val chartH = chartBottom - chartTop

        val labelPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.argb(160, 180, 180, 180)
            textSize = 9.dp.toPx()
            isAntiAlias = true
        }

        // Y-axis labels: max, mid, 0
        val yValues = listOf(maxH, maxH / 2.0, 0.0)
        yValues.forEachIndexed { i, v ->
            val y = chartTop + (i.toFloat() / (yValues.size - 1)) * chartH
            val label = if (v == 0.0) "0h" else "${"%,.2f".format(v)}h"
            labelPaint.textAlign = android.graphics.Paint.Align.RIGHT
            drawContext.canvas.nativeCanvas.drawText(
                label,
                yLabelWidth - 4.dp.toPx(),
                y + labelPaint.textSize / 3f,
                labelPaint
            )
        }

        // X-axis labels: day numbers
        val step = if (data.size <= 8) 1 else 2
        labelPaint.textAlign = android.graphics.Paint.Align.CENTER
        data.forEachIndexed { i, point ->
            if (i % step == 0 || i == data.size - 1) {
                val x = chartLeft + ((point.days - minDay).toFloat() / dayRange) * chartW
                drawContext.canvas.nativeCanvas.drawText(
                    "${point.days}",
                    x,
                    size.height,
                    labelPaint
                )
            }
        }

        // Subtle horizontal grid lines
        listOf(0f, 0.5f, 1f).forEach { frac ->
            val y = chartTop + frac * chartH
            drawLine(
                color = Color.White.copy(alpha = 0.07f),
                start = Offset(chartLeft, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        fun toOffset(d: TimeAndDays) = Offset(
            x = chartLeft + ((d.days - minDay).toFloat() / dayRange) * chartW,
            y = chartTop + chartH - (d.hours.toFloat() / maxH.toFloat()) * chartH
        )

        val points = data.map(::toOffset)

        // Gradient fill under the line
        val fillPath = Path().apply {
            moveTo(points.first().x, chartBottom)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, chartBottom)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(goldColor.copy(alpha = 0.35f), Color.Transparent),
                startY = chartTop,
                endY = chartBottom
            )
        )

        // Line connecting points
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawPath(
            path = linePath,
            color = goldColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Dots at each data point
        points.forEach { pt ->
            drawCircle(color = goldColor, radius = 3.5.dp.toPx(), center = pt)
            drawCircle(color = Color(0xFF150535), radius = 1.5.dp.toPx(), center = pt)
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