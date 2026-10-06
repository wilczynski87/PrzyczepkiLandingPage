package com.example.przyczepki_landingpage

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PrzyczepkiFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(MainActivity.RESERVATION_TOPIC)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: getString(R.string.app_name)
        val body = message.notification?.body ?: "Nowa rezerwacja przyczepki"
        val manager = getSystemService(NotificationManager::class.java)
        val channelId = CHANNEL_ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    getString(R.string.reservations_channel),
                    NotificationManager.IMPORTANCE_HIGH,
                ),
            )
        }
        manager.notify(
            message.messageId?.hashCode() ?: body.hashCode(),
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build(),
        )
    }

    companion object {
        private const val CHANNEL_ID = "reservations"
    }
}
