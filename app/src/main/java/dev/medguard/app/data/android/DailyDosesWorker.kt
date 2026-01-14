package dev.medguard.app.data.android

import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.medguard.app.data.local.room.MedGuardDatabase
import dev.medguard.app.data.repository.DoseRepositoryImpl
import dev.medguard.app.data.repository.ScheduleRepositoryImpl
import dev.medguard.app.domain.reminder.DoseReminderScheduler
import dev.medguard.app.domain.usecase.GenerateDailyDosesUseCase
import java.time.LocalDate

class DailyDosesWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // 1) DB y repos como en MainActivity
        val db = Room.databaseBuilder(
            applicationContext,
            MedGuardDatabase::class.java,
            "medguard.db"
        ).build()

        val scheduleRepository = ScheduleRepositoryImpl(db.scheduleDao())
        val doseRepository = DoseRepositoryImpl(db.doseDao())
        val scheduler: DoseReminderScheduler = DoseReminderSchedulerImpl(applicationContext)

        val generateDailyDosesUseCase = GenerateDailyDosesUseCase(
            scheduleRepository = scheduleRepository,
            doseRepository = doseRepository,
            reminderScheduler = scheduler
        )

        // 2) Generar dosis de HOY + programar alarmas
        generateDailyDosesUseCase.execute(LocalDate.now())

        return Result.success()
    }
}
