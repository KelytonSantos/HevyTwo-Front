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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.West
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import com.example.sort.data.EditableExercise
import com.example.sort.data.EditableSet
import com.example.sort.viewmodel.EditRoutineViewModel

@Composable
fun EditRoutineScreen(
    routineId: String,
    routineName: String,
    reloadTrigger: Int = 0,
    onBack: () -> Unit = {},
    onAddExercises: (addedIds: Set<String>) -> Unit = {}
) {
    val viewModel: EditRoutineViewModel = viewModel()

    LaunchedEffect(routineId, reloadTrigger) {
        viewModel.load(routineId, routineName)
    }

    // Auto-dismiss success banner after 2s
    LaunchedEffect(viewModel.saveSuccess) {
        if (viewModel.saveSuccess) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearSaveState()
        }
    }

    val bgGradient = Brush.verticalGradient(
        listOf(Color(0xFF020617), Color(0xFF0C1445), Color(0xFF2E1065))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 96.dp), // space for fixed footer
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 24.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.West,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = viewModel.routineName.ifBlank { "Edit Routine" },
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center
                    )
                    // Spacer for visual balance
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }

            // Summary cards
            item {
                SummaryCards(
                    totalSets = viewModel.totalSets,
                    totalVolume = viewModel.totalVolume,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Loading state
            if (viewModel.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF7311D4))
                    }
                }
            }

            // Error state
            viewModel.errorMessage?.let { msg ->
                item {
                    Text(
                        text = msg,
                        color = Color(0xFFEF4444),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Exercise sections
            itemsIndexed(viewModel.exercises) { exIndex, exercise ->
                ExerciseSection(
                    exercise = exercise,
                    exerciseIndex = exIndex,
                    onRestTimeChange = { viewModel.updateRestTime(exIndex, it) },
                    onWeightChange = { si, v -> viewModel.updateSetWeight(exIndex, si, v) },
                    onRepsChange = { si, v -> viewModel.updateSetReps(exIndex, si, v) },
                    onAddSet = { viewModel.addSet(exIndex) },
                    onDeleteSet = { si -> viewModel.deleteSet(exIndex, si) },
                    onRemoveExercise = { viewModel.removeExercise(exIndex) }
                )
            }

            // Add Exercise — dashed button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 2.dp,
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onAddExercises(viewModel.addedExerciseApiIds) }
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add Exercise",
                            color = Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // Fixed bottom — Save Routine
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xFF020617).copy(alpha = 0.9f))
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Success banner
                if (viewModel.saveSuccess) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF166534).copy(alpha = 0.9f))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4ADE80),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Rotina salva com sucesso!",
                            color = Color(0xFF4ADE80),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Save button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (viewModel.isSaving)
                                Brush.horizontalGradient(listOf(Color(0xFF4B5563), Color(0xFF374151)))
                            else
                                Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFFA855F7)))
                        )
                        .clickable(enabled = !viewModel.isSaving) { viewModel.saveRoutine() }
                        .padding(vertical = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Save Routine",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Summary cards row
// ─────────────────────────────────────────────

@Composable
private fun SummaryCards(
    totalSets: Int,
    totalVolume: Double,
    modifier: Modifier = Modifier
) {
    val cardBg = Color.White.copy(alpha = 0.08f)
    val cardBorder = Color.White.copy(alpha = 0.1f)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SummaryCard(
            label = "DURATION",
            value = "00:00",
            modifier = Modifier.weight(1f),
            bg = cardBg,
            border = cardBorder
        )
        SummaryCard(
            label = "VOLUME",
            value = if (totalVolume > 0) "${totalVolume.toInt()} kg" else "0 kg",
            modifier = Modifier.weight(1f),
            bg = cardBg,
            border = cardBorder
        )
        SummaryCard(
            label = "SETS",
            value = totalSets.toString(),
            modifier = Modifier.weight(1f),
            bg = cardBg,
            border = cardBorder
        )
    }
}

