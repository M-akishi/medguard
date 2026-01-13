package dev.medguard.app.presentation.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.medguard.app.domain.usecase.CreateMedicationUseCase
import dev.medguard.app.domain.usecase.GetAllMedicationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicationListViewModel(
    private val getAllMedications: GetAllMedicationsUseCase,
    private val createMedication: CreateMedicationUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicationListUiState())
    val uiState: StateFlow<MedicationListUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val meds = getAllMedications()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    medications = meds,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error loading medications"
                )
            }
        }
    }

    fun onAddMedicationClick() {
        _uiState.value = _uiState.value.copy(
            isAddDialogVisible = true,
            newName = "",
            newNotes = ""
        )
    }

    fun onDismissAddDialog() {
        _uiState.value = _uiState.value.copy(
            isAddDialogVisible = false,
            newName = "",
            newNotes = ""
        )
    }

    fun onNewNameChange(value: String) {
        _uiState.value = _uiState.value.copy(newName = value)
    }

    fun onNewNotesChange(value: String) {
        _uiState.value = _uiState.value.copy(newNotes = value)
    }

    fun onConfirmAddMedication() {
        val current = _uiState.value
        val name = current.newName.trim()
        if (name.isBlank()) {
            // podrías poner un error específico si quieres
            return
        }

        viewModelScope.launch {
            try {
                createMedication(
                    name = name,
                    notes = current.newNotes.trim().ifBlank { null }
                )
                // recargar lista
                refresh()
                // cerrar diálogo
                _uiState.value = _uiState.value.copy(
                    isAddDialogVisible = false,
                    newName = "",
                    newNotes = ""
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error creating medication"
                )
            }
        }
    }
}
