package dev.medguard.app.presentation.dose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.medguard.app.domain.usecase.ConfirmDoseUseCase
import dev.medguard.app.domain.usecase.GetDosesForDateUseCase
import dev.medguard.app.domain.usecase.RecordLateIntakeUseCase

class DoseListViewModelFactory(
    private val getDosesForDate: GetDosesForDateUseCase,
    private val confirmDoseTaken: ConfirmDoseUseCase,
    private val recordLateIntake: RecordLateIntakeUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoseListViewModel::class.java)) {
            return DoseListViewModel(
                getDosesForDate = getDosesForDate,
                confirmDoseUseCase = confirmDoseTaken,
                recordLateIntakeUseCase = recordLateIntake
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
