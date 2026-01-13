package dev.medguard.app.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.medguard.app.domain.usecase.GetAllActiveSchedulesUseCase

class ScheduleListViewModelFactory(
    private val getAllActiveSchedules: GetAllActiveSchedulesUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleListViewModel::class.java)) {
            return ScheduleListViewModel(
                getAllActiveSchedules = getAllActiveSchedules
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
