package dev.medguard.app.domain.repository

import dev.medguard.app.domain.model.Medication
import java.util.UUID

interface MedicationRepository {

    suspend fun insert(medication: Medication)

    suspend fun getById(id: UUID): Medication?

    suspend fun getAll(): List<Medication>
}
