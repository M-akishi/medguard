package dev.medguard.app.domain.model

import java.util.UUID

/**
 * Represents a medication defined by the user.
 *
 * A Medication is a logical entity that can have multiple Schedules.
 * It does not contain timing or intake history information.
 */
data class Medication(

    /** Unique identifier for the medication */
    val id: UUID = UUID.randomUUID(),

    /** Display name of the medication */
    val name: String,

    /** Optional user notes (e.g. instructions, warnings) */
    val notes: String? = null,

    /** Indicates whether the medication is currently active */
    val isActive: Boolean = true
)
