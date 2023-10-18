package com.example.todolist

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityNewTaskBinding
import com.example.todolist.pojo.ListDB
import com.example.todolist.pojo.TaskDB
import com.example.todolist.pojo.TaskModel
import java.text.SimpleDateFormat
import java.util.Calendar


class NewTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewTaskBinding
    private lateinit var taskDB: TaskDB
    private lateinit var listDB: ListDB
    private lateinit var listName: String

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityNewTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent: Intent = intent
        listName = intent.getStringExtra("listName").toString()

        taskDB = TaskDB.getDB(this)
        listDB = ListDB.getDB(this)

        binding.datePicker.minDate = System.currentTimeMillis() - 1000

        binding.newTaskSaveBTN.setOnClickListener{
            var flag = true
            val name = binding.titleET.editText?.text.toString()
            val day = binding.datePicker.dayOfMonth
            val month = binding.datePicker.month + 1
            val year =  binding.datePicker.year
            val sdf = SimpleDateFormat("dd-mm-yyyy")
            val deadlineStr = "$day-$month-$year"
            val deadline = sdf.parse(deadlineStr)
            val calendar = Calendar.getInstance()
            val nowDay = calendar.get(Calendar.DAY_OF_MONTH)
            val nowMonth = calendar.get(Calendar.MONTH) + 1
            val nowYear = calendar.get(Calendar.YEAR)
            val nowDateStr = "$nowDay-$nowMonth-$nowYear"
            val nowDate = sdf.parse(nowDateStr)
            val description = binding.descriptionET.editText?.text.toString()

            if(name=="")
            {
                Toast.makeText(this,"You Must Enter A Title For The Task...",
                    Toast.LENGTH_SHORT).show()
                flag = false
            }
            if(flag)
            {
                if(deadline < nowDate)
                {
                    Toast.makeText(this,"Please Choose A Valid Date...",
                        Toast.LENGTH_SHORT).show()
                    flag = false
                }
            }
            if(flag)
            {
                val duplicated = taskDB.taskDao().getTaskByName(name)
                if(duplicated.isNotEmpty())
                {
                    Toast.makeText(binding.root.context, "This Name Already Exists!",Toast.LENGTH_SHORT).show()
                }
                else {
                    val task = TaskModel(name,false,nowDateStr,deadlineStr,description,listName)

                    taskDB.taskDao().insertTask(task)
                    listDB.listDao().incrementTasks(listName)
                    Toast.makeText(
                        this, "Created Successfully...",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, TasksActivity::class.java)
                    intent.putExtra("listName", task.listName)
                    startActivity(intent)
                    finish()
                }
            }
        }

        binding.newTaskBackBTN.setOnClickListener{
            back()
        }

        binding.newTaskMenuBTN.setOnClickListener{
            val popup = PopupMenu(this, binding.newTaskMenuBTN, 0, 0, R.style.PopupMenuStyle)
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
        val intent = Intent(binding.root.context,TasksActivity::class.java)
        intent.putExtra("listName",listName)
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