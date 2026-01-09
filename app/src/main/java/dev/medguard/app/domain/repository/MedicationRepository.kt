package dev.medguard.app.domain.repository

import dev.medguard.app.domain.model.Medication
import java.util.UUID

interface MedicationRepository {

    fun insert(medication: Medication)

    fun getById(id: UUID): Medication?

    fun getAll(): List<Medication>
}
