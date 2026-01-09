package dev.medguard.app.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.medguard.app.data.local.room.entity.MedicationEntity
import java.util.UUID

@Dao
interface MedicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medication: MedicationEntity)

    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getById(id: UUID): MedicationEntity?

    @Query("SELECT * FROM medications")
    suspend fun getAll(): List<MedicationEntity>
}
