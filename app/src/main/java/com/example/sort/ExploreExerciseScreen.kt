package com.example.sort

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.West
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import com.example.sort.viewmodel.RoutineBuilderViewModel

@Composable
fun ExploreExerciseScreen(
    selectionMode: Boolean = false,
    routineBuilderViewModel: RoutineBuilderViewModel? = null,
    /** Pre-populated IDs (Wger API IDs) already added to the routine — shown as "Adicionado". */
    initialAddedIds: Set<String> = emptySet(),
    /** When set, exercises are added directly via this callback instead of going
     *  through [routineBuilderViewModel]. Used in the EditRoutine flow. */
    onExerciseSelected: ((com.example.sort.data.ExerciseDto) -> Unit)? = null,
    onBack: () -> Unit = {}
) {
    // Track locally which exercise IDs were already added (for direct-add / edit flow)
    var localAddedIds by remember(initialAddedIds) { mutableStateOf(initialAddedIds) }
    val viewModel: com.example.sort.viewmodel.ExploreExerciseViewModel = viewModel()
    val filters = listOf("Strength", "Cardio", "Yoga", "Stretch")
    var selectedFilter by remember { mutableStateOf(filters.first()) }
    var query by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.loadFirstPage()
    }

    LaunchedEffect(listState, viewModel.exercises.size, viewModel.isLoading, viewModel.endReached) {
        snapshotFlow {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val canScrollForward = listState.canScrollForward
            val reachedEnd = lastVisibleItem >= viewModel.exercises.size - 1
            val listIsShort = !canScrollForward && viewModel.exercises.isNotEmpty()
            (reachedEnd || listIsShort) && !viewModel.isLoading && !viewModel.endReached
        }
            .distinctUntilChanged()
            .collect { shouldLoadNext ->
                if (shouldLoadNext) {
                    viewModel.loadNextPage()
                }
            }
    }

    val exercises = viewModel.exercises
        .filter { it.name.contains(query, ignoreCase = true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF020617), Color(0xFF0C1445), Color(0xFF2E1065))
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 24.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { onBack() }
                            .background(Color.White.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.West,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(0.dp))
                    Text(
                        text = "Explore Exercícios",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Buscar exercicios...", color = Color(0xFFB6B6D6)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFFB6B6D6)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color.White.copy(alpha = 0.08f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )
            }

            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filters) { filter ->
                        val isSelected = filter == selectedFilter
                        Surface(
                            color = if (isSelected) Color(0xFF7311D4) else Color.White.copy(alpha = 0.08f),
                            contentColor = Color.White,
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .clickable { selectedFilter = filter }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = filter,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (viewModel.isLoading && exercises.isEmpty()) {
                item {
                    Text(
                        text = "Carregando...",
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            viewModel.errorMessage?.let { message ->
                item {
                    Text(
                        text = message,
                        color = Color(0xFFFFB4B4),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            items(exercises) { exercise ->
                val isSelected = when {
                    onExerciseSelected != null -> localAddedIds.contains(exercise.exerciseId)
                    else -> routineBuilderViewModel?.isSelected(exercise.exerciseId) ?: false
                }
                ExerciseCard(
                    exercise = exercise,
                    selectionMode = selectionMode,
                    isSelected = isSelected,
                    onToggle = { selected ->
                        if (onExerciseSelected != null) {
                            // Direct-add mode (edit routine flow): call API and mark locally
                            if (selected && !localAddedIds.contains(exercise.exerciseId)) {
                                localAddedIds = localAddedIds + exercise.exerciseId
                                onExerciseSelected(exercise)
                            }
                        } else {
                            if (selected) {
                                routineBuilderViewModel?.addExercise(exercise)
                            } else {
                                routineBuilderViewModel?.removeExercise(exercise.exerciseId)
                            }
                        }
                    }
                )
            }

            if ((viewModel.isLoading || viewModel.isPendingLoad) && exercises.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: com.example.sort.data.ExerciseDto,
    selectionMode: Boolean = false,
    isSelected: Boolean = false,
    onToggle: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components {
                add(ImageDecoderDecoder.Factory())
            }
            .build()
    }

    val cardGradient = Brush.horizontalGradient(
        listOf(Color(0xFF081138), Color(0xFF0C1445), Color(0xFF2E1065))
    )

    val subtitle = when {
        exercise.instructions.isNotEmpty() -> exercise.instructions.first()
        exercise.targetMuscles.isNotEmpty() -> exercise.targetMuscles.joinToString(", ")
        else -> ""
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(cardGradient)
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(22.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.92f)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = exercise.gifUrl,
                imageLoader = imageLoader,
                contentDescription = exercise.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (selectionMode) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (isSelected) Color(0xFF7311D4) else Color.White.copy(alpha = 0.12f))
                    .clickable { onToggle(!isSelected) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isSelected) "Adicionado" else "Adicionar",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}
