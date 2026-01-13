package dev.medguard.app.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime
import java.util.UUID

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val medicationId: UUID,
    val time: LocalTime,
    val doseDescription: String,
    val activeDays: Set<dev.medguard.app.domain.model.DayOfWeek>,
    val isActive: Boolean
)

