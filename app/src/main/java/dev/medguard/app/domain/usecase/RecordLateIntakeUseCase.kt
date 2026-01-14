package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.Dose
import dev.medguard.app.domain.model.DoseStatus
import dev.medguard.app.domain.repository.DoseRepository
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID

/**
 * Records a late intake for a dose without changing its status.
 */
class RecordLateIntakeUseCase(
    private val doseRepository: DoseRepository,
    private val clock: Clock
) {

    /**
     * Records a late intake for the given dose.
     *
     * @param doseId Identifier of the dose
     * @return Result containing the updated Dose or a failure
     */
    suspend fun execute(doseId: UUID): Result<Dose> {
        val dose = doseRepository.getById(doseId)
            ?: return Result.failure(IllegalStateException("Dose not found"))

        val now = LocalDateTime.now(clock)

        // Late intake can be recorded only once
        if (dose.lateIntakeAt != null) {
            return Result.failure(
                IllegalStateException("Late intake already recorded for this dose")
            )
        }

        val updatedDose = when (dose.status) {
            DoseStatus.PENDING -> {
                // 👉 Nueva regla: de PENDING pasa a MISSED al registrar tardía
                dose.copy(
                    status = DoseStatus.MISSED,
                    lateIntakeAt = now
                )
            }
            else -> {
                // TAKEN, MISSED u otros estados: no cambiamos el status,
                // solo registramos que se tomó tarde.
                dose.copy(
                    lateIntakeAt = now
                )
            }
        }

        doseRepository.update(updatedDose)

        return Result.success(updatedDose)
    }
}
