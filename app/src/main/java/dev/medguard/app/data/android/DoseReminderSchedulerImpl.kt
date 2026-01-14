package dev.medguard.app.data.android
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import dev.medguard.app.domain.reminder.DoseReminderScheduler
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

class DoseReminderSchedulerImpl(
    private val context: Context
) : DoseReminderScheduler {

    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleDoseReminder(doseId: UUID, dateTime: LocalDateTime) {
        val triggerAtMillis = dateTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val intent = Intent(context, DoseAlarmReceiver::class.java).apply {
            action = "dev.medguard.app.ACTION_DOSE_REMINDER"
            putExtra("doseId", doseId.toString())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            doseId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d("DoseScheduler", "Programando alarma ${doseId} a millis=$triggerAtMillis")

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    override fun cancelDoseReminder(doseId: UUID) {
        val intent = Intent(context, DoseAlarmReceiver::class.java).apply {
            action = "dev.medguard.app.ACTION_DOSE_REMINDER"
            putExtra("doseId", doseId.toString())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            doseId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}
