package com.example.taskbuddy.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskbuddy.R
import com.example.taskbuddy.view.adapter.TaskAdapter
import com.example.taskbuddy.view.viewmodel.TaskViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.taskbuddy.model.data.Task

class MainActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var addTaskButton: FloatingActionButton
    private lateinit var taskRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        // Initialize the views
        addTaskButton = findViewById(R.id.addTaskButton)
        taskRecyclerView = findViewById(R.id.taskRecyclerView)

        setupRecyclerView()
        setupSpinner() // Initialize the spinner

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        // Observe changes in the task list
        taskViewModel.allTasks.observe(this) { tasks: List<Task>? ->
            tasks?.let {
                taskAdapter.submitList(it)
            }
        }

        // Set OnClickListener for FAB to open AddTaskActivity
        addTaskButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        // Implement swipe-to-edit and swipe-to-delete functionality
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // We are not supporting move action
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = taskAdapter.getTaskAt(position)

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        // Navigate to EditTaskActivity
                        val intent = Intent(this@MainActivity, EditTaskActivity::class.java)
                        intent.putExtra("taskId", task.id)
                        intent.putExtra("taskName", task.taskName)
                        intent.putExtra("taskDescription", task.taskDescription)
                        intent.putExtra("taskPriority", task.priority.priorityName)
                        intent.putExtra("taskDeadline", task.deadline)
                        startActivity(intent)
                    }
                    ItemTouchHelper.RIGHT -> {
                        // Show confirmation dialog before deleting
                        showDeleteConfirmationDialog(task)
                    }
                }
            }
        })

        itemTouchHelper.attachToRecyclerView(taskRecyclerView)
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter { task -> // Pass clicked task to EditTaskActivity
            val intent = Intent(this, EditTaskActivity::class.java)
            intent.putExtra("taskId", task.id)
            intent.putExtra("taskName", task.taskName)
            intent.putExtra("taskDescription", task.taskDescription)
            intent.putExtra("taskPriority", task.priority.priorityName)
            intent.putExtra("taskDeadline", task.deadline)
            startActivity(intent)
        }
        taskRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }
    }

    private fun setupSpinner() {
        val sortSpinner = findViewById<Spinner>(R.id.sortBySpinner)
        val sortOptions = resources.getStringArray(R.array.sort_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = adapter

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                val selectedOption = sortOptions[position]
                sortTasks(selectedOption)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun sortTasks(selectedOption: String) {
        taskViewModel.allTasks.observe(this) { tasks: List<Task>? ->
            tasks?.let {
                val sortedTasks = when (selectedOption) {
                    "Title A-Z" -> it.sortedBy { task -> task.taskName }
                    "Title Z-A" -> it.sortedByDescending { task -> task.taskName }
                    "Priority Low-High" -> it.sortedBy { task -> task.priority } // Compare Priority directly
                    "Priority High-Low" -> it.sortedByDescending { task -> task.priority } // Compare Priority directly
                    "Nearest Deadline" -> it.sortedBy { task -> task.deadline }
                    "Furthest Deadline" -> it.sortedByDescending { task -> task.deadline }
                    else -> it
                }
                taskAdapter.submitList(sortedTasks)
            }
        }
    }

    private fun showDeleteConfirmationDialog(task: Task) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Are you sure you want to delete this task?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                // Delete task if "Yes" is clicked
                taskViewModel.delete(task)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                taskAdapter.notifyDataSetChanged() // Reset the swipe action
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Delete Task")
        alert.show()
    }
}
