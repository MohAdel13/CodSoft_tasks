package com.example.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.todolist.databinding.ActivityEditTaskBinding
import com.example.todolist.databinding.ActivityListsBinding
import com.example.todolist.pojo.ListDB
import com.example.todolist.pojo.TaskDB
import com.example.todolist.pojo.TaskModel
import java.text.SimpleDateFormat
import java.util.Calendar

class EditTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTaskBinding
    private lateinit var taskName: String
    private lateinit var type: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent: Intent = intent
        taskName = intent.getStringExtra("taskName").toString()
        type = intent.getStringExtra("type").toString()

        val taskDB = TaskDB.getDB(binding.root.context)
        val task = taskDB.taskDao().getTaskByName(taskName)[0]

        val (day,month,year) = task.deadline.split("-")

        binding.editDatePicker.init(Integer.valueOf(year)
            ,Integer.valueOf(month) - 1,Integer.valueOf(day),null)
        binding.editTitleET.editText?.setText(task.name)
        binding.editDescriptionET.editText?.setText(task.description)

        binding.editDatePicker.minDate = System.currentTimeMillis() - 1000

        binding.editTaskSaveBTN.setOnClickListener{
            var flag = true
            val name = binding.editTitleET.editText?.text.toString()
            val day = binding.editDatePicker.dayOfMonth
            val month = binding.editDatePicker.month + 1
            val year =  binding.editDatePicker.year
            val sdf = SimpleDateFormat("dd-mm-yyyy")
            val deadlineStr = "$day-$month-$year"
            val deadline = sdf.parse(deadlineStr)
            val calendar = Calendar.getInstance()
            val nowDay = calendar.get(Calendar.DAY_OF_MONTH)
            val nowMonth = calendar.get(Calendar.MONTH) + 1
            val nowYear = calendar.get(Calendar.YEAR)
            val nowDateStr = "$nowDay-$nowMonth-$nowYear"
            val nowDate = sdf.parse(nowDateStr)
            val description = binding.editDescriptionET.editText?.text.toString()

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
                if(duplicated.isNotEmpty() && (name != task.name))
                {
                    Toast.makeText(binding.root.context, "This Name Already Exists!", Toast.LENGTH_SHORT).show()
                }
                else {
                    taskDB.taskDao().updateTask(taskName,name,deadlineStr,description)
                    Toast.makeText(
                        this, "Modified Successfully...",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this, TaskViewActivity::class.java)
                    intent.putExtra("taskName", name)
                    intent.putExtra("type",type)
                    startActivity(intent)
                    finish()
                }
            }
        }

        binding.editTaskBackBTN.setOnClickListener{
            back()
        }

        binding.editTaskMenuBTN.setOnClickListener{
            val popup = PopupMenu(this, binding.editTaskMenuBTN, 0, 0, R.style.PopupMenuStyle)
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
        val intent = Intent(binding.root.context,TaskViewActivity::class.java)
        intent.putExtra("taskName", taskName)
        intent.putExtra("type",type)
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