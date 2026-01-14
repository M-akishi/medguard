package dev.medguard.app.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.medguard.app.domain.model.DayOfWeek
import dev.medguard.app.domain.usecase.CreateScheduleAndGenerateDosesUseCase
import dev.medguard.app.domain.usecase.GetAllActiveSchedulesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.UUID

class ScheduleListViewModel(
    private val getAllActiveSchedules: GetAllActiveSchedulesUseCase,
    private val createScheduleAndGenerateDoses: CreateScheduleAndGenerateDosesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleListUiState())
    val uiState: StateFlow<ScheduleListUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val schedules = getAllActiveSchedules()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        schedules = schedules,
                        errorMessage = null
                    )
                }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = t.message ?: "Error cargando horarios"
                    )
                }
            }
        }
    }

    fun createSchedule(
        medicationId: UUID,
        time: LocalTime,
        doseDescription: String,
        activeDays: Set<DayOfWeek>,
        isActive: Boolean
    ) {
        viewModelScope.launch {
            try {
                // isActive lo dejamos listo por si luego lo soporta el use case
                createScheduleAndGenerateDoses.execute(
                    medicationId = medicationId,
                    time = time,
                    doseDescription = doseDescription,
                    activeDays = activeDays
                )
                // Después de crear el horario y generar dosis, recargamos
                refresh()
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        errorMessage = t.message ?: "Error al crear el horario"
                    )
                }
            }
        }
    }
}
