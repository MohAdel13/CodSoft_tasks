package com.example.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import com.example.todolist.databinding.ActivityTaskViewBinding
import com.example.todolist.pojo.TaskDB
import com.example.todolist.pojo.TaskModel
import kotlin.system.exitProcess

class TaskViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskViewBinding
    private lateinit var type: String
    private lateinit var taskName: String
    private lateinit var taskDB: TaskDB
    private lateinit var task: TaskModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityTaskViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent: Intent = intent
        taskName = intent.getStringExtra("taskName").toString()
        type = intent.getStringExtra("type").toString()

        taskDB = TaskDB.getDB(this)
        task = taskDB.taskDao().getTaskByName(taskName)[0]

        binding.taskTitleTV.text = task.name
        binding.taskStartDateTV.text = task.writeDate
        binding.taskDeadlineTV.text = task.deadline
        binding.taskDescriptionTV.text = task.description

        binding.editTaskBTN.setOnClickListener{
            val intent = Intent(this, EditTaskActivity::class.java)
            intent.putExtra("taskName", task.name)
            intent.putExtra("type", type)
            startActivity(intent)
            finish()
        }

        binding.taskViewBackBTN.setOnClickListener{
            back()
        }

        binding.taskViewMenuBTN.setOnClickListener{
            val popup = PopupMenu(this, binding.taskViewMenuBTN, 0, 0, R.style.PopupMenuStyle)
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
        if(type == "ordinary") {
            val intent = Intent(binding.root.context, TasksActivity::class.java)
            val listName = task.listName
            intent.putExtra("listName", listName)
            startActivity(intent)
            finish()
        }
        else
        {
            val intent = Intent(binding.root.context, TasksFromMenuActivity::class.java)
            intent.putExtra("type", type)
            startActivity(intent)
            finish()
        }
    }

    private fun menuClick(type: String)
    {
        val intent = Intent(binding.root.context,TasksFromMenuActivity::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
        finish()
    }

}