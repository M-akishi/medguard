package dev.medguard.app.presentation.dose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.UUID

@Composable
fun DoseListRoute(
    factory: DoseListViewModelFactory,
    onDoseClick: (UUID) -> Unit = {}
) {
    val viewModel: DoseListViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    DoseListScreen(
        state = uiState,
        onRetry = viewModel::refresh,
        onConfirmDose = viewModel::onConfirmDoseClick,
        onDoseClick = onDoseClick
    )
}
