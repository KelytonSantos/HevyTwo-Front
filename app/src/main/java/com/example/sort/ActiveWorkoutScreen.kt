package com.example.sort

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sort.data.ActiveExercise
import com.example.sort.data.ActiveSet
import com.example.sort.viewmodel.ActiveWorkoutViewModel
import kotlinx.coroutines.delay

private val BgGradient = Brush.verticalGradient(
    listOf(Color(0xFF020617), Color(0xFF0C1445), Color(0xFF2E1065))
)
private val Primary       = Color(0xFF256AF4)
private val CardBg        = Color(0xFF0F172A)
private val CardBorder    = Color(0xFF1E293B)
private val HeaderRowBg   = Color(0xFF1E293B).copy(alpha = 0.5f)

private fun formatDuration(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return "%02d:%02d:%02d".format(h, m, s)
}

@Composable
fun ActiveWorkoutScreen(
    routineId: String,
    routineName: String,
    onClose: () -> Unit = {},
    viewModel: ActiveWorkoutViewModel = viewModel()
) {
    LaunchedEffect(routineId) {
        viewModel.initWorkout(routineId, routineName)
    }

    // Live timer — starts once loading is done
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    LaunchedEffect(viewModel.isLoading) {
        if (!viewModel.isLoading) {
            while (true) {
                delay(1000L)
                elapsedSeconds++
            }
        }
    }

    var showFinishDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancelar treino?") },
            text  = { Text("Seu progresso não será salvo. Deseja cancelar a sessão?") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    viewModel.cancelWorkout(onSuccess = onClose)
                }) { Text("Cancelar treino", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Continuar", color = Primary)
                }
            }
        )
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Finalizar treino?") },
            text  = { Text("Você tem certeza que quer finalizar a sessão de treino?") },
            confirmButton = {
                TextButton(onClick = {
                    showFinishDialog = false
                    viewModel.finishWorkout(onSuccess = onClose)
                }) { Text("Finalizar", color = Primary) }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header (same style as other screens) ──────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable { showCancelDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint     = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text       = viewModel.routineName.ifEmpty { routineName },
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp
                )

                // Ghost spacer to keep title centred
                Spacer(modifier = Modifier.size(40.dp))
            }


            // ── Body ──────────────────────────────────────────────────────────
            if (viewModel.isLoading) {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    // Stats
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            WorkoutStatCard(
                                label      = "DURATION",
                                value      = formatDuration(elapsedSeconds),
                                valueColor = Primary,
                                modifier   = Modifier.weight(1f)
                            )
                            WorkoutStatCard(
                                label    = "VOLUME",
                                value    = "${viewModel.totalVolume} kg",
                                modifier = Modifier.weight(1f)
                            )
                            WorkoutStatCard(
                                label    = "SETS",
                                value    = viewModel.completedSets.toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Exercise cards
                    itemsIndexed(viewModel.exercises) { exIdx, exercise ->
                        ExerciseCard(
                            exercise        = exercise,
                            onSetKgChange   = { si, v -> viewModel.updateSetKg(exIdx, si, v) },
                            onSetRepsChange = { si, v -> viewModel.updateSetReps(exIdx, si, v) },
                            onToggleSet     = { si     -> viewModel.toggleSet(exIdx, si) },
                            modifier = Modifier.padding(
                                start  = 16.dp,
                                end    = 16.dp,
                                top    = if (exIdx == 0) 4.dp else 24.dp,
                                bottom = 0.dp
                            )
                        )
                    }
                }
            }
        }

        // ── Bottom bar ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xFF020617).copy(alpha = 0.9f))
                .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(0.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Primary)
                    .clickable { showFinishDialog = true }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.isFinishing) {
                    CircularProgressIndicator(
                        color       = Color.White,
                        modifier    = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Finish Workout",
                        color      = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(0.07f))
                    .border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = "Timer",
                    tint     = Color.LightGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Error banner
        viewModel.errorMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp, start = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFB91C1C))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(msg, color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

// ── Exercise Card ─────────────────────────────────────────────────────────────

@Composable
private fun ExerciseCard(
    exercise: ActiveExercise,
    onSetKgChange: (Int, String) -> Unit,
    onSetRepsChange: (Int, String) -> Unit,
    onToggleSet: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Title + menu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = exercise.exerciseName,
                color      = Primary,
                fontWeight = FontWeight.Bold,
                fontSize   = 20.sp
            )
            Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = Color.Gray)
        }

        // Table
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardBg)
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
        ) {
            // Column headers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderRowBg)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("SET",      modifier = Modifier.weight(1f),   textAlign = TextAlign.Center, style = colHeaderStyle)
                Text("PREVIOUS", modifier = Modifier.weight(3.5f), textAlign = TextAlign.Center, style = colHeaderStyle)
                Text("KG",       modifier = Modifier.weight(2f),   textAlign = TextAlign.Center, style = colHeaderStyle)
                Text("REPS",     modifier = Modifier.weight(2f),   textAlign = TextAlign.Center, style = colHeaderStyle)
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint     = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Rows
            exercise.sets.forEachIndexed { setIdx, set ->
                SetRow(
                    set          = set,
                    setNumber    = setIdx + 1,
                    onKgChange   = { onSetKgChange(setIdx, it) },
                    onRepsChange = { onSetRepsChange(setIdx, it) },
                    onToggle     = { onToggleSet(setIdx) }
                )
                if (setIdx < exercise.sets.lastIndex) {
                    HorizontalDivider(color = CardBorder, thickness = 1.dp)
                }
            }
        }
    }
}

