package dev.medguard.app.domain.repository

import dev.medguard.app.domain.model.Dose
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

interface DoseRepository {

    fun insert(dose: Dose)

    fun update(dose: Dose)

    fun getById(id: UUID): Dose?

    fun existsForSchedule(
        scheduleId: UUID,
        scheduledDateTime: LocalDateTime
    ): Boolean

    fun getPendingDoses(): List<Dose>

    fun getDosesForDate(date: LocalDate): List<Dose>
    fun getPendingDosesBefore(expirationTime: LocalDateTime): List<Dose>
}
