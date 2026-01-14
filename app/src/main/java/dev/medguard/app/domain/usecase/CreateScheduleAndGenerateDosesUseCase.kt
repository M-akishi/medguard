package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.DayOfWeek
import dev.medguard.app.domain.model.Schedule
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class CreateScheduleAndGenerateDosesUseCase(
    private val createScheduleUseCase: CreateScheduleUseCase,
    private val generateDailyDosesUseCase: GenerateDailyDosesUseCase
) {

    suspend fun execute(
        medicationId: UUID,
        time: LocalTime,
        doseDescription: String,
        activeDays: Set<DayOfWeek>
    ): Result<Schedule> {

        // 1) Crear el horario
        val result = createScheduleUseCase.execute(
            medicationId = medicationId,
            time = time,
            doseDescription = doseDescription,
            activeDays = activeDays
        )

        val createdSchedule = result.getOrElse { return Result.failure(it) }

        // 2) Generar dosis + alarmas SOLO para HOY.
        // El Worker diario se encargará de los días futuros.
        generateDailyDosesUseCase.execute(LocalDate.now())

        return Result.success(createdSchedule)
    }
}
