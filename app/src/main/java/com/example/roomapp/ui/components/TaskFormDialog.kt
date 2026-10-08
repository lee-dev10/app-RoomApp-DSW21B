package com.example.roomapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.roomapp.data.model.TaskCategory
import com.example.roomapp.data.model.TaskPriority
import com.example.roomapp.data.model.TaskStatus
import com.example.roomapp.ui.viewmodel.TaskFormState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskFormDialog(
    formState: TaskFormState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (TaskCategory) -> Unit,
    onPriorityChange: (TaskPriority) -> Unit,
    onStatusChange: (TaskStatus) -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!formState.isFormOpen) return

    val isEditing = formState.editingTaskId != null

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (isEditing) "Editar Tarea" else "Nueva Tarea",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                OutlinedTextField(
                    value = formState.title,
                    onValueChange = onTitleChange,
                    label = { Text("Título *") },
                    isError = formState.titleError != null,
                    supportingText = {
                        formState.titleError?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description Input
                OutlinedTextField(
                    value = formState.description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Descripción") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category Selection
                Text(
                    text = "Categoría",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TaskCategory.entries
                        .filter { it != TaskCategory.TODAS }
                        .forEach { category ->
                            FilterChip(
                                selected = formState.category == category,
                                onClick = { onCategoryChange(category) },
                                label = { Text(category.label) }
                            )
                        }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Priority Selection
                Text(
                    text = "Prioridad",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TaskPriority.entries.forEach { priority ->
                        FilterChip(
                            selected = formState.priority == priority,
                            onClick = { onPriorityChange(priority) },
                            label = { Text(priority.label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Status Selection
                Text(
                    text = "Estado",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TaskStatus.entries.forEach { status ->
                        FilterChip(
                            selected = formState.status == status,
                            onClick = { onStatusChange(status) },
                            label = { Text(status.label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Due Date Selection
                Text(
                    text = "Fecha de Vencimiento",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                val todayCal = Calendar.getInstance()
                val tomorrowCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
                val weekCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 7) }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = formState.dueDateMillis == null,
                        onClick = { onDueDateChange(null) },
                        label = { Text("Sin fecha") }
                    )
                    FilterChip(
                        selected = isSameDay(formState.dueDateMillis, todayCal.timeInMillis),
                        onClick = { onDueDateChange(todayCal.timeInMillis) },
                        label = { Text("Hoy") }
                    )
                    FilterChip(
                        selected = isSameDay(formState.dueDateMillis, tomorrowCal.timeInMillis),
                        onClick = { onDueDateChange(tomorrowCal.timeInMillis) },
                        label = { Text("Mañana") }
                    )
                    FilterChip(
                        selected = isSameDay(formState.dueDateMillis, weekCal.timeInMillis),
                        onClick = { onDueDateChange(weekCal.timeInMillis) },
                        label = { Text("En 1 semana") }
                    )
                }

                formState.dueDateMillis?.let { dateMillis ->
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    Text(
                        text = "Seleccionada: ${dateFormat.format(Date(dateMillis))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Button(onClick = onSave) {
                        Text(if (isEditing) "Actualizar" else "Guardar")
                    }
                }
            }
        }
    }
}

private fun isSameDay(time1: Long?, time2: Long): Boolean {
    if (time1 == null) return false
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
