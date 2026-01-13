package dev.medguard.app.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.medguard.app.data.local.room.entity.DoseEntity
import dev.medguard.app.domain.model.DoseStatus
import java.time.LocalDateTime
import java.util.UUID

@Dao
interface DoseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dose: DoseEntity)

    @Update
    suspend fun update(dose: DoseEntity)

    @Query("SELECT * FROM doses WHERE id = :id")
    suspend fun getById(id: UUID): DoseEntity?

    @Query(
        """
        SELECT COUNT(*) FROM doses 
        WHERE scheduleId = :scheduleId 
        AND scheduledDateTime = :scheduledDateTime
        """
    )
    suspend fun countForScheduleAt(
        scheduleId: UUID,
        scheduledDateTime: LocalDateTime
    ): Int

    @Query(
        """
        SELECT * FROM doses
        WHERE status = :status
        AND scheduledDateTime < :expirationTime
        """
    )
    suspend fun getPendingDosesBefore(
        status: DoseStatus,
        expirationTime: LocalDateTime
    ): List<DoseEntity>

    @Query("""
    SELECT * FROM doses
    WHERE scheduledDateTime >= :start
      AND scheduledDateTime < :end
""")
    suspend fun getDosesBetween(
        start: LocalDateTime,
        end: LocalDateTime
    ): List<DoseEntity>


    @Query("""
    SELECT * FROM doses
    WHERE status = :status
""")
    suspend fun getByStatus(status: DoseStatus): List<DoseEntity>



}
