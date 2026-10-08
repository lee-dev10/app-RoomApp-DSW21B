package com.example.roomapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomapp.data.local.DatabaseProvider
import com.example.roomapp.data.local.TaskEntity
import com.example.roomapp.data.model.TaskCategory
import com.example.roomapp.data.model.TaskPriority
import com.example.roomapp.data.model.TaskStatus
import com.example.roomapp.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TaskStats(
    val total: Int = 0,
    val pending: Int = 0,
    val inProgress: Int = 0,
    val completed: Int = 0
)

data class TaskUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val stats: TaskStats = TaskStats(),
    val searchQuery: String = "",
    val selectedStatusFilter: String = "TODAS",
    val selectedCategoryFilter: String = TaskCategory.TODAS.label,
    val isLoading: Boolean = false
)

data class TaskFormState(
    val editingTaskId: Long? = null,
    val title: String = "",
    val description: String = "",
    val category: TaskCategory = TaskCategory.PERSONAL,
    val priority: TaskPriority = TaskPriority.MEDIA,
    val status: TaskStatus = TaskStatus.PENDIENTE,
    val dueDateMillis: Long? = null,
    val titleError: String? = null,
    val isFormOpen: Boolean = false
)

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    init {
        val database = DatabaseProvider.getDatabase(application)
        repository = TaskRepository(database.taskDao())
    }

    private val _searchQuery = MutableStateFlow("")
    private val _selectedStatusFilter = MutableStateFlow("TODAS")
    private val _selectedCategoryFilter = MutableStateFlow(TaskCategory.TODAS.label)

    private val _allTasks = repository.observeAllTasks()

    val uiState: StateFlow<TaskUiState> = combine(
        _allTasks,
        _searchQuery,
        _selectedStatusFilter,
        _selectedCategoryFilter
    ) { allTasks, searchQuery, statusFilter, categoryFilter ->

        val stats = TaskStats(
            total = allTasks.size,
            pending = allTasks.count { it.status == TaskStatus.PENDIENTE.label },
            inProgress = allTasks.count { it.status == TaskStatus.EN_PROGRESO.label },
            completed = allTasks.count { it.status == TaskStatus.COMPLETADA.label }
        )

        val filtered = allTasks.filter { task ->
            val matchesSearch = searchQuery.isBlank() ||
                    task.title.contains(searchQuery, ignoreCase = true) ||
                    task.description.contains(searchQuery, ignoreCase = true)

            val matchesStatus = when (statusFilter) {
                "TODAS" -> true
                else -> task.status.equals(statusFilter, ignoreCase = true)
            }

            val matchesCategory = when (categoryFilter) {
                TaskCategory.TODAS.label -> true
                else -> task.category.equals(categoryFilter, ignoreCase = true)
            }

            matchesSearch && matchesStatus && matchesCategory
        }

        TaskUiState(
            tasks = filtered,
            stats = stats,
            searchQuery = searchQuery,
            selectedStatusFilter = statusFilter,
            selectedCategoryFilter = categoryFilter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskUiState(isLoading = true)
    )

    private val _formState = MutableStateFlow(TaskFormState())
    val formState: StateFlow<TaskFormState> = _formState

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterChange(status: String) {
        _selectedStatusFilter.value = status
    }

    fun onCategoryFilterChange(category: String) {
        _selectedCategoryFilter.value = category
    }

    fun openCreateForm() {
        _formState.value = TaskFormState(isFormOpen = true)
    }

    fun openEditForm(task: TaskEntity) {
        val categoryEnum = TaskCategory.entries.find { it.label == task.category } ?: TaskCategory.PERSONAL
        val priorityEnum = TaskPriority.entries.find { it.label == task.priority } ?: TaskPriority.MEDIA
        val statusEnum = TaskStatus.entries.find { it.label == task.status } ?: TaskStatus.PENDIENTE

        _formState.value = TaskFormState(
            editingTaskId = task.id,
            title = task.title,
            description = task.description,
            category = categoryEnum,
            priority = priorityEnum,
            status = statusEnum,
            dueDateMillis = task.dueDateMillis,
            isFormOpen = true
        )
    }

    fun closeForm() {
        _formState.value = TaskFormState(isFormOpen = false)
    }

    fun onTitleChange(title: String) {
        _formState.value = _formState.value.copy(
            title = title,
            titleError = if (title.isNotBlank()) null else _formState.value.titleError
        )
    }

    fun onDescriptionChange(description: String) {
        _formState.value = _formState.value.copy(description = description)
    }

    fun onCategoryChange(category: TaskCategory) {
        _formState.value = _formState.value.copy(category = category)
    }

    fun onPriorityChange(priority: TaskPriority) {
        _formState.value = _formState.value.copy(priority = priority)
    }

    fun onStatusChange(status: TaskStatus) {
        _formState.value = _formState.value.copy(status = status)
    }

    fun onDueDateChange(dueDateMillis: Long?) {
        _formState.value = _formState.value.copy(dueDateMillis = dueDateMillis)
    }

    fun saveTask() {
        val currentForm = _formState.value
        val titleTrimmed = currentForm.title.trim()

        if (titleTrimmed.isEmpty()) {
            _formState.value = currentForm.copy(
                titleError = "El título es obligatorio"
            )
            return
        }

        val taskToSave = TaskEntity(
            id = currentForm.editingTaskId ?: 0,
            title = titleTrimmed,
            description = currentForm.description.trim(),
            category = currentForm.category.label,
            priority = currentForm.priority.label,
            status = currentForm.status.label,
            dueDateMillis = currentForm.dueDateMillis
        )

        viewModelScope.launch {
            if (currentForm.editingTaskId == null || currentForm.editingTaskId == 0L) {
                repository.insertTask(taskToSave)
            } else {
                repository.updateTask(taskToSave)
            }
            closeForm()
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            val newStatus = if (task.status == TaskStatus.COMPLETADA.label) {
                TaskStatus.PENDIENTE.label
            } else {
                TaskStatus.COMPLETADA.label
            }
            repository.updateTask(task.copy(status = newStatus))
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            repository.deleteTaskById(id)
        }
    }

    fun deleteAllTasks() {
        viewModelScope.launch {
            repository.deleteAllTasks()
        }
    }

    fun deleteCompletedTasks() {
        viewModelScope.launch {
            repository.deleteCompletedTasks()
        }
    }
}
