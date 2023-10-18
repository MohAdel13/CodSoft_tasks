package com.example.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import com.example.todolist.databinding.ActivityTasksBinding
import com.example.todolist.pojo.TaskDB
import com.example.todolist.pojo.TaskModel

class TasksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTasksBinding
    private lateinit var taskDB: TaskDB
    private lateinit var listName : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent: Intent = intent
        listName = intent.getStringExtra("listName").toString()

        taskDB = TaskDB.getDB(this)

        val tasks: MutableList<TaskModel> = taskDB.taskDao().getSelectedTasks(listName)

        if(tasks.isNotEmpty())
        {
            binding.noTasksTV.visibility = View.GONE
        }

        val adapter = TaskAdapter()
        adapter.setTasks(tasks,"ordinary")

        binding.tasksRV.adapter = adapter

        binding.taskAddBTN.setOnClickListener {
            val intent = Intent(binding.root.context, NewTaskActivity::class.java)
            intent.putExtra("listName", listName)
            startActivity(intent)
            finish()
        }

        binding.tasksBackBTN.setOnClickListener{
            back()
        }

        binding.tasksMenuBTN.setOnClickListener{
            val popup = PopupMenu(this, binding.tasksMenuBTN, 0, 0, R.style.PopupMenuStyle)
            popup.menuInflater.inflate(R.menu.menu_layout, popup.menu)
            popup.setOnMenuItemClickListener {
                    item -> when(item.itemId){
                R.id.tasksForToday ->{
                    menuClick("today")
                    true
                }

                R.id.allTasks ->{
                    menuClick("all")
                    true
                }

                R.id.completedTasks ->{
                    menuClick("completed")
                    true
                }

                R.id.uncompletedTasks ->{
                    menuClick("uncompleted")
                    true
                }

                else -> {
                    false
                }
            }
            }
            popup.show()
        }

        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            back()
        }

    }

    private fun back()
    {
        val intent = Intent(binding.root.context,ListsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun menuClick(type: String)
    {
        val intent = Intent(binding.root.context,TasksFromMenuActivity::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
        finish()
    }
}