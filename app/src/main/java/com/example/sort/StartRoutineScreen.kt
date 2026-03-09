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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.West
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Mock data ──────────────────────────────────────────────────────────────

private data class MockRoutine(
    val name: String,
    val minutes: Int,
    val exercises: Int,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color
)

private val mockFeatured = MockRoutine(
    name = "Pull Day",
    minutes = 50,
    exercises = 6,
    icon = Icons.Default.FitnessCenter,
    iconTint = Color.White,
    iconBg = Color.Transparent
)

private val mockRoutines = listOf(
    MockRoutine(
        name = "Cardio Burn",
        minutes = 25,
        exercises = 4,
        icon = Icons.Default.Timer,
        iconTint = Color(0xFF256AF4),
        iconBg = Color(0xFF256AF4).copy(alpha = 0.15f)
    ),
    MockRoutine(
        name = "Leg Day",
        minutes = 45,
        exercises = 8,
        icon = Icons.Default.SelfImprovement,
        iconTint = Color(0xFF34D399),
        iconBg = Color(0xFF34D399).copy(alpha = 0.15f)
    ),
    MockRoutine(
        name = "Upper Body Power",
        minutes = 60,
        exercises = 10,
        icon = Icons.Default.FitnessCenter,
        iconTint = Color(0xFFFB923C),
        iconBg = Color(0xFFFB923C).copy(alpha = 0.15f)
    )
)

// ─── Screen ─────────────────────────────────────────────────────────────────

@Composable
fun StartRoutineScreen(
    onBack: () -> Unit = {},
    onStartSuggested: () -> Unit = {},
    onRoutineClick: (String) -> Unit = {},
    onCreateRoutine: () -> Unit = {}
) {
    val bgGradient = Brush.verticalGradient(
        listOf(Color(0xFF020617), Color(0xFF0C1445), Color(0xFF2E1065))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        // ── Content ──────────────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize()
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
                        text = "Start Workout",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }

            // Suggested for Today
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
                    Text(
                        text = "Suggested for Today",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FeaturedRoutineCard(
                        routine = mockFeatured,
                        onStart = onStartSuggested
                    )
                }
            }

            // Your Routines header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Routines",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "View All",
                        color = Color(0xFF256AF4),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Routine cards
            items(mockRoutines) { routine ->
                RoutineListCard(
                    routine = routine,
                    onClick = { onRoutineClick(routine.name) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Create custom routine button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 2.dp,
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onCreateRoutine() }
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Create Custom Routine",
                            color = Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

// ─── Featured routine card ───────────────────────────────────────────────────

@Composable
private fun FeaturedRoutineCard(
    routine: MockRoutine,
    onStart: () -> Unit
) {
    val cardGradient = Brush.linearGradient(
        listOf(Color(0xFF256AF4), Color(0xFF4F46E5), Color(0xFF7C3AED))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardGradient)
            .padding(24.dp)
    ) {
        // Background blur circle decoration
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(140.dp)
                .background(
                    brush = Brush.radialGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Transparent)),
                    shape = CircleShape
                )
        )

        // Background icon watermark
        Icon(
            imageVector = Icons.Default.FitnessCenter,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.12f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(80.dp)
        )

        Column {
            // Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "FEATURED ROUTINE",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = routine.name,
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${routine.minutes} min",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${routine.exercises} exercises",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Start button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable { onStart() }
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Start Suggested Workout",
                    color = Color(0xFF256AF4),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = Color(0xFF256AF4),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// ─── Routine list card ────────────────────────────────────────────────────────

@Composable
private fun RoutineListCard(
    routine: MockRoutine,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(routine.iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = routine.icon,
                contentDescription = null,
                tint = routine.iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = routine.name,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${routine.minutes} min • ${routine.exercises} exercises",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }

        // Chevron
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
