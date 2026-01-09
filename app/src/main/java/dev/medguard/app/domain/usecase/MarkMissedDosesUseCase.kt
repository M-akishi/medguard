package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.Dose
import dev.medguard.app.domain.model.DoseStatus
import dev.medguard.app.domain.repository.DoseRepository
import java.time.Clock
import java.time.LocalDateTime
import java.time.Duration

/**
 * Marks pending doses as missed when their confirmation window expires.
 */
class MarkMissedDosesUseCase(
    private val doseRepository: DoseRepository,
    private val clock: Clock,
    private val confirmationWindow: Duration
) {

    /**
     * Executes the missed dose evaluation.
     *
     * @return list of doses that were marked as missed
     */
    suspend fun execute(): List<Dose> {
        val now = LocalDateTime.now(clock)

        val expirationTime = now.minus(confirmationWindow)

        val expiredDoses = doseRepository
            .getPendingDosesBefore(expirationTime)

        val missedDoses = expiredDoses.map { dose ->
            dose.copy(status = DoseStatus.MISSED)
        }

        missedDoses.forEach { doseRepository.update(it) }

        return missedDoses
    }
}
