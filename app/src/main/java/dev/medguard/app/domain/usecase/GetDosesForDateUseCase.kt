package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.Dose
import dev.medguard.app.domain.repository.DoseRepository
import java.time.LocalDate

class GetDosesForDateUseCase(
    private val doseRepository: DoseRepository
) {
    suspend operator fun invoke(date: LocalDate): List<Dose> =
        doseRepository.getDosesForDate(date)
}
