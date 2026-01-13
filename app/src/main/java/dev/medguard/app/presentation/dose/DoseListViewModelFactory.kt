package dev.medguard.app.presentation.dose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.medguard.app.domain.usecase.ConfirmDoseTakenUseCase
import dev.medguard.app.domain.usecase.GetDosesForDateUseCase

class DoseListViewModelFactory(
    private val getDosesForDate: GetDosesForDateUseCase,
    private val confirmDoseTaken: ConfirmDoseTakenUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoseListViewModel::class.java)) {
            return DoseListViewModel(
                getDosesForDate = getDosesForDate,
                confirmDoseTaken = confirmDoseTaken
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
