package dev.medguard.app.domain.repository

import dev.medguard.app.domain.model.Schedule
import java.util.UUID

interface ScheduleRepository {

    fun insert(schedule: Schedule)

    fun getByMedicationId(medicationId: UUID): List<Schedule>

    fun getAllActive(): List<Schedule>
}
