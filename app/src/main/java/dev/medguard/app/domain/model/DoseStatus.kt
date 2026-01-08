package dev.medguard.app.domain.model

/**
 * Represents the lifecycle state of a dose.
 *
 * State transitions are one-way:
 * PENDING -> TAKEN or MISSED
 */
enum class DoseStatus {
    PENDING,
    TAKEN,
    MISSED
}
