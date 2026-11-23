package com.example.myapplication

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

class StepRepository private constructor(private  val dao: StepDao){

    companion object {
        @Volatile private var instance: StepRepository? = null

        fun get(context: Context): StepRepository =
            instance ?: synchronized(this) {
                instance ?: StepRepository(AppDatabase.get(context).stepDao()).also { instance = it }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun today(): String = LocalDate.now().toString()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun todaySteps(): Int = dao.getSteps(today()) ?: 0

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveToday(steps: Int) = dao.upsert(Daily_Steps(today(), steps))

}