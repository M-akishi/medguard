package dev.medguard.app.domain.model

import java.time.LocalTime
import java.util.UUID

/**
 * Defines when and how a medication must be taken.
 *
 * A Schedule is a recurring rule that generates one or more Doses.
 * It does not track intake history.
 */

data class Schedule(
    val id: UUID = UUID.randomUUID(),

    /** Reference to the medication this schedule belongs to */
    val medicationId: UUID,

    /** Fixed local time when the dose should be taken */
    val time: LocalTime,

    /** Human-readable dose description (e.g. "1 tablet", "500 mg") */
    val doseDescription: String,

    /** Days of the week when this schedule is active */
    val activeDays: Set<DayOfWeek>,

    /** Whether this schedule is currently active */
    val isActive: Boolean = true
)