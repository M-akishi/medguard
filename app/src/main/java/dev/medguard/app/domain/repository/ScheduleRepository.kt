package dev.medguard.app.domain.repository

import dev.medguard.app.domain.model.Schedule
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ScheduleRepository {

    suspend fun insert(schedule: Schedule)

    fun getByMedicationId(medicationId: UUID): Flow<List<Schedule>>

    suspend fun getAllActive(): List<Schedule>

    suspend fun getById(id: UUID): Schedule?
}
