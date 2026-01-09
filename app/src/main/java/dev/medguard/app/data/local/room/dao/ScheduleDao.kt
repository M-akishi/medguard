package dev.medguard.app.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.medguard.app.data.local.room.entity.ScheduleEntity
import java.util.UUID

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: ScheduleEntity)

    @Query("SELECT * FROM schedules WHERE medicationId = :medicationId")
    suspend fun getByMedicationId(medicationId: UUID): List<ScheduleEntity>

    @Query("SELECT * FROM schedules WHERE isActive = 1")
    suspend fun getAllActive(): List<ScheduleEntity>
}
