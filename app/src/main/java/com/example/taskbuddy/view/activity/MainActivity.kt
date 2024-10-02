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
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter()
        taskRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }
    }
}
