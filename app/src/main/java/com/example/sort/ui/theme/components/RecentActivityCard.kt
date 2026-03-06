package com.example.sort.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RecentActivityCard() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF7311D4).copy(.08f),
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                Color(0xFF7311D4).copy(.15f),
                RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                "Atividade Recente",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text("Ver tudo", color = Color(0xFF7311D4))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color(0xFF7311D4).copy(.3f),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(Icons.Default.History, null, tint = Color.White)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {

                Text("Treino de Pernas", color = Color.White)

                Text(
                    "Há 2 horas • 45 min",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                "320 kcal",
                color = Color(0xFF7311D4),
                fontWeight = FontWeight.Bold
            )
        }
    }
}