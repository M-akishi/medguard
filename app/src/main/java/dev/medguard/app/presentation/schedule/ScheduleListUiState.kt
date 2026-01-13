package dev.medguard.app.presentation.schedule

import dev.medguard.app.domain.model.Schedule

data class ScheduleListUiState(
    val isLoading: Boolean = false,
    val schedules: List<Schedule> = emptyList(),
    val errorMessage: String? = null
)