package com.example.roomapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roomapp.ui.viewmodel.TaskStats

@Composable
fun StatsHeader(
    stats: TaskStats,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            title = "Total",
            count = stats.total,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Pendientes",
            count = stats.pending,
            containerColor = Color(0xFFFFF3E0), // Light Amber
            contentColor = Color(0xFFE65100),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "En Progreso",
            count = stats.inProgress,
            containerColor = Color(0xFFE1F5FE), // Light Blue
            contentColor = Color(0xFF0277BD),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Completadas",
            count = stats.completed,
            containerColor = Color(0xE8E8F5E9), // Light Green
            contentColor = Color(0xFF2E7D32),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    count: Int,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 4.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = contentColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
