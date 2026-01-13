package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.Dose
import dev.medguard.app.domain.repository.DoseRepository
import java.time.LocalDate

class GetDosesForTodayUseCase(
    private val repository: DoseRepository
) {
    suspend operator fun invoke(): List<Dose> =
        repository.getDosesForDate(LocalDate.now())
}
