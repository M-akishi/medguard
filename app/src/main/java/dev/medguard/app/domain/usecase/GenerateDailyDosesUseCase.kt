package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.Dose
import dev.medguard.app.domain.model.DoseStatus
import dev.medguard.app.domain.reminder.DoseReminderScheduler
import dev.medguard.app.domain.repository.DoseRepository
import dev.medguard.app.domain.repository.ScheduleRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

/**
 * Generates daily doses based on active schedules.
 */
class GenerateDailyDosesUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val doseRepository: DoseRepository,
    private val reminderScheduler: DoseReminderScheduler
) {

    suspend fun execute(
        date: LocalDate,
        zoneId: ZoneId = ZoneId.systemDefault()
    ) {
        val schedules = scheduleRepository.getAllActive()
        val now = LocalDateTime.now(zoneId)

        schedules.forEach { schedule ->
            // Solo si el schedule está activo este día
            if (!schedule.isActiveOn(date)) return@forEach

            // Fecha y hora programada para este día
            val scheduledDateTime = schedule.getScheduledDateTime(date, zoneId)
            val scheduledLocalDateTime = scheduledDateTime.toLocalDateTime()

            // Evitar duplicar dosis
            val alreadyExists = doseRepository.existsForSchedule(
                scheduleId = schedule.id,
                scheduledDateTime = scheduledLocalDateTime
            )
            if (alreadyExists) return@forEach

            // Crear la dosis
            val dose = Dose(
                id = UUID.randomUUID(),
                medicationId = schedule.medicationId,
                scheduleId = schedule.id,
                scheduledDateTime = scheduledLocalDateTime,
                doseDescription = schedule.doseDescription,
                status = DoseStatus.PENDING
            )

            // Guardar en BD
            doseRepository.insert(dose)

            // 🔔 Programar alarma solo si es futuro
            if (scheduledLocalDateTime.isAfter(now)) {
                reminderScheduler.scheduleDoseReminder(
                    doseId = dose.id,
                    dateTime = scheduledLocalDateTime
                )
            }
        }
    }
}
