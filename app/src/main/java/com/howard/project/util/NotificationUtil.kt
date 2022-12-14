package com.howard.project.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.howard.project.R
import com.howard.project.data.FcmRedirectData.NotificationType
import com.howard.project.ui.view.FcmRedirectActivity

object NotificationUtil {

    fun constructLargeIconBitmap(context: Context, resourceId: Int, widthPixels: Int, heightPixels: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, resourceId)
        val mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mutableBitmap)
        drawable?.setBounds(0, 0, widthPixels, heightPixels)
        drawable?.draw(canvas)
        return mutableBitmap
    }

    private fun getNotificationType(notificationType: String?): NotificationType {
        return when (notificationType) {
            NOTIFICATION_TYPE_TEST -> NotificationType.TEST
            else -> NotificationType.UNKNOWN
        }
    }

    private fun getNotificationIntent(context: Context, notificationType: NotificationType?, data: String?): PendingIntent {
        val intent = Intent(context, FcmRedirectActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(BUNDLE_FCM_NOTIFICATION_TYPE, notificationType)
            putExtra(BUNDLE_FCM_DATA, data)
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    fun displayNotification(context: Context, largeIcon: Bitmap?, message: MutableMap<String, String?>) {
        val notificationChannel = NotificationChannel("notification_channel_id", "Soepay Notification", NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.apply {
            this.enableLights(true)
            this.setShowBadge(true)
            this.lightColor = Color.GREEN
        }

        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, "notification_channel_id")
            .setSmallIcon(R.drawable.ic_baseline_done_24)
            .setLargeIcon(largeIcon)
            .setContentTitle(message["title"])
            .setContentText(message["body"])
            .setAutoCancel(true)
            .setOngoing(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(message["title"])
                    .bigText(message["body"])
            )

        val notificationType = getNotificationType(message["type"])
        val pendingIntent = getNotificationIntent(context, notificationType, message["data"])
        mBuilder.setContentIntent(pendingIntent)

        val notificationId = System.currentTimeMillis().toInt()
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.createNotificationChannel(notificationChannel)
        mNotificationManager.notify(notificationId, mBuilder.build())
    }

    fun testNotification(context: Context, testData: String?) {
        val message: MutableMap<String, String?> = mutableMapOf(
            "title" to "testNotification title",
            "body" to "testNotification body",
            "type" to NOTIFICATION_TYPE_TEST,
            "data" to "{\"testDataMessage\": \"$testData\"}",
        )
        val largeIcon = constructLargeIconBitmap(context, R.mipmap.ic_launcher_pug, 256, 256)
        displayNotification(context, largeIcon, message)
    }
}

/** Notification format
{
    "body": "NOTIFICATION body",
    "title": "NOTIFICATION title",
    "type": "NOTIFICATION_TYPE_type",
    "data": {
        "data_field": "data_content"
    }
}
 */