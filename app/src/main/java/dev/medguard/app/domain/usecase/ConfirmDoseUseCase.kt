package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.DoseStatus
import dev.medguard.app.domain.repository.DoseRepository
import java.time.LocalDateTime
import java.util.UUID

class ConfirmDoseUseCase(
    private val doseRepository: DoseRepository
) {

    /**
     * Marca una dosis como tomada solo si la hora actual está dentro
     * del rango [scheduledDateTime, scheduledDateTime + 1h].
     */
    suspend fun execute(doseId: UUID): Result<Unit> {
        val dose = doseRepository.getById(doseId)
            ?: return Result.failure(IllegalArgumentException("Dosis no encontrada"))

        val now = LocalDateTime.now()
        val scheduled = dose.scheduledDateTime
        val maxAllowed = scheduled.plusHours(1)

        // Muy temprano o demasiado tarde
        if (now.isBefore(scheduled) || now.isAfter(maxAllowed)) {
            return Result.failure(
                IllegalStateException(
                    "Fuera del rango permitido para confirmar esta toma"
                )
            )
        }

        // Dentro de la ventana: se marca como tomada
        val updated = dose.copy(
            status = DoseStatus.TAKEN,
            takenAt = now
        )

        doseRepository.update(updated)
        return Result.success(Unit)
    }
}