private val colHeaderStyle = TextStyle(
    color         = Color.Gray,
    fontSize      = 10.sp,
    fontWeight    = FontWeight.Bold,
    letterSpacing = 1.sp
)

// ── Set Row ───────────────────────────────────────────────────────────────────

@Composable
private fun SetRow(
    set: ActiveSet,
    setNumber: Int,
    onKgChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onToggle: () -> Unit
) {
    val rowBg = if (set.isCompleted) Primary.copy(alpha = 0.06f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text       = setNumber.toString(),
            color      = Color(0xFF94A3B8),
            fontWeight = FontWeight.Bold,
            fontSize   = 14.sp,
            textAlign  = TextAlign.Center,
            modifier   = Modifier.weight(1f)
        )

        val prev = if (set.previousKg.isNotEmpty() && set.previousReps.isNotEmpty())
            "${set.previousKg} kg x ${set.previousReps}"
        else "–"

        Text(
            text      = prev,
            color     = Color(0xFF64748B),
            fontSize  = 13.sp,
            textAlign = TextAlign.Center,
            modifier  = Modifier.weight(3.5f)
        )

        CompactInput(
            value         = set.kg,
            onValueChange = onKgChange,
            placeholder   = set.previousKg.ifEmpty { "–" },
            modifier      = Modifier.weight(2f).padding(horizontal = 5.dp),
            enabled        = !set.isCompleted
        )

        CompactInput(
            value         = set.reps,
            onValueChange = onRepsChange,
            placeholder   = set.previousReps.ifEmpty { "–" },
            modifier      = Modifier.weight(2f).padding(horizontal = 5.dp),
            enabled        = !set.isCompleted
        )

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            if (set.isLoading) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(18.dp),
                    color       = Primary,
                    strokeWidth = 2.dp
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (set.isCompleted) Primary else Color.Transparent)
                        .border(
                            1.5.dp,
                            if (set.isCompleted) Primary else Color(0xFF475569),
                            RoundedCornerShape(5.dp)
                        )
                        .clickable(onClick = onToggle),
                    contentAlignment = Alignment.Center
                ) {
                    if (set.isCompleted) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint     = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Compact input ─────────────────────────────────────────────────────────────

@Composable
private fun CompactInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    BasicTextField(
        value           = value,
        onValueChange   = onValueChange,
        enabled         = enabled,
        singleLine      = true,
        modifier        = modifier,
        textStyle = TextStyle(
            color      = if (enabled) Color.White else Color(0xFF94A3B8),
            fontWeight = FontWeight.Bold,
            fontSize   = 14.sp,
            textAlign  = TextAlign.Center
        ),
        cursorBrush     = SolidColor(Primary),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction    = ImeAction.Next
        ),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                if (value.isEmpty()) {
                    Text(
                        text       = placeholder,
                        color      = Color(0xFF475569),
                        fontWeight = FontWeight.Bold,
                        fontSize   = 14.sp,
                        textAlign  = TextAlign.Center,
                        modifier   = Modifier.fillMaxWidth()
                    )
                }
                inner()
            }
        }
    )
}

// ── Stat card ─────────────────────────────────────────────────────────────────

@Composable
private fun WorkoutStatCard(
    label: String,
    value: String,
    valueColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(0.05f))
            .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            label,
            color         = Color(0xFF94A3B8),
            fontSize      = 10.sp,
            fontWeight    = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Text(
            value,
            color      = valueColor,
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp
        )
    }
}
