package dev.medguard.app.presentation.dose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.medguard.app.domain.usecase.ConfirmDoseUseCase
import dev.medguard.app.domain.usecase.GetDosesForDateUseCase
import dev.medguard.app.domain.usecase.MarkMissedDosesUseCase
import dev.medguard.app.domain.usecase.RecordLateIntakeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

class DoseListViewModel(
    private val getDosesForDate: GetDosesForDateUseCase,
    private val confirmDoseUseCase: ConfirmDoseUseCase,
    private val recordLateIntakeUseCase: RecordLateIntakeUseCase,
    private val markMissedDosesUseCase: MarkMissedDosesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoseListUiState())
    val uiState: StateFlow<DoseListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            markMissedDosesUseCase.execute()
            refreshForDate(LocalDate.now())
        }
    }

    fun onScreenOpened() {
        viewModelScope.launch {
            // 1) Marcar como MISSED las PENDING cuyo tiempo ya expiró
            markMissedDosesUseCase.execute()
            // 2) Refrescar lista para la fecha actual
            refresh()
        }
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

    fun confirmDose(doseId: UUID) {
        viewModelScope.launch {
            // marcar que estamos confirmando esta dosis
            _uiState.update { it.copy(confirmingId = doseId, errorMessage = null) }

            val result = confirmDoseUseCase.execute(doseId)

            result
                .onSuccess {
                    // recargar las tomas (para actualizar status TAKEN)
                    refresh()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            confirmingId = null,
                            errorMessage = error.message
                                ?: "No se pudo confirmar la toma"
                        )
                    }
                }
            }
        }

    fun recordLateIntake(doseId: UUID) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    confirmingId = doseId,
                    errorMessage = null
                )
            }

            recordLateIntakeUseCase.execute(doseId)
                .onSuccess { updatedDose ->
                    _uiState.update { state ->
                        state.copy(
                            confirmingId = null
                        )
                    }
                    // recargar lista del día
                    refresh()
                }
                .onFailure { error ->
                    _uiState.update { state ->
                        state.copy(
                            confirmingId = null,
                            errorMessage = error.message
                                ?: "No se pudo registrar la toma tardía"
                        )
                    }
                }
        }
    }

}
