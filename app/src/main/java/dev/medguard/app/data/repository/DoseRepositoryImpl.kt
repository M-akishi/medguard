package dev.medguard.app.data.repository

import dev.medguard.app.data.local.room.dao.DoseDao
import dev.medguard.app.data.local.room.entity.DoseEntity
import dev.medguard.app.domain.model.Dose
import dev.medguard.app.domain.model.DoseStatus
import dev.medguard.app.domain.repository.DoseRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class DoseRepositoryImpl(
    private val doseDao: DoseDao
) : DoseRepository {

    override suspend fun insert(dose: Dose) {
        doseDao.insert(dose.toEntity())
    }

    override suspend fun update(dose: Dose) {
        doseDao.update(dose.toEntity())
    }

    override suspend fun getById(id: UUID): Dose? {
        return doseDao.getById(id)?.toDomain()
    }

    override suspend fun existsForSchedule(
        scheduleId: UUID,
        scheduledDateTime: LocalDateTime
    ): Boolean {
        return doseDao.countForScheduleAt(scheduleId, scheduledDateTime) > 0
    }

    override suspend fun getPendingDoses(): List<Dose> {
        return doseDao.getByStatus(DoseStatus.PENDING).map { it.toDomain() }
    }

    override suspend fun getDosesForDate(date: LocalDate): List<Dose> {
        val start = date.atStartOfDay()
        val end = start.plusDays(1)
        return doseDao.getDosesBetween(start, end).map { it.toDomain() }
    }


    override suspend fun getPendingDosesBefore(expirationTime: LocalDateTime): List<Dose> {
        return doseDao
            .getPendingDosesBefore(DoseStatus.PENDING, expirationTime)
            .map { it.toDomain() }
    }

    // Mapping extensions

    private fun Dose.toEntity(): DoseEntity =
        DoseEntity(
            id = id,
            medicationId = medicationId,
            scheduleId = scheduleId,
            scheduledDateTime = scheduledDateTime,
            doseDescription = doseDescription,
            status = status,
            takenAt = takenAt,
            lateIntakeAt = lateIntakeAt
        )

    private fun DoseEntity.toDomain(): Dose =
        Dose(
            id = id,
            medicationId = medicationId,
            scheduleId = scheduleId,
            scheduledDateTime = scheduledDateTime,
            doseDescription = doseDescription,
            status = status,
            takenAt = takenAt,
            lateIntakeAt = lateIntakeAt
        )
}
