package dev.medguard.app.domain.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Represents a single expected intake of a medication.
 *
 * A Dose is generated from a Schedule and represents
 * a concrete moment in time.
 */
data class Dose(

    /** Unique identifier for the dose */
    val id: UUID = UUID.randomUUID(),

    /** Reference to the schedule that generated this dose */
    val scheduleId: UUID,

    /** Reference to the medication */
    val medicationId: UUID,

    /** Scheduled local date and time for this dose */
    val scheduledDateTime: LocalDateTime,

    /** Dose description copied from the Schedule at creation time */
    val doseDescription: String,

    /** Current status of the dose */
    val status: DoseStatus = DoseStatus.PENDING,

    /** Timestamp when the dose was confirmed as taken */
    val takenAt: LocalDateTime? = null,

    /**
     * Timestamp of a late intake recorded after the dose
     * was already marked as missed.
     *
     * This does not change the dose status.
     */
    val lateIntakeAt: LocalDateTime? = null
)
