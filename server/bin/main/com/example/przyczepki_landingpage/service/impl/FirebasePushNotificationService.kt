package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.modules.FcmConfig
import com.example.przyczepki_landingpage.service.PushNotificationService
import com.example.przyczepki_landingpage.service.ReservationPushCopy
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File

class FirebasePushNotificationService(
    private val config: FcmConfig,
) : PushNotificationService {

    @Volatile
    private var initialized = false

    @Volatile
    private var available = false

    override suspend fun notifyNewReservation(reservation: ReservationDto) {
        withContext(Dispatchers.IO) {
            if (!ensureInit()) return@withContext
            val message = Message.builder()
                .setTopic(config.topic)
                .setNotification(
                    Notification.builder()
                        .setTitle(ReservationPushCopy.TITLE)
                        .setBody(ReservationPushCopy.body(reservation))
                        .build(),
                )
                .putData("type", "new_reservation")
                .putData("reservationId", reservation.id.orEmpty())
                .putData("trailerName", reservation.trailerName.orEmpty())
                .setAndroidConfig(
                    AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .build(),
                )
                .build()
            val messageId = FirebaseMessaging.getInstance().send(message)
            println("FCM: wysłano powiadomienie $messageId")
        }
    }

    private fun ensureInit(): Boolean {
        synchronized(this) {
            if (initialized) return available
            initialized = true
            val credentialsJson = config.serviceAccountJson
            val credentialsFile = config.serviceAccountFile
            val stream = when {
                !credentialsJson.isNullOrBlank() -> ByteArrayInputStream(credentialsJson.toByteArray())
                !credentialsFile.isNullOrBlank() -> File(credentialsFile).takeIf { it.isFile }?.inputStream()
                else -> null
            }
            if (stream == null) {
                println("FCM: brak service account — push wyłączony")
                available = false
                return false
            }
            return try {
                stream.use { input ->
                    val options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(input))
                        .setProjectId(config.projectId)
                        .build()
                    if (FirebaseApp.getApps().isEmpty()) {
                        FirebaseApp.initializeApp(options)
                    }
                }
                available = true
                true
            } catch (e: Exception) {
                println("FCM: nie udało się zainicjować Firebase: ${e.message}")
                available = false
                false
            }
        }
    }
}
