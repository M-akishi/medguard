package dev.medguard.app.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.medguard.app.domain.model.DoseStatus
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "doses")
data class DoseEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val medicationId: UUID,
    val scheduleId: UUID,
    val scheduledDateTime: LocalDateTime,
    val doseDescription: String,
    val status: DoseStatus,
    val takenAt: LocalDateTime?,
    val lateIntakeAt: LocalDateTime?
)
