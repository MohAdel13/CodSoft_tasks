package com.example.todolist.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "List_Table")
class ListModel(

    @PrimaryKey(autoGenerate = false)
    var name: String,
    var tasksCount: Int,
    var completedTasks: Int
)