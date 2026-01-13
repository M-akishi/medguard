package dev.medguard.app.presentation.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.medguard.app.domain.usecase.CreateMedicationUseCase
import dev.medguard.app.domain.usecase.GetAllMedicationsUseCase

class MedicationListViewModelFactory(
    private val getAllMedications: GetAllMedicationsUseCase,
    private val createMedication: CreateMedicationUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicationListViewModel::class.java)) {
            return MedicationListViewModel(
                getAllMedications = getAllMedications,
                createMedication = createMedication
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
