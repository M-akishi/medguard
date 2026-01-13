package dev.medguard.app.domain.usecase

import dev.medguard.app.domain.model.Schedule
import dev.medguard.app.domain.repository.ScheduleRepository

class GetAllActiveSchedulesUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(): List<Schedule> =
        repository.getAllActive()
}
