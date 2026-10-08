package com.example.roomapp.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "task_database"
            )
            .fallbackToDestructiveMigration(true)
            .build()
            .also { createdDatabase ->
                instance = createdDatabase
            }
        }
    }
}
