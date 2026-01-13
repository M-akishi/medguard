package dev.medguard.app.domain.repository

import dev.medguard.app.domain.model.Schedule
import java.util.UUID

interface ScheduleRepository {

    suspend fun insert(schedule: Schedule)

    suspend fun getByMedicationId(medicationId: UUID): List<Schedule>

    suspend fun getAllActive(): List<Schedule>

    suspend fun getById(id: UUID): Schedule?
}
