package dev.medguard.app.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey
    val id: UUID,
    val name: String,
    val notes: String?,
    val isActive: Boolean
)
