package com.example.greensignal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.greensignal.presentation.ui.navigation.Screen
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        const val DEFAULT_NOTIFICATION_ID = 0
        const val CHANNEL_ID = "1"
        const val CHANNEL_NAME = "Default"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let { message ->
            sendNotification(message, remoteMessage.data)
        }
    }

    private fun sendNotification(message: RemoteMessage.Notification, data: Map<String, String>) {
        val type = data["Type"]
        val id = data["idReceive"] ?: data["idIncident"] ?: data["idMessage"]

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("myapp://green_signal/" +
                    (when (type) {
                        "IncidentAround" -> Screen.IncidentScreen.route
                        "NewReceiveMessage" -> Screen.MessageScreen.route
                        else -> ""
                    }) +
                    "/${id}")
        )
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_launcher_lighter_round)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)

        manager.notify(DEFAULT_NOTIFICATION_ID, notificationBuilder.build())
    }
}