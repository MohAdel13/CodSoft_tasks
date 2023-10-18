package com.example.todolist.pojo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.Date

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTask(task: TaskModel)

    @Delete
    fun deleteTask(task: TaskModel)

    @Query("SELECT * FROM TASK_TABLE WHERE LISTNAME = :listName")
    fun getSelectedTasks(listName: String): MutableList<TaskModel>

    @Query("UPDATE Task_Table SET NAME = :newName ," +
            " DEADLINE = :newDeadline, DESCRIPTION = :newDescription WHERE NAME = :oldName")
    fun updateTask(oldName: String, newName: String, newDeadline: String, newDescription: String)

    @Query("UPDATE Task_Table SET COMPLETED = :newCompleted WHERE NAME = :idName")
    fun changeTaskCompleteness(newCompleted: Boolean, idName: String)

    @Query("UPDATE TASK_TABLE SET LISTNAME = :newTaskName WHERE LISTNAME = :oldTaskName")
    fun updateListName(oldTaskName: String, newTaskName: String)

    @Query("DELETE FROM TASK_TABLE WHERE LISTNAME = :idName")
    fun deleteListTasks(idName: String)

    @Query("SELECT * FROM TASK_TABLE WHERE DEADLINE = :todayDate")
    fun getTodayTasks(todayDate: String): MutableList<TaskModel>

    @Query("SELECT * FROM Task_Table WHERE name = :idName")
    fun getTaskByName(idName: String): MutableList<TaskModel>

    @Query("SELECT * FROM TASK_TABLE")
    fun getAllTasks(): MutableList<TaskModel>

    @Query("SELECT * FROM TASK_TABLE WHERE completed = :completed")
    fun getCompletedUncompletedTasks(completed: Boolean): MutableList<TaskModel>
}