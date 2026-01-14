package dev.medguard.app.data.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.room.Room
import dev.medguard.app.data.android.DoseReminderSchedulerImpl
import dev.medguard.app.data.local.room.MedGuardDatabase
import dev.medguard.app.data.repository.DoseRepositoryImpl
import dev.medguard.app.domain.reminder.DoseReminderScheduler
import java.time.LocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) return

        val appContext = context.applicationContext

        // 1) Creamos la DB (mismo nombre que en MainActivity)
        val db = Room.databaseBuilder(
            appContext,
            MedGuardDatabase::class.java,
            "medguard.db"
        ).build()

        // 2) Repo con el DAO correcto
        val doseRepository = DoseRepositoryImpl(db.doseDao())

        // 3) Scheduler de alarmas
        val scheduler: DoseReminderScheduler = DoseReminderSchedulerImpl(appContext)

        // 4) Reprogramar dosis futuras pendientes
        CoroutineScope(Dispatchers.IO).launch {
            val now = LocalDateTime.now()
            val pendingDoses = doseRepository.getPendingDosesAfter(now)
            pendingDoses.forEach { dose ->
                scheduler.scheduleDoseReminder(dose.id, dose.scheduledDateTime)
            }
        }
    }
}

