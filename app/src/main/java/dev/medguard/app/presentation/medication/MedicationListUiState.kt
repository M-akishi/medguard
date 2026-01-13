package dev.medguard.app.presentation.medication

import dev.medguard.app.domain.model.Medication

data class MedicationListUiState(
    val isLoading: Boolean = false,
    val medications: List<Medication> = emptyList(),
    val errorMessage: String? = null,
    val isAddDialogVisible: Boolean = false,
    val newName: String = "",
    val newNotes: String = "",
)
