package dev.medguard.app.domain.repository

import dev.medguard.app.domain.model.Dose
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

interface DoseRepository {

    suspend fun insert(dose: Dose)

    suspend fun update(dose: Dose)

    suspend fun getById(id: UUID): Dose?

    suspend fun existsForSchedule(
        scheduleId: UUID,
        scheduledDateTime: LocalDateTime
    ): Boolean

    suspend fun getPendingDoses(): List<Dose>

    suspend fun getDosesForDate(date: LocalDate): List<Dose>

    suspend fun getPendingDosesBefore(expirationTime: LocalDateTime): List<Dose>
    suspend fun getPendingDosesAfter(from: LocalDateTime): List<Dose>

}

