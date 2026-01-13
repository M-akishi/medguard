package dev.medguard.app.data.repository
import dev.medguard.app.data.local.room.dao.MedicationDao
import dev.medguard.app.data.local.room.entity.MedicationEntity
import dev.medguard.app.domain.model.Medication
import dev.medguard.app.domain.repository.MedicationRepository
import java.util.UUID


class MedicationRepositoryImpl (
    private val medicationDao: MedicationDao
) : MedicationRepository {
    override suspend fun insert(medication: Medication) {
        medicationDao.insert(medication.toEntity())
    }

    override suspend fun getById(id: UUID): Medication? {
        return medicationDao
            .getById(id)        // MedicationEntity?
            ?.toDomain()
    }

    override suspend fun getAll(): List<Medication> {
        return medicationDao
            .getAll()           // List<MedicationEntity>
            .map { it.toDomain() }
    }

    private fun Medication.toEntity(): MedicationEntity =
        MedicationEntity(
            id = id,
            name = name,
            notes = notes,
            isActive = isActive,
        )

    private fun MedicationEntity.toDomain(): Medication =
        Medication(
            id = id,
            name = name,
            notes = notes,
            isActive = isActive
        )
}