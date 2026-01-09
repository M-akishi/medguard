package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.Dose
import dev.medguard.app.domain.model.DoseStatus
import dev.medguard.app.domain.model.Schedule
import dev.medguard.app.domain.repository.DoseRepository
import dev.medguard.app.domain.repository.ScheduleRepository
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

/**
 * Generates daily doses based on active schedules.
 */
class GenerateDailyDosesUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val doseRepository: DoseRepository
) {

    suspend fun execute(
        date: LocalDate,
        zoneId: ZoneId = ZoneId.systemDefault()
    ) {

        val schedules = scheduleRepository.getAllActive()

        schedules.forEach { schedule ->
            if (!schedule.isActiveOn(date)) return@forEach

            val scheduledDateTime = schedule.getScheduledDateTime(date, zoneId)

            val scheduledLocalDateTime = scheduledDateTime.toLocalDateTime()

            // Prevent duplicate dose generation
            val alreadyExists = doseRepository.existsForSchedule(
                scheduleId = schedule.id,
                scheduledDateTime = scheduledLocalDateTime
            )

            if (alreadyExists) return@forEach

            val dose = Dose(
                id = UUID.randomUUID(),
                medicationId = schedule.medicationId,
                scheduleId = schedule.id,
                scheduledDateTime = scheduledDateTime.toLocalDateTime(),
                doseDescription = schedule.doseDescription,
                status = DoseStatus.PENDING
            )

            doseRepository.insert(dose)
        }
    }
}