@Composable
private fun SummaryCard(
    label: String,
    value: String,
    bg: Color,
    border: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

// ─────────────────────────────────────────────
// Exercise section
// ─────────────────────────────────────────────

@Composable
private fun ExerciseSection(
    exercise: EditableExercise,
    exerciseIndex: Int,
    onRestTimeChange: (String) -> Unit,
    onWeightChange: (Int, String) -> Unit,
    onRepsChange: (Int, String) -> Unit,
    onAddSet: () -> Unit,
    onDeleteSet: (Int) -> Unit,
    onRemoveExercise: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showRestTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components { add(ImageDecoderDecoder.Factory()) }
            .build()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = if (exerciseIndex == 0) 8.dp else 24.dp)
    ) {
        // Exercise header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // GIF or fallback icon
            if (exercise.workoutImage != null) {
                AsyncImage(
                    model = exercise.workoutImage,
                    imageLoader = imageLoader,
                    contentDescription = exercise.exerciseName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                )
            } else {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = Color(0xFF256AF4),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = exercise.exerciseName,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Box {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { menuExpanded = true }
                )
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Remove exercise",
                                color = Color(0xFFEF4444)
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onRemoveExercise()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (showRestTimePicker) {
            RestTimePickerDialog(
                initial = exercise.restTime,
                onDismiss = { showRestTimePicker = false },
                onConfirm = {
                    onRestTimeChange(it)
                    showRestTimePicker = false
                }
            )
        }

        // Rest time row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                .clickable { showRestTimePicker = true }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = Color(0xFF256AF4),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Rest Time",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = exercise.restTime,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            Text(
                text = "editar",
                color = Color(0xFF7311D4),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sets table
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.03f))
        ) {
            // Table header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.07f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SET",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(32.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "PREVIOUS",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "KG",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(64.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "REPS",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(56.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Set rows
            exercise.sets.forEachIndexed { setIndex, set ->
                SetRow(
                    set = set,
                    isLast = setIndex == exercise.sets.lastIndex,
                    onWeightChange = { onWeightChange(setIndex, it) },
                    onRepsChange = { onRepsChange(setIndex, it) },
                    onDeleteSet = { onDeleteSet(setIndex) }
                )
            }

            // Add Set button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAddSet() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ADD SET",
                        color = Color.White.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Single set row
// ─────────────────────────────────────────────

@Composable
private fun SetRow(
    set: EditableSet,
    isLast: Boolean,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onDeleteSet: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Deletar set") },
            text = { Text("Deseja remover o set ${set.setNumber}?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDeleteSet()
                }) {
                    Text("Deletar", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    val rowBg = if (set.isCompleted)
        Color(0xFF7311D4).copy(alpha = 0.1f)
    else
        Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set number — tap to delete
        Box(
            modifier = Modifier
                .width(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .clickable { showDeleteDialog = true }
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = set.setNumber.toString(),
                color = Color(0xFFEF4444).copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

        // Previous (placeholder)
        Text(
            text = "--",
            color = Color.White.copy(alpha = 0.35f),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        // Weight input
        CompactNumberInput(
            value = set.weight,
            onValueChange = onWeightChange,
            placeholder = "0",
            modifier = Modifier.width(64.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))

        // Reps input
        CompactNumberInput(
            value = set.reps,
            onValueChange = onRepsChange,
            placeholder = "0",
            modifier = Modifier.width(56.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
    }

    // Divider between rows
    if (!isLast) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.07f))
        )
    }
}

// ─────────────────────────────────────────────
// Compact number input field
// ─────────────────────────────────────────────

@Composable
private fun CompactNumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = { new ->
            // Only allow digits and one decimal point
            if (new.all { it.isDigit() || it == '.' }) onValueChange(new)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = TextStyle(
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        ),
        cursorBrush = SolidColor(Color(0xFF7311D4)),
        singleLine = true,
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.1f)),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color.White.copy(alpha = 0.35f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                innerTextField()
            }
        }
    )
}

// ─────────────────────────────────────────────
// Rest time wheel picker dialog
// ─────────────────────────────────────────────

@Composable
private fun RestTimePickerDialog(
    initial: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val parts = initial.split(":")
    val initMin = parts.getOrNull(0)?.toIntOrNull()?.coerceIn(0, 10) ?: 1
    val initSec = parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: 30

    var selectedMinutes by remember { mutableStateOf(initMin) }
    var selectedSeconds by remember { mutableStateOf(if (initMin >= 10) 0 else initSec) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0C1445))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tempo de Descanso",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "máx. 10 minutos",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Minutes (0..10)
                WheelPicker(
                    count = 11,
                    selectedIndex = selectedMinutes,
                    onIndexSelected = { min ->
                        selectedMinutes = min
                        if (min == 10) selectedSeconds = 0
                    },
                    label = { it.toString().padStart(2, '0') },
                    modifier = Modifier.width(80.dp)
                )
                Text(
                    text = ":",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                // Seconds (0..59 or locked at 0 when min=10)
                key(selectedMinutes == 10) {
                    WheelPicker(
                        count = if (selectedMinutes == 10) 1 else 60,
                        selectedIndex = selectedSeconds,
                        onIndexSelected = { sec -> selectedSeconds = sec },
                        label = { it.toString().padStart(2, '0') },
                        modifier = Modifier.width(80.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "min",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    modifier = Modifier.width(80.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(44.dp))
                Text(
                    text = "seg",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    modifier = Modifier.width(80.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = Color.White.copy(alpha = 0.5f))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF7311D4))
                        .clickable {
                            onConfirm("%02d:%02d".format(selectedMinutes, selectedSeconds))
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Confirmar", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Drum / scroll-wheel column picker
// ─────────────────────────────────────────────

@Composable
private fun WheelPicker(
    count: Int,
    selectedIndex: Int,
    onIndexSelected: (Int) -> Unit,
    label: (Int) -> String,
    modifier: Modifier = Modifier
) {
    val itemHeight = 44.dp
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = selectedIndex.coerceIn(0, maxOf(0, count - 1))
    )
    val snapBehavior = rememberSnapFlingBehavior(listState)

    // Report center item when scroll settles
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val idx = listState.firstVisibleItemIndex.coerceIn(0, count - 1)
            onIndexSelected(idx)
        }
    }

    Box(modifier = modifier.height(itemHeight * 3)) {
        // Selection highlight band in the center
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeight)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.1f))
        )
        // Top fade
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(itemHeight)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0C1445), Color.Transparent)
                    )
                )
        )
        // Bottom fade
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(itemHeight)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xFF0C1445))
                    )
                )
        )
        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            contentPadding = PaddingValues(vertical = itemHeight),
            modifier = Modifier.fillMaxSize()
        ) {
            items(count) { index ->
                val isCenter by remember(listState) {
                    derivedStateOf { listState.firstVisibleItemIndex == index }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label(index),
                        color = if (isCenter) Color.White else Color.White.copy(alpha = 0.3f),
                        fontWeight = if (isCenter) FontWeight.Bold else FontWeight.Normal,
                        fontSize = if (isCenter) 26.sp else 18.sp
                    )
                }
            }
        }
    }
}
