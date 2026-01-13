package dev.medguard.app.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.medguard.app.data.local.room.entity.ScheduleEntity
import dev.medguard.app.domain.model.Schedule
import java.util.UUID

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: ScheduleEntity)

    @Query("SELECT * FROM schedules WHERE medicationId = :medicationId")
    suspend fun getByMedicationId(medicationId: UUID): List<Schedule>

    @Query("SELECT * FROM schedules WHERE isActive = 1")
    suspend fun getAllActive(): List<Schedule>

    @Query("SELECT * FROM schedules WHERE id = :id")
    suspend fun getById(id: UUID): ScheduleEntity?
}
