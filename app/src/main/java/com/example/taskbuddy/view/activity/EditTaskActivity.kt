package com.example.taskbuddy.view.activity

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.taskbuddy.R
import com.example.taskbuddy.model.data.Task
import com.example.taskbuddy.model.data.Priority
import com.example.taskbuddy.view.viewmodel.TaskViewModel
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditTaskActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel
    private var taskId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_task)

        // Get task details from intent
        taskId = intent.getIntExtra("taskId", -1)
        val taskName = intent.getStringExtra("taskName")
        val taskDescription = intent.getStringExtra("taskDescription")
        val taskPriorityString = intent.getStringExtra("taskPriority")
        val taskPriority = try {
            Priority.valueOf(taskPriorityString?.toUpperCase() ?: "LOW") // Use uppercase to match enum
        } catch (e: IllegalArgumentException) {
            Priority.LOW // Fallback to LOW if the priority is not valid
        }
        val taskDeadline = intent.getLongExtra("taskDeadline", -1)

        // Set the task details in the form
        findViewById<TextInputEditText>(R.id.titleEditText).setText(taskName)
        findViewById<TextInputEditText>(R.id.descriptionEditText).setText(taskDescription)

        // Set the priority
        setPriorityRadioButton(taskPriority)

        // Set the deadline
        if (taskDeadline != -1L) { // Check if deadline is valid
            findViewById<TextView>(R.id.selectedDateTextView).text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                Date(taskDeadline)
            )
        }

        // Save the changes when the user clicks the save button
        findViewById<Button>(R.id.saveTaskButton).setOnClickListener {
            saveEditedTask()
        }

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
    }

    private fun setPriorityRadioButton(priority: Priority?) {
        val radioGroup = findViewById<RadioGroup>(R.id.priorityRadioGroup)
        when (priority) {
            Priority.LOW -> radioGroup.check(R.id.lowPriorityRadioButton)
            Priority.MEDIUM -> radioGroup.check(R.id.mediumPriorityRadioButton)
            Priority.HIGH -> radioGroup.check(R.id.highPriorityRadioButton)
            null -> radioGroup.clearCheck() // Handle null case if priority is not set
        }
    }

    private fun saveEditedTask() {
        val taskName = findViewById<TextInputEditText>(R.id.titleEditText).text.toString()
        val taskDescription = findViewById<TextInputEditText>(R.id.descriptionEditText).text.toString()
        val taskPriority = when (findViewById<RadioGroup>(R.id.priorityRadioGroup).checkedRadioButtonId) {
            R.id.lowPriorityRadioButton -> Priority.LOW
            R.id.mediumPriorityRadioButton -> Priority.MEDIUM
            else -> Priority.HIGH // Default to HIGH if none selected
        }
        val taskDeadline = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .parse(findViewById<TextView>(R.id.selectedDateTextView).text.toString())?.time ?: 0

        val updatedTask = Task(taskId!!, taskName, taskDescription, taskPriority, taskDeadline)

        taskViewModel.update(updatedTask)
        finish() // Go back to MainActivity
    }
}
