package com.example.taskbuddy.view.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.taskbuddy.R
import com.example.taskbuddy.databinding.AddTaskBinding
import com.example.taskbuddy.model.database.Priority
import com.example.taskbuddy.model.database.Task
import com.example.taskbuddy.view.viewmodel.TaskViewModel
import androidx.activity.viewModels
import kotlinx.coroutines.launch
import java.util.*

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: AddTaskBinding
    private val taskViewModel: TaskViewModel by viewModels()
    private var selectedDeadline: Long = 0L // Stores deadline timestamp
    private var selectedPriority: Priority = Priority.LOW // Default priority

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up date picker for deadline
        binding.showDatePickerButton.setOnClickListener {
            showDatePicker()
        }

        // Handle priority selection
        binding.priorityRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedPriority = when (checkedId) {
                R.id.highPriorityRadioButton -> Priority.HIGH
                R.id.mediumPriorityRadioButton -> Priority.MEDIUM
                R.id.lowPriorityRadioButton -> Priority.LOW
                else -> Priority.LOW
            }
        }

        // Save the task when the button is clicked
        binding.saveTaskButton.setOnClickListener {
            val taskName = binding.titleEditText.text.toString()
            val taskDescription = binding.descriptionEditText.text.toString()

            if (taskName.isNotEmpty() && taskDescription.isNotEmpty() && selectedDeadline != 0L) {
                val newTask = Task(
                    taskName = taskName,
                    taskDescription = taskDescription,
                    priority = selectedPriority,
                    deadline = selectedDeadline
                )
                saveTask(newTask)
            } else {
                Toast.makeText(this, "Please fill all fields and set a deadline", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveTask(task: Task) {
        lifecycleScope.launch {
            taskViewModel.insert(task)

            // After saving the task, navigate back to the main activity
            val intent = Intent(this@AddTaskActivity, MainActivity::class.java)
            startActivity(intent)
            finish() // Close AddTaskActivity after saving the task
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
            selectedDeadline = selectedCalendar.timeInMillis // Save the selected date as a timestamp

            // Show selected date in the TextView
            binding.selectedDateTextView.text = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        }, year, month, day)

        datePickerDialog.show()
    }
}
