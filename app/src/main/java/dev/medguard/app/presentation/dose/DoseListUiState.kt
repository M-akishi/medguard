package dev.medguard.app.presentation.dose

import dev.medguard.app.domain.model.Dose
import java.time.LocalDate

data class DoseListUiState(
    val isLoading: Boolean = false,
    val date: LocalDate = LocalDate.now(),
    val doses: List<Dose> = emptyList(),
    val errorMessage: String? = null,
    val confirmingId: java.util.UUID? = null
)
