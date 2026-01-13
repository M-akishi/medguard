package dev.medguard.app.presentation.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.medguard.app.domain.model.DayOfWeek
import dev.medguard.app.domain.model.Medication
import java.time.LocalTime
import java.util.UUID

@Composable
fun ScheduleListRoute(
    factory: ScheduleListViewModelFactory,
    medications: List<Medication>,
    onScheduleClick: (UUID) -> Unit = {},
    onCreateSchedule: (
        medicationId: UUID,
        time: LocalTime,
        doseDescription: String,
        activeDays: Set<DayOfWeek>,
        isActive: Boolean
    ) -> Unit
) {
    val viewModel: ScheduleListViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    ScheduleListScreen(
        state = uiState,
        onRetry = viewModel::refresh,
        onScheduleClick = onScheduleClick,
        onCreateSchedule = onCreateSchedule,
        medications = medications
    )
}

