package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.Schedule
import dev.medguard.app.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GetSchedulesForMedicationUseCase(
    private val scheduleRepository: ScheduleRepository
) {
    operator fun invoke(medicationId: UUID): Flow<List<Schedule>> =
        scheduleRepository.getByMedicationId(medicationId)
}
