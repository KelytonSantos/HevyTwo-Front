package com.example.sort.ui.theme.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// DENTRO DO ARQUIVO FloatingWorkoutButton.kt

@Composable
fun FloatingWorkoutButton(onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        // 1. O Brilho (Blur) atrás do botão
        Box(
            modifier = Modifier
                // Aumente o 'end' para mover para a ESQUERDA
                // Diminua o 'bottom' para mover para BAIXO
                .padding(end = 40.dp, bottom = 40.dp)
                .size(80.dp)
                .background(
                    Color(0xFF7311D4).copy(alpha = 0.3f),
                    shape = CircleShape
                )
                .blur(40.dp)
        )

        // 2. O Botão com Gradiente
        Box(
            modifier = Modifier
                // Aumente o 'end' para mover para a ESQUERDA (ex: de 32 para 44)
                // Diminua o 'bottom' para mover para BAIXO (ex: de 110 para 30)
                .padding(end = 44.dp, bottom = 30.dp)
                .size(56.dp)
                .background(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF7311D4), Color(0xFFD411A6))
                    ),
                    shape = CircleShape
                )
                .clickable { onClick() }
                .border(1.dp, Color.White.copy(0.2f), CircleShape),

            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = "Workout",
                tint = Color.White
            )
        }
    }
}
