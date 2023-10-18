package com.example.todolist.pojo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskModel::class], version = 2, exportSchema = false)
abstract class TaskDB: RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object{
        private var INSTANCE: TaskDB? = null
        fun getDB(context: Context): TaskDB{
            if(INSTANCE == null)
            {
                synchronized(this)
                {
                    INSTANCE = Room.databaseBuilder(context, TaskDB::class.java,"task.db")
                        .allowMainThreadQueries().
                        fallbackToDestructiveMigration().
                        build()
                }
            }
            return INSTANCE!!
        }
    }
}