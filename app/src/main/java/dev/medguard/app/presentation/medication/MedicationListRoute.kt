package dev.medguard.app.presentation.medication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.UUID

@Composable
fun MedicationListRoute(
    factory: MedicationListViewModelFactory,
    onMedicationClick: (UUID) -> Unit = {}
) {
    val viewModel: MedicationListViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    MedicationListScreen(
        state = uiState,
        onRetry = viewModel::refresh,
        onAddClick = viewModel::onAddMedicationClick,
        onDismissAddDialog = viewModel::onDismissAddDialog,
        onNewNameChange = viewModel::onNewNameChange,
        onNewNotesChange = viewModel::onNewNotesChange,
        onConfirmAddMedication = viewModel::onConfirmAddMedication,
        onMedicationClick = onMedicationClick
    )
}
