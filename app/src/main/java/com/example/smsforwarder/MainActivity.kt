package com.example.smsforwarder

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.smsforwarder.ui.theme.SmsForwarderTheme

// Firebase imports
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Initialize Firebase
        FirebaseApp.initializeApp(this)

        // ✅ Request SMS permissions
        val smsPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions.forEach { (name, granted) ->
                if (granted) {
                    println("✅ Permission granted: $name")
                } else {
                    println("❌ Permission denied: $name")
                }
            }
        }

        smsPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_SMS
            )
        )

        enableEdgeToEdge()
        setContent {
            SmsForwarderTheme {
                // 👇 Remember UID state
                var uid by remember { mutableStateOf("Signing in...") }

                // Sign in anonymously
                LaunchedEffect(Unit) {
                    FirebaseAuth.getInstance().signInAnonymously()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = FirebaseAuth.getInstance().currentUser
                                uid = user?.uid ?: "UID not found"
                                println("✅ Signed in anonymously. UID: ${user?.uid}")
                            } else {
                                uid = "Auth failed: ${task.exception?.message}"
                                println("❌ Auth failed: ${task.exception}")
                            }
                        }
                }

                // Display UID in the UI
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(
                        text = "Anonymous UID: $uid",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// Optional composables — can be removed if you don’t need them
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
    SmsForwarderTheme {
        Greeting("Android")
    }
}
