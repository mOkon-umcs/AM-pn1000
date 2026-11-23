package com.example.myapplication

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert

@Entity(tableName = "daily_steps")
data class Daily_Steps(
    @PrimaryKey val date: String,
    val steps: Int
)

@Dao
interface StepDao{
    @Upsert
    suspend fun upsert(entry: Daily_Steps)

    @Query("SELECT steps FROM DAILY_STEPS WHERE date = :date")
    suspend fun getSteps(date: String): Int?
}

@Database(entities = [Daily_Steps::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun stepDao(): StepDao

    companion object{
        @Volatile private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "myapp.db"
                ).build().also { instance = it }
            }
    }
}