package com.example.todolist.pojo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertList(list:ListModel)

    @Query("SELECT * FROM List_Table")
    fun getAllLists(): MutableList<ListModel>

    @Delete
    fun deleteList(list:ListModel)

    @Query("UPDATE LIST_TABLE SET name = :newName WHERE name = :oldName")
    fun updateList(oldName: String, newName: String)

    @Query("UPDATE List_Table SET tasksCount = tasksCount + 1 WHERE name = :idName")
    fun incrementTasks(idName: String)

    @Query("UPDATE List_Table SET tasksCount = tasksCount - 1 WHERE name = :idName")
    fun decrementTasks(idName: String)

    @Query("UPDATE List_Table SET completedTasks = completedTasks - 1 WHERE name = :idName")
    fun decrementCompletedTasks(idName: String)

    @Query("UPDATE List_Table SET completedTasks = completedTasks + 1 WHERE name = :idName")
    fun incrementCompletedTasks(idName: String)

    @Query("SELECT * FROM LIST_TABLE WHERE name = :idName")
    fun checkDuplicated(idName: String): List<ListModel>
}