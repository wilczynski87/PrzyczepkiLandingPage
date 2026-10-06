package com.example.przyczepki_landingpage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { subscribeToReservations() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val text = TextView(this).apply {
            text = getString(R.string.push_ready)
            textSize = 18f
            setPadding(48, 96, 48, 48)
        }
        setContentView(text)
        requestNotificationPermission()
        subscribeToReservations()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun subscribeToReservations() {
        FirebaseMessaging.getInstance()
            .subscribeToTopic(RESERVATION_TOPIC)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    android.util.Log.w(TAG, "Nie udało się zasubskrybować tematu FCM", task.exception)
                }
            }
    }

    companion object {
        const val RESERVATION_TOPIC = "admin_reservations"
        private const val TAG = "PrzyczepkiPush"
    }
}
