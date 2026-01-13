package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.DoseStatus
import dev.medguard.app.domain.repository.DoseRepository
import java.time.LocalDateTime
import java.util.UUID

class ConfirmDoseTakenUseCase(
    private val repository: DoseRepository
) {
    suspend operator fun invoke(
        doseId: UUID,
        takenAt: LocalDateTime = LocalDateTime.now()
    ) {
        val dose = repository.getById(doseId) ?: return

        if (dose.status == DoseStatus.TAKEN) return

        val updated = dose.copy(
            status = DoseStatus.TAKEN,
            takenAt = takenAt
        )

        repository.update(updated)
    }
}
