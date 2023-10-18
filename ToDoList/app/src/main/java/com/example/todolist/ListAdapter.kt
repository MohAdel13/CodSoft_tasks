package com.example.todolist
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.ListCardLayoutBinding
import com.example.todolist.pojo.ListDB
import com.example.todolist.pojo.ListModel
import com.example.todolist.pojo.TaskDB

class ListAdapter: RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    private lateinit var binding: ListCardLayoutBinding
    lateinit var l: MutableList<ListModel>
    lateinit var listDB: ListDB
    lateinit var taskDB: TaskDB

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        binding = ListCardLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        listDB = ListDB.getDB(binding.root.context)
        taskDB = TaskDB.getDB(binding.root.context)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return l.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.holderBinding.nameTV.text = l[position].name
        holder.holderBinding.tasksNumTV.text = l[position].completedTasks.toString() + " / " +
                l[position].tasksCount.toString()
        holder.holderBinding.listPB.min = 0
        holder.holderBinding.listPB.max = l[position].tasksCount
        holder.holderBinding.listPB.progress = l[position].completedTasks
    }

    fun setList(ls: MutableList<ListModel>)
    {
        l = ls
    }

    fun getList(): MutableList<ListModel>
    {
        return l
    }

    inner class ListViewHolder(binding: ListCardLayoutBinding): RecyclerView.ViewHolder(binding.root){
        var holderBinding: ListCardLayoutBinding
        init {
                holderBinding = binding

                holderBinding.deleteBTN.setOnClickListener {
                    taskDB.taskDao().deleteListTasks(l[adapterPosition].name)
                    listDB.listDao().deleteList(l[adapterPosition])
                    Toast.makeText(
                        holderBinding.root.context,
                        "Deleted Successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    l.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
            holderBinding.editBTN.setOnClickListener {
                val builder = AlertDialog.Builder(holderBinding.root.context, R.style.MyDialogTheme)
                val inflater = LayoutInflater.from(holderBinding.root.context)
                val dialogLayout = inflater.inflate(R.layout.list_dialog_layout, null)
                val newDialogEditText = dialogLayout.findViewById<EditText>(R.id.newListET)

                builder.setTitle("Please Enter A New Name For The List: ")
                builder.setPositiveButton("Save") { _, _ ->
                    val newListName = newDialogEditText.text.toString() + " "
                    if (newListName != " ") {
                        val duplicated = listDB.listDao().checkDuplicated(newListName)
                        if(duplicated.isNotEmpty())
                        {
                            Toast.makeText(binding.root.context, "This Name Already Exists!",Toast.LENGTH_SHORT).show()
                        }
                        else {
                            taskDB.taskDao()
                                .updateListName(l[adapterPosition].name, newListName)
                            listDB.listDao().updateList(l[adapterPosition].name, newListName)
                            Toast.makeText(
                                holderBinding.root.context,
                                "Updated Successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            l[adapterPosition].name = newListName
                            notifyItemChanged(adapterPosition)
                        }
                    } else {
                        Toast.makeText(
                            holderBinding.root.context, "You Must Enter A Name For The List",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                builder.setNegativeButton("Cancel", null)
                builder.setView(dialogLayout)
                builder.show()
            }
            holderBinding.listCV.setOnClickListener{
                val intent =
                    Intent(holderBinding.root.context, TasksActivity::class.java)
                intent.putExtra("listName", l[adapterPosition].name)
                holderBinding.root.context.startActivity(intent)
                (holderBinding.root.context as Activity).finish()
            }
        }
    }
}