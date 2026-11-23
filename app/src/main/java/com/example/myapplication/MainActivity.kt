package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.jar.Manifest

class MainActivity : ComponentActivity() {

    private var notificationsAllowed by mutableStateOf(false)
    private var permanentlyDenied by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationsAllowed = granted
        permanentlyDenied = !granted && !shouldShowRequestPermissionRationale(
            android.Manifest.permission.POST_NOTIFICATIONS)

        if (!granted) {
            Toast.makeText(this,
                "Bez zgody nie pokażemy powiadomień",
                Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun openNotififcationSettings(){
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("Lifecycle", "Mainactivity onCreate")

        StepsNotifications.createChannel(this)

        enableEdgeToEdge()
        setContent {
            var steps by rememberSaveable { mutableStateOf(0) }
            MyApplicationTheme {
                Column {
                    ProfileCard("Jan", "Student")
                    Spacer(Modifier.height(20.dp))

                    Text("A", Modifier.padding(16.dp).background(Color.Yellow))
                    Text("B", Modifier.background(Color.Yellow).padding(16.dp))

                    Spacer(Modifier.height(20.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,

                        ) {
                        Text("Licznik kroków: $steps")
                        Spacer(modifier = Modifier.height(5.dp))
                        Row {
                            Button(onClick = {
                                steps++
                                if(steps == StepsNotifications.GOAL){
                                    StepsNotifications.showGoalReached(this@MainActivity)
                                }
                            }) {
                                Text("Dodaj krok")
                            }
                            Spacer(modifier = Modifier.width(5.dp))
                            Button(onClick = {steps = 0}) {
                                Text("Zeruj")
                            }
                        }
                        Button(onClick = {
                            val intent = Intent(
                                this@MainActivity,
                                HistoryActivity::class.java)
                            intent.putExtra("today_steps", steps)
                            startActivity(intent)
                        }) {
                            Text("Zobacz historię")
                        }
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_SEND)
                                .apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT,
                                        "Dzisiaj zrobiłem $steps kroków")
                                }
                                startActivity(Intent.createChooser(
                                    intent,
                                    "Udostępnij przez"))

                        }){}

                        Button(onClick = {
                            StepService.start(this@MainActivity,steps)
                        }){ Text("Uruchom krokomierz") }
                        Button(onClick = {
                            StepService.stop(this@MainActivity)
                        }) {Text("Zatrzymaj krokomierz") }

                        Spacer(Modifier.height(16.dp))
                        Text(
                            if (notificationsAllowed) "Powiadomienia: włączone"
                            else "Powiadomienia: wyłączone"
                        )
                        if(!notificationsAllowed) {
                            Spacer(Modifier.height(8.dp))
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !permanentlyDenied ) {
                                Button(onClick = {
                                    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                }) { Text("Włącz powiadomienia") }
                            } else {
                                Button(onClick = { openNotififcationSettings() }) {
                                    Text("Otwórz ustawienia powiadomień")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle","Mainactivity onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle","Mainactivity onResume")
    }
    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "Mainactivity onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "Mainactivity onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Lifecycle", "Mainactivity onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "Mainactivity onDestroy")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}
@Composable
fun ProfileCard(name: String, role: String){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all= 16.dp)
            .padding(top = 48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(name.take(1), color = MaterialTheme.colorScheme.onPrimary)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(name, style = MaterialTheme.typography.titleMedium)
            Text(role, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
@Composable
fun HistoryList(days: List<String>){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(days) { day ->
            Card(
                modifier = Modifier.fillMaxSize().padding(4.dp)
            ) { Text(day, modifier = Modifier.padding(16.dp)) }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HistoryListPreview(){
    MyApplicationTheme {
        HistoryList(listOf(
            "Poniedziałek: 5200",
            "Wtorek: 7400",
            "Środa: 3100",
            "Czwartek: 9800"))
    }
}
