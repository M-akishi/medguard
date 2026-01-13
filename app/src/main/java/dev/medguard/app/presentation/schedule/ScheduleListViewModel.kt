package dev.medguard.app.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.medguard.app.domain.usecase.GetAllActiveSchedulesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScheduleListViewModel(
    private val getAllActiveSchedules: GetAllActiveSchedulesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleListUiState())
    val uiState: StateFlow<ScheduleListUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val schedules = getAllActiveSchedules()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    schedules = schedules,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error cargando horarios"
                )
            }
        }
    }
}
