package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.DayOfWeek
import dev.medguard.app.domain.model.Schedule
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID

/**
 * Crea un horario (Schedule) y luego genera las dosis del día
 * para todos los horarios activos (incluyendo el recién creado).
 */
class CreateScheduleAndGenerateDosesUseCase(
    private val createScheduleUseCase: CreateScheduleUseCase,
    private val generateDailyDosesUseCase: GenerateDailyDosesUseCase
) {

    suspend fun execute(
        medicationId: UUID,
        time: LocalTime,
        doseDescription: String,
        activeDays: Set<DayOfWeek>,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Result<Schedule> {
        // 1) Crear el horario
        val result = createScheduleUseCase.execute(
            medicationId = medicationId,
            time = time,
            doseDescription = doseDescription,
            activeDays = activeDays
        )

        if (result.isFailure) return result

        // 2) Generar las dosis de HOY (para todos los schedules activos)
        val today = LocalDate.now(zoneId)
        generateDailyDosesUseCase.execute(
            date = today,
            zoneId = zoneId
        )

        return result
    }
}
