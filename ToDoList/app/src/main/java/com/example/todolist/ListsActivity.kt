package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityListsBinding
import com.example.todolist.pojo.ListDB
import com.example.todolist.pojo.ListModel


class ListsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityListsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listDB = ListDB.getDB(binding.root.context)
        val lists = listDB.listDao().getAllLists()

        if(lists.isNotEmpty())
        {
            binding.noListsTV.visibility = View.GONE
        }

        val adapter = ListAdapter()
        adapter.setList(lists)

        binding.listsRV.adapter = adapter

        binding.listAddBTN.setOnClickListener{
            val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
            val inflater = layoutInflater

            val dialogLayout = inflater.inflate(R.layout.list_dialog_layout,null)
            val newDialogEditText = dialogLayout.findViewById<EditText>(R.id.newListET)
            builder.setTitle("Please Enter A Name For The List: ")
            builder.setPositiveButton("Save") { _, _ ->
                val newListName = newDialogEditText.text.toString() + " "
                if (newListName != " ") {
                    val duplicated = listDB.listDao().checkDuplicated(newListName)
                    if(duplicated.isNotEmpty())
                    {
                        Toast.makeText(binding.root.context, "This Name Already Exists!",Toast.LENGTH_SHORT).show()
                    }
                    else {
                        val newList = ListModel(newListName, 0, 0)
                        listDB.listDao().insertList(newList)
                        Toast.makeText(
                            binding.root.context,
                            "Created Successfully!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        lists.add(newList)
                        adapter.notifyDataSetChanged()
                        if(binding.noListsTV.visibility == View.VISIBLE) {
                            binding.noListsTV.visibility = View.GONE
                        }
                    }
                } else {
                    Toast.makeText(
                        binding.root.context,
                        "You Must Enter A Name For The List",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            builder.setNegativeButton("Cancel",null)
            builder.setView(dialogLayout)
            builder.show()
        }

        binding.listsBackBTN.setOnClickListener{
            back()
        }

        binding.listsMenuBTN.setOnClickListener{
            val popup = PopupMenu(this, binding.listsMenuBTN, 0, 0, R.style.PopupMenuStyle)
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
        val intent = Intent(binding.root.context,MainActivity::class.java)
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