package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.Medication
import dev.medguard.app.domain.repository.MedicationRepository

class GetAllMedicationsUseCase(
    private val medicationRepository: MedicationRepository
) {
    suspend operator fun invoke(): List<Medication> =
        medicationRepository.getAll()
}
