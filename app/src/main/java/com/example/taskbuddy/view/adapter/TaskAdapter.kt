package com.example.taskbuddy.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskbuddy.databinding.TaskItemBinding
import com.example.taskbuddy.model.database.Task
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var taskList = emptyList<Task>()

    class TaskViewHolder(val binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.taskName.text = task.taskName
            binding.taskDescription.text = task.taskDescription
            binding.priorityTextView.text = task.priority.priorityName

            // Convert deadline timestamp to a readable date
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val deadlineDate = sdf.format(Date(task.deadline))
            binding.deadlineTextView.text = deadlineDate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = taskList[position]
        holder.bind(currentTask)
    }

    override fun getItemCount(): Int = taskList.size

    fun submitList(tasks: List<Task>) {
        this.taskList = tasks
        notifyDataSetChanged()
    }

    // Add this method to get the task at a specific position
    fun getTaskAt(position: Int): Task {
        return taskList[position]
    }
}
