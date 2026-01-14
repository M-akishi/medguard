package dev.medguard.app.presentation.dose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.medguard.app.domain.model.Medication
import java.util.UUID

@Composable
fun DoseListRoute(
    factory: DoseListViewModelFactory,
    medications: List<Medication>,
    onDoseClick: (UUID) -> Unit = {},
    onRecordLateIntake: (UUID) -> Unit = {}
) {
    val viewModel: DoseListViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    DoseListScreen(
        state = uiState,
        onRetry = viewModel::refresh,
        onDoseClick = onDoseClick,
        medications = medications
    )
}
