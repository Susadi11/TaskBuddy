package com.example.taskbuddy.model.repository

import androidx.lifecycle.LiveData
import com.example.taskbuddy.model.data.Task
import com.example.taskbuddy.model.data.TaskDao

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }
}
