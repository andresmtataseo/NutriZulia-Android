package com.nutrizulia.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nutrizulia.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_APPOINTMENTS_ID = "appointments_reminders"
        const val CHANNEL_APPOINTMENTS_NAME = "Recordatorios de citas"
        const val NOTIFICATION_GROUP_APPOINTMENTS = "group_appointments"
        const val NOTIFICATION_ID_12H = 1001
    }

    fun ensureChannels() {
        val channel = NotificationChannel(
            CHANNEL_APPOINTMENTS_ID,
            CHANNEL_APPOINTMENTS_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notificaciones para recordatorios de citas (12h y 1h antes)"
            setShowBadge(true)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
        }
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun show12hCount(count: Int) {
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return

        val notification = NotificationCompat.Builder(context, CHANNEL_APPOINTMENTS_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Citas pendientes/reprogramadas")
            .setContentText("Tienes $count citas por atender en las próximas horas")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup(NOTIFICATION_GROUP_APPOINTMENTS)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_ID_12H, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun show1hDetails(
        pacienteNombre: String,
        servicioNombre: String?,
        tipoActividadNombre: String?,
        fechaHora: String,
        consultaId: String
    ) {
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return

        val subtitle = buildString {
            append(fechaHora)
            val service = servicioNombre ?: tipoActividadNombre
            if (!service.isNullOrBlank()) {
                append(" · ")
                append(service)
            }
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_APPOINTMENTS_ID)
            .setSmallIcon(R.drawable.ic_schedule)
            .setContentTitle("Cita en 1 hora: $pacienteNombre")
            .setContentText(subtitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setGroup(NOTIFICATION_GROUP_APPOINTMENTS)
            .setAutoCancel(true)
            .build()

        // Usar un ID derivado del hash de la consulta para evitar colisiones
        val notificationId = 2000 + (consultaId.hashCode() and 0x7FFFFFFF)
        manager.notify(notificationId, notification)
    }
}