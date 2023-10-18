package com.example.todolist.pojo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database([ListModel::class], version = 6, exportSchema = false)
abstract class ListDB: RoomDatabase() {
    abstract fun listDao():ListDao

    companion object {
        private var INSTANCE: ListDB? = null
        fun getDB(context: Context): ListDB {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(context,ListDB::class.java, "list.db")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}