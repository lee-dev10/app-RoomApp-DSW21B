package com.example.roomapp.data.repository

import com.example.roomapp.data.local.TaskDao
import com.example.roomapp.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao
) {
    fun observeAllTasks(): Flow<List<TaskEntity>> = taskDao.observeAllTasks()

    fun getTaskById(id: Long): Flow<TaskEntity?> = taskDao.getTaskById(id)

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    suspend fun deleteAllTasks() = taskDao.deleteAllTasks()

    suspend fun deleteCompletedTasks() = taskDao.deleteCompletedTasks()
}
