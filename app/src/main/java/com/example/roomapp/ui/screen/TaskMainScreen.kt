package com.example.roomapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.roomapp.data.local.TaskEntity
import com.example.roomapp.data.model.TaskCategory
import com.example.roomapp.data.model.TaskStatus
import com.example.roomapp.ui.components.DeleteConfirmDialog
import com.example.roomapp.ui.components.StatsHeader
import com.example.roomapp.ui.components.TaskCard
import com.example.roomapp.ui.components.TaskFormDialog
import com.example.roomapp.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskMainScreen(
    viewModel: TaskViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    var showMenu by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var showDeleteCompletedDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gestión de Tareas",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones"
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Limpiar completadas") },
                            onClick = {
                                showMenu = false
                                showDeleteCompletedDialog = true
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar todas las tareas") },
                            onClick = {
                                showMenu = false
                                showDeleteAllDialog = true
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreateForm() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar nueva tarea"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Stats
            StatsHeader(stats = uiState.stats)

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Buscar tarea por título o descripción...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar búsqueda"
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status Filter Chips
            Text(
                text = "Filtrar por Estado:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val statusFilters = listOf("TODAS") + TaskStatus.entries.map { it.label }
                statusFilters.forEach { statusLabel ->
                    FilterChip(
                        selected = uiState.selectedStatusFilter.equals(statusLabel, ignoreCase = true),
                        onClick = { viewModel.onStatusFilterChange(statusLabel) },
                        label = { Text(statusLabel) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Category Filter Chips
            Text(
                text = "Filtrar por Categoría:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TaskCategory.entries.forEach { category ->
                    FilterChip(
                        selected = uiState.selectedCategoryFilter == category.label,
                        onClick = { viewModel.onCategoryFilterChange(category.label) },
                        label = { Text(category.label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tasks List or Empty State
            if (uiState.tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Task,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (uiState.searchQuery.isNotEmpty() ||
                                uiState.selectedStatusFilter != "TODAS" ||
                                uiState.selectedCategoryFilter != TaskCategory.TODAS.label
                            ) {
                                "No se encontraron tareas con los filtros aplicados."
                            } else {
                                "No tienes tareas registradas.\n¡Haz clic en + para agregar una!"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tareas (${uiState.tasks.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(
                        items = uiState.tasks,
                        key = { it.id }
                    ) { task ->
                        TaskCard(
                            task = task,
                            onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                            onEdit = { viewModel.openEditForm(task) },
                            onDelete = { taskToDelete = task }
                        )
                    }
                }
            }
        }
    }

    // Form Dialog
    TaskFormDialog(
        formState = formState,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onCategoryChange = viewModel::onCategoryChange,
        onPriorityChange = viewModel::onPriorityChange,
        onStatusChange = viewModel::onStatusChange,
        onDueDateChange = viewModel::onDueDateChange,
        onSave = viewModel::saveTask,
        onDismiss = viewModel::closeForm
    )

    // Delete Single Task Confirmation Dialog
    taskToDelete?.let { task ->
        DeleteConfirmDialog(
            title = "Eliminar Tarea",
            message = "¿Estás seguro de que deseas eliminar la tarea \"${task.title}\"?",
            onConfirm = { viewModel.deleteTask(task.id) },
            onDismiss = { taskToDelete = null }
        )
    }

    // Delete All Confirmation Dialog
    if (showDeleteAllDialog) {
        DeleteConfirmDialog(
            title = "Eliminar todas las tareas",
            message = "¿Estás seguro de que deseas eliminar TODAS las tareas guardadas? Esta acción no se puede deshacer.",
            onConfirm = { viewModel.deleteAllTasks() },
            onDismiss = { showDeleteAllDialog = false }
        )
    }

    // Delete Completed Confirmation Dialog
    if (showDeleteCompletedDialog) {
        DeleteConfirmDialog(
            title = "Limpiar tareas completadas",
            message = "¿Deseas eliminar todas las tareas que ya han sido marcadas como completadas?",
            onConfirm = { viewModel.deleteCompletedTasks() },
            onDismiss = { showDeleteCompletedDialog = false }
        )
    }
}
