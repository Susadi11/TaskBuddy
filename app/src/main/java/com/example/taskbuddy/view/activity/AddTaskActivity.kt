package com.example.taskbuddy.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taskbuddy.R
import com.example.taskbuddy.databinding.AddTaskBinding
import com.example.taskbuddy.model.database.Task
import com.example.taskbuddy.view.viewmodel.TaskViewModel
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: AddTaskBinding
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveTaskButton.setOnClickListener {
            val taskName = binding.titleEditText.text.toString()
            val taskDescription = binding.descriptionEditText.text.toString()

            if (taskName.isNotEmpty() && taskDescription.isNotEmpty()) {
                val newTask = Task(taskName = taskName, taskDescription = taskDescription)
                saveTask(newTask)
            }
        }
    }

    private fun saveTask(task: Task) {
        lifecycleScope.launch {
            taskViewModel.insert(task)

            // After saving the task, navigate back to the main activity
            val intent = Intent(this@AddTaskActivity, MainActivity::class.java)
            startActivity(intent)
            finish() // Closes the AddTaskActivity after saving the task
        }
    }
}
