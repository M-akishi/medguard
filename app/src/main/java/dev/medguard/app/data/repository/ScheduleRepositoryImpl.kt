package dev.medguard.app.data.repository
import dev.medguard.app.data.local.room.dao.ScheduleDao
import dev.medguard.app.data.local.room.entity.ScheduleEntity
import dev.medguard.app.domain.model.Schedule
import dev.medguard.app.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ScheduleRepositoryImpl (
    private val scheduleDao: ScheduleDao
) : ScheduleRepository {
    override suspend fun insert(schedule: Schedule) {
        scheduleDao.insert(schedule.toEntity())
    }

    override fun getByMedicationId(medicationId: UUID): Flow<List<Schedule>> {
        return scheduleDao
            .getByMedicationId(medicationId) // Flow<List<ScheduleEntity>>
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getAllActive(): List<Schedule> {
        return scheduleDao.getAllActive()
    }

    override suspend fun getById(id: UUID): Schedule? {
        return scheduleDao.getById(id)?.toDomain()
    }

    private fun Schedule.toEntity(): ScheduleEntity =
        ScheduleEntity(
            id = id,
            medicationId = medicationId,
            time = time,
            doseDescription = doseDescription,
            activeDays = activeDays,
            isActive = isActive
        )
    private fun ScheduleEntity.toDomain(): Schedule =
        Schedule(
            id = id,
            medicationId = medicationId,
            time = time,
            doseDescription = doseDescription,
            activeDays = activeDays,
            isActive = isActive
        )
}
