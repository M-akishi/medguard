package dev.medguard.app.data.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.medguard.app.MainActivity
import dev.medguard.app.R
import java.util.UUID

class DoseAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("DoseAlarmReceiver", "onReceive llamado 🚨")

        val doseIdStr = intent.getStringExtra("doseId") ?: run {
            Log.w("DoseAlarmReceiver", "Sin doseId en el intent")
            return
        }

        val doseId = runCatching { UUID.fromString(doseIdStr) }.getOrNull()
        if (doseId == null) {
            Log.w("DoseAlarmReceiver", "doseId inválido: $doseIdStr")
            return
        }

        // Intent para abrir la app
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("openDoseId", doseId.toString())
        }

        val pending = android.app.PendingIntent.getActivity(
            context,
            doseId.hashCode(),
            openIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "dose_reminders")
            .setSmallIcon(R.mipmap.ic_launcher) // 👈 usa uno que exista seguro
            .setContentTitle("Hora de tu medicamento")
            .setContentText("Tienes una dosis pendiente.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pending)

        with(NotificationManagerCompat.from(context)) {
            notify(doseId.hashCode(), builder.build())
        }

        Log.d("DoseAlarmReceiver", "Notificación mostrada para doseId=$doseIdStr")
    }
}
