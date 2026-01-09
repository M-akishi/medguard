package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.Schedule
import dev.medguard.app.domain.model.DayOfWeek
import dev.medguard.app.domain.repository.ScheduleRepository
import java.time.LocalTime
import java.util.UUID

/**
 * Creates a schedule for an existing medication.
 */
class CreateScheduleUseCase(
    private val scheduleRepository: ScheduleRepository
) {

    suspend fun execute(
        medicationId: UUID,
        time: LocalTime,
        doseDescription: String,
        activeDays: Set<DayOfWeek>
    ): Result<Schedule> {

        if (doseDescription.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Dose description cannot be empty")
            )
        }

        if (activeDays.isEmpty()) {
            return Result.failure(
                IllegalArgumentException("At least one active day must be selected")
            )
        }

        val schedule = Schedule(
            medicationId = medicationId,
            time = time,
            doseDescription = doseDescription.trim(),
            activeDays = activeDays
        )

        scheduleRepository.insert(schedule)

        return Result.success(schedule)
    }
}
