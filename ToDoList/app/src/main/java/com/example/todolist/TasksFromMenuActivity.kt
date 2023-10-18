package com.example.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.todolist.databinding.ActivityTasksBinding
import com.example.todolist.databinding.ActivityTasksFromMenuBinding
import com.example.todolist.pojo.TaskDB
import com.example.todolist.pojo.TaskModel
import java.util.Calendar

class TasksFromMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTasksFromMenuBinding
    private lateinit var taskDB: TaskDB
    private lateinit var type : String
    private lateinit var tasks : MutableList<TaskModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityTasksFromMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent: Intent = intent
        type = intent.getStringExtra("type").toString()

        taskDB = TaskDB.getDB(this)

        when(type)
        {
            "all"->{
                tasks = taskDB.taskDao().getAllTasks()
                binding.tasksFromMenuActivityTypeTV.text = "All Tasks"
            }

            "today"->{
                val calendar = Calendar.getInstance()
                val nowDay = calendar.get(Calendar.DAY_OF_MONTH)
                val nowMonth = calendar.get(Calendar.MONTH) + 1
                val nowYear = calendar.get(Calendar.YEAR)
                val nowDateStr = "$nowDay-$nowMonth-$nowYear"
                tasks = taskDB.taskDao().getTodayTasks(nowDateStr)
                binding.tasksFromMenuActivityTypeTV.text = "Today"
            }

            "completed"->{
                tasks = taskDB.taskDao().getCompletedUncompletedTasks(true)
                binding.tasksFromMenuActivityTypeTV.text = "Completed Tasks"
            }

            "uncompleted"->{
                tasks = taskDB.taskDao().getCompletedUncompletedTasks(false)
                binding.tasksFromMenuActivityTypeTV.text = "Uncompleted Tasks"
            }
        }

        if(tasks.isNotEmpty())
        {
            binding.MenuNoTasksTV.visibility = View.GONE
        }

        val adapter = TaskAdapter()
        adapter.setTasks(tasks, type)

        binding.MenuTasksRV.adapter = adapter

        binding.menuTasksBackBTN.setOnClickListener{
            back()
        }

        binding.menuTasksMenuBTN.setOnClickListener{
            val popup = PopupMenu(this, binding.menuTasksMenuBTN, 0, 0, R.style.PopupMenuStyle)
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