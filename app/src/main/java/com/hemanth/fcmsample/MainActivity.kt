package com.hemanth.fcmsample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.hemanth.fcmsample.ui.theme.FCMSampleTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        askNotificationPermission()
        fetchFcmToken()

        setContent {
            FCMSampleTheme {
                MainLayout(
                    onSubscribeButtonClicked = { subscribeToTopic() },
                    onLogTokenButtonClicked = { fetchFcmToken() }
                )
            }
        }
    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("weather")
            .addOnCompleteListener {
                val msg = if (it.isSuccessful)
                    "Subscribed to weather topic"
                else
                    "Subscription failed"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchFcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d(TAG, "FCM Token: $it")
            Toast.makeText(this, "Token logged", Toast.LENGTH_SHORT).show()
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(
                    this,
                    "Notification permission required for alerts",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @Composable
    fun MainLayout(
        onSubscribeButtonClicked: () -> Unit,
        onLogTokenButtonClicked: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.firebase_lockup_400),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.quickstart_message),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onSubscribeButtonClicked) {
                Text("Subscribe to Weather")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onLogTokenButtonClicked) {
                Text("Log FCM Token")
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
