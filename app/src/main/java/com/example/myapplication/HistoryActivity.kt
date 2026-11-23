package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("Lifecycle", "Historyactivity onCreate")

        val todaySteps = intent.getIntExtra("today_steps",0)

        setContent {
            MyApplicationTheme {
                HistoryScreen(todaySteps)
            }
        }

    }
}

@Composable
fun HistoryScreen(todaySteps: Int){
    val days = listOf("Poniedziałek: 1200", "Wtorek: 7400",
        "Środa: 3010", "Czwartek: 9800")

    Column (modifier = Modifier
        .fillMaxSize()
        .safeDrawingPadding()
        .padding(16.dp)) {

        Text("Dzisiaj: $todaySteps kroków",
            style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(days) { day ->
                Card (modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)) {
                    Text(day, modifier = Modifier.padding(16.dp))
                }
            }
        }

    }
}