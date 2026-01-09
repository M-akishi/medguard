package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.Medication
import dev.medguard.app.domain.repository.MedicationRepository
import java.util.UUID

/**
 * Creates a new medication.
 */
class CreateMedicationUseCase(
    private val medicationRepository: MedicationRepository
) {

    fun execute(
        name: String,
        notes: String? = null
    ): Result<Medication> {

        if (name.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Medication name cannot be empty")
            )
        }

        val medication = Medication(
            id = UUID.randomUUID(),
            name = name.trim(),
            notes = notes
        )

        medicationRepository.insert(medication)

        return Result.success(medication)
    }
}
