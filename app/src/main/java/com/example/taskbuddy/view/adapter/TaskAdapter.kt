package com.example.taskbuddy.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskbuddy.databinding.TaskItemBinding
import com.example.taskbuddy.model.data.Task
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(private val onItemClicked: (Task) -> Unit) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var taskList = emptyList<Task>()

    class TaskViewHolder(val binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task, onItemClicked: (Task) -> Unit) {
            binding.taskName.text = task.taskName
            binding.taskDescription.text = task.taskDescription
            binding.priorityTextView.text = task.priority.priorityName

            // Convert deadline timestamp to a readable date
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val deadlineDate = sdf.format(Date(task.deadline))
            binding.deadlineTextView.text = deadlineDate

            // Handle click on task item
            binding.root.setOnClickListener {
                onItemClicked(task)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = taskList[position]
        holder.bind(currentTask, onItemClicked)
    }

    override fun getItemCount(): Int = taskList.size

    fun submitList(tasks: List<Task>) {
        this.taskList = tasks
        notifyDataSetChanged()
    }

    fun getTaskAt(position: Int): Task {
        return taskList[position]
    }
}
