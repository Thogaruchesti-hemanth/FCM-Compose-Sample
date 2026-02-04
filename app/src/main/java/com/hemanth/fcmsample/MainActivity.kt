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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.messaging
import com.hemanth.fcmsample.ui.theme.FCMSampleTheme

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this,
                "FCM can't post notifications without POST_NOTIFICATIONS permission",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    companion object {

        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            FCMSampleTheme {
                MainLayout(
                    onSubscribeButtonClicked = {
                        Log.d(TAG, "Subscribing to weather topic")
                        // [START subscribe_topics]
                        Firebase.messaging.subscribeToTopic("weather")
                            .addOnCompleteListener { task ->
                                var msg = getString(R.string.msg_subscribed)
                                if (!task.isSuccessful) {
                                    msg = getString(R.string.msg_subscribe_failed)
                                }
                                Log.d(TAG, msg)
                                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                            }
                    },
                    onLogTokenButtonClicked = {
                        Firebase.messaging.token.addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.w(
                                        TAG,
                                        "Fetching FCM registration token failed",
                                        task.exception
                                    )
                                    return@OnCompleteListener
                                }

                                // Get new FCM registration token
                                val token = task.result

                                // Log and toast
                                val msg = getString(R.string.msg_token_fmt, token)
                                Log.d(TAG, msg)
                                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                            }
                        )

                    }
                )
            }
        }
        Toast.makeText(this, "See README for setup instructions", Toast.LENGTH_SHORT).show()
        askNotificationPermission()

    }


    @Composable
    fun MainLayout(onSubscribeButtonClicked: () -> Unit, onLogTokenButtonClicked: () -> Unit) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
        ) {
            Image(
                painterResource(R.drawable.firebase_lockup_400),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            Text(
                text = stringResource(R.string.quickstart_message),
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onSubscribeButtonClicked,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .width(200.dp)
            ) {
                Text(stringResource(R.string.subscribe_to_weather))
            }
            Button(onClick = onLogTokenButtonClicked) {
                Text(stringResource(R.string.log_token))
            }
        }
    }

    @Preview(showSystemUi = true)
    @Composable
    fun DefaultPreview() {
        MainLayout(
            onSubscribeButtonClicked = {

            },
            onLogTokenButtonClicked = {

            }
        )
    }
}