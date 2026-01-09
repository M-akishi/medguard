package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.Dose
import dev.medguard.app.domain.model.DoseStatus
import dev.medguard.app.domain.repository.DoseRepository
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID

/**
 * Confirms a scheduled dose as taken.
 *
 * This use case enforces all business rules related to dose confirmation.
 * Authentication (biometric or device credential) must be handled by the UI
 * before invoking this use case.
 */
class ConfirmDoseUseCase(
    private val doseRepository: DoseRepository,
    private val clock: Clock
) {

    /**
     * Attempts to confirm the given dose.
     *
     * @param doseId Identifier of the dose to confirm
     * @return Result containing the updated Dose or a failure
     */
    fun execute(doseId: UUID): Result<Dose> {
        val dose = doseRepository.getById(doseId)
            ?: return Result.failure(IllegalStateException("Dose not found"))

        // Dose can only be confirmed if it is still pending
        if (dose.status != DoseStatus.PENDING) {
            return Result.failure(
                IllegalStateException("Dose cannot be confirmed in state ${dose.status}")
            )
        }

        val now = LocalDateTime.now(clock)

        // Dose cannot be confirmed before its scheduled time
        if (now.isBefore(dose.scheduledDateTime)) {
            return Result.failure(
                IllegalStateException("Dose cannot be confirmed before scheduled time")
            )
        }

        val confirmedDose = dose.copy(
            status = DoseStatus.TAKEN,
            takenAt = now
        )

        doseRepository.update(confirmedDose)

        return Result.success(confirmedDose)
    }
}
