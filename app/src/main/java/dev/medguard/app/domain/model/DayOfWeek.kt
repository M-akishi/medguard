package dev.medguard.app.domain.model

/**
 * Represents days of the week in a platform-independent way.
 */
enum class DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;
    companion object {
        fun fromJava(day: java.time.DayOfWeek): DayOfWeek = when(day) {
            java.time.DayOfWeek.MONDAY -> MONDAY
            java.time.DayOfWeek.TUESDAY -> TUESDAY
            java.time.DayOfWeek.WEDNESDAY -> WEDNESDAY
            java.time.DayOfWeek.THURSDAY -> THURSDAY
            java.time.DayOfWeek.FRIDAY -> FRIDAY
            java.time.DayOfWeek.SATURDAY -> SATURDAY
            java.time.DayOfWeek.SUNDAY -> SUNDAY
        }
    }


}