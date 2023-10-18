package com.example.todolist.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "Task_Table")
class TaskModel (
    @PrimaryKey(autoGenerate = false)
    var name: String,
    var completed: Boolean,
    var writeDate: String,
    var deadline: String,
    var description: String,
    var listName: String
)