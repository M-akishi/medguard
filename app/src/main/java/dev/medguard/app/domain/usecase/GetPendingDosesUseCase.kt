package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.Dose
import dev.medguard.app.domain.repository.DoseRepository

class GetPendingDosesUseCase(
    private val doseRepository: DoseRepository
) {
    suspend operator fun invoke(): List<Dose> =
        doseRepository.getPendingDoses()
}
