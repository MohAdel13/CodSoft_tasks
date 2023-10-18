package com.example.todolist

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.TaskCardLayoutBinding
import com.example.todolist.pojo.ListDB
import com.example.todolist.pojo.TaskDB
import com.example.todolist.pojo.TaskModel

class TaskAdapter: RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private lateinit var binding: TaskCardLayoutBinding
    private lateinit var tasksList: MutableList<TaskModel>
    private lateinit var listDB: ListDB
    private lateinit var taskDB: TaskDB
    private lateinit var type: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        binding = TaskCardLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        listDB = ListDB.getDB(binding.root.context)
        taskDB = TaskDB.getDB(binding.root.context)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tasksList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.holderBinding.completedCB.isChecked = tasksList[position].completed
        holder.holderBinding.taskNameTV.text = tasksList[position].name
        holder.holderBinding.deadlineTV.text = tasksList[position].deadline
    }

    fun setTasks(tasks: MutableList<TaskModel>, type: String){
        tasksList = tasks
        this.type = type
    }

    inner class TaskViewHolder(binding: TaskCardLayoutBinding): RecyclerView.ViewHolder(binding.root){
        var holderBinding: TaskCardLayoutBinding
        init
        {
            holderBinding = binding

            holderBinding.taskDeleteBTN.setOnClickListener{
                listDB.listDao().decrementTasks(tasksList[adapterPosition].listName)
                if(tasksList[adapterPosition].completed)
                {
                    listDB.listDao().decrementCompletedTasks(tasksList[adapterPosition].listName)
                }
                taskDB.taskDao().deleteTask(tasksList[adapterPosition])
                Toast.makeText(
                    holderBinding.root.context,
                    "Deleted Successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                tasksList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)

            }

            holderBinding.completedCB.setOnClickListener{
                taskDB.taskDao().changeTaskCompleteness(holderBinding.completedCB.isChecked,tasksList[adapterPosition].name)
                if(tasksList[adapterPosition].completed)
                {
                    listDB.listDao().decrementCompletedTasks(tasksList[adapterPosition].listName)
                }
                else{
                    listDB.listDao().incrementCompletedTasks(tasksList[adapterPosition].listName)
                }
                tasksList[adapterPosition].completed = !tasksList[adapterPosition].completed
            }

            holderBinding.taskCV.setOnClickListener{
                val intent = Intent(holderBinding.root.context,TaskViewActivity::class.java)
                intent.putExtra("taskName", tasksList[adapterPosition].name)
                intent.putExtra("type", type)
                holderBinding.root.context.startActivity(intent)
                (holderBinding.root.context as Activity).finish()
            }
        }
    }
}