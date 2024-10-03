package com.example.taskbuddy.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskbuddy.R
import com.example.taskbuddy.view.adapter.TaskAdapter
import com.example.taskbuddy.view.viewmodel.TaskViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog
import android.content.DialogInterface
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.taskbuddy.model.database.Task

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

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        taskViewModel.allTasks.observe(this) { tasks ->
            tasks?.let { taskAdapter.submitList(it) }
        }

        // Set OnClickListener for FAB to open AddTaskActivity
        addTaskButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        // Implement swipe-to-delete functionality
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
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

                // Show confirmation dialog before deleting
                showDeleteConfirmationDialog(task)
            }
        })

        itemTouchHelper.attachToRecyclerView(taskRecyclerView)
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter()
        taskRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
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