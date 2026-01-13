package dev.medguard.app.data.local.room

import androidx.room.TypeConverter
import dev.medguard.app.domain.model.DoseStatus
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

class RoomConverters {

    // UUID as String
    @TypeConverter
    fun fromUuid(value: java.util.UUID?): String? = value?.toString()

    @TypeConverter
    fun toUuid(value: String?): java.util.UUID? =
        value?.let { java.util.UUID.fromString(it) }

    // LocalDateTime as ISO String
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? =
        value?.toString() // ISO-8601 by default

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? =
        value?.let { LocalDateTime.parse(it) }

    // LocalTime as ISO String
    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? =
        value?.toString()

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? =
        value?.let { LocalTime.parse(it) }

    // DayOfWeek as Int (1-7)
    @TypeConverter
    fun fromDayOfWeek(value: DayOfWeek?): Int? =
        value?.value

    @TypeConverter
    fun toDayOfWeek(value: Int?): DayOfWeek? =
        value?.let { DayOfWeek.of(it) }

    // Set<DayOfWeek> as comma-separated ints, e.g. "1,2,3"
    @TypeConverter
    fun fromDayOfWeekSet(days: Set<DayOfWeek>?): String? =
        days?.joinToString(",") { it.value.toString() }

    @TypeConverter
    fun toDayOfWeekSet(value: String?): Set<DayOfWeek>? =
        value
            ?.takeIf { it.isNotBlank() }
            ?.split(",")
            ?.map { DayOfWeek.of(it.toInt()) }
            ?.toSet()

    // DoseStatus as String
    @TypeConverter
    fun fromDoseStatus(status: DoseStatus?): String? =
        status?.name

    @TypeConverter
    fun toDoseStatus(value: String?): DoseStatus? =
        value?.let { DoseStatus.valueOf(it) }

    @TypeConverter
    fun fromDomainDayOfWeek(value: dev.medguard.app.domain.model.DayOfWeek?): String? =
        value?.name

    @TypeConverter
    fun toDomainDayOfWeek(value: String?): dev.medguard.app.domain.model.DayOfWeek? =
        value?.let { dev.medguard.app.domain.model.DayOfWeek.valueOf(it) }

    @TypeConverter
    fun fromDomainDayOfWeekSet(days: Set<dev.medguard.app.domain.model.DayOfWeek>?): String? =
        days?.joinToString(",") { it.name }

    @TypeConverter
    fun toDomainDayOfWeekSet(value: String?): Set<dev.medguard.app.domain.model.DayOfWeek>? =
        value
            ?.takeIf { it.isNotBlank() }
            ?.split(",")
            ?.map { dev.medguard.app.domain.model.DayOfWeek.valueOf(it) }
            ?.toSet()

}
