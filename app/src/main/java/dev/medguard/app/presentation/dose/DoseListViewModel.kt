package dev.medguard.app.presentation.dose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.medguard.app.domain.usecase.ConfirmDoseTakenUseCase
import dev.medguard.app.domain.usecase.GetDosesForDateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

class DoseListViewModel(
    private val getDosesForDate: GetDosesForDateUseCase,
    private val confirmDoseTaken: ConfirmDoseTakenUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoseListUiState())
    val uiState: StateFlow<DoseListUiState> = _uiState.asStateFlow()

    init {
        refreshForDate(LocalDate.now())
    }

    fun refresh() {
        refreshForDate(_uiState.value.date)
    }

    fun refreshForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                date = date
            )
            try {
                val doses = getDosesForDate(date)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    doses = doses,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error cargando tomas"
                )
            }
        }
    }

    fun onConfirmDoseClick(doseId: UUID) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(confirmingId = doseId)
            try {
                confirmDoseTaken(doseId)
                refresh() // recarga la lista
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error confirmando toma"
                )
            } finally {
                _uiState.value = _uiState.value.copy(confirmingId = null)
            }
        }
    }
}
