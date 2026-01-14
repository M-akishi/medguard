package dev.medguard.app.domain.reminder
import java.time.LocalDateTime
import java.util.UUID

interface DoseReminderScheduler {
    fun scheduleDoseReminder(doseId: UUID, dateTime: LocalDateTime)
    fun cancelDoseReminder(doseId: UUID)
}