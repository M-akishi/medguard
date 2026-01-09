package dev.medguard.app.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.medguard.app.data.local.room.dao.DoseDao
import dev.medguard.app.data.local.room.dao.MedicationDao
import dev.medguard.app.data.local.room.dao.ScheduleDao
import dev.medguard.app.data.local.room.entity.DoseEntity
import dev.medguard.app.data.local.room.entity.MedicationEntity
import dev.medguard.app.data.local.room.entity.ScheduleEntity

@Database(
    entities = [
        MedicationEntity::class,
        ScheduleEntity::class,
        DoseEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class MedGuardDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun doseDao(): DoseDao
}
