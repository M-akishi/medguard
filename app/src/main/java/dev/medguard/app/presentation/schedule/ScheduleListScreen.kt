package dev.medguard.app.presentation.schedule

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.medguard.app.domain.model.DayOfWeek
import dev.medguard.app.domain.model.Medication
import dev.medguard.app.domain.model.Schedule
import dev.medguard.app.ui.theme.MedGuardTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun ScheduleListScreen(
    state: ScheduleListUiState,
    onRetry: () -> Unit,
    onScheduleClick: (UUID) -> Unit,
    onCreateSchedule: (
        medicationId: UUID,
        time: LocalTime,
        doseDescription: String,
        activeDays: Set<DayOfWeek>,
        isActive: Boolean
    ) -> Unit,
    medications: List<Medication>
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.errorMessage != null && state.schedules.isEmpty() -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Error al cargar horarios")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.errorMessage,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetry) {
                        Text("Reintentar")
                    }
                }
            }

            state.schedules.isEmpty() -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No tienes horarios configurados aún")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Crea un horario para que MedGuard genere tus tomas.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            else -> {
                val medById = remember(medications) {
                    medications.associateBy { it.id }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.schedules, key = { it.id }) { schedule ->
                        val medicationName = medById[schedule.medicationId]?.name?: "Medicamento Desconocido"
                        ScheduleItem(
                            schedule = schedule,
                            medicationName = medicationName,
                            onClick = { onScheduleClick(schedule.id) }
                        )
                    }
                }
            }
        }

        // FAB para crear nuevo horario
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar horario"
            )
        }

        // Diálogo de creación de horario
        if (showDialog) {
            CreateScheduleDialog(
                medications = medications,
                onDismiss = { showDialog = false },
                onConfirm = { medId, time, desc, days, active ->
                    onCreateSchedule(medId, time, desc, days, active)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
private fun ScheduleItem(
    schedule: Schedule,
    medicationName: String,
    onClick: () -> Unit
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val timeText = schedule.time.format(timeFormatter)
    val daysText = formatDays(schedule.activeDays)

    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = medicationName,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = schedule.doseDescription,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = daysText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun formatDays(days: Set<DayOfWeek>): String {
    if (days.isEmpty()) return "Sin días asignados"

    val order = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    )

    val sorted = order.filter { it in days }

    val labels = sorted.map {
        when (it) {
            DayOfWeek.MONDAY -> "Lun"
            DayOfWeek.TUESDAY -> "Mar"
            DayOfWeek.WEDNESDAY -> "Mié"
            DayOfWeek.THURSDAY -> "Jue"
            DayOfWeek.FRIDAY -> "Vie"
            DayOfWeek.SATURDAY -> "Sáb"
            DayOfWeek.SUNDAY -> "Dom"
        }
    }

    return labels.joinToString(", ")
}

@Preview(
    name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)
@Composable
fun ScheduleListScreenPreviewLight() {
    MedGuardTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val fakeMedications = sampleMedications
            val fakeSchedules = sampleSchedules

            ScheduleListScreen(
                state = ScheduleListUiState(
                    isLoading = false,
                    schedules = fakeSchedules,
                    errorMessage = null
                ),
                onRetry = {},
                onScheduleClick = { /* no-op en preview */ },
                onCreateSchedule = { _, _, _, _, _ -> /* no-op en preview */ },
                medications = fakeMedications
            )
        }
    }
}

@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun ScheduleListScreenPreviewDark() {
    MedGuardTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val fakeMedications = sampleMedications
            val fakeSchedules = sampleSchedules

            ScheduleListScreen(
                state = ScheduleListUiState(
                    isLoading = false,
                    schedules = fakeSchedules,
                    errorMessage = null
                ),
                onRetry = {},
                onScheduleClick = {},
                onCreateSchedule = { _, _, _, _, _ -> },
                medications = fakeMedications
            )
        }
    }
}


private val sampleMedications = listOf(
    Medication(
        id = UUID.randomUUID(),
        name = "Paracetamol 500mg",
    ),
    Medication(
        id = UUID.randomUUID(),
        name = "Ibuprofeno 400mg",
    )
)

private val sampleSchedules = listOf(
    Schedule(
        id = UUID.randomUUID(),
        medicationId = sampleMedications[0].id,
        time = LocalTime.of(8, 0),
        doseDescription = "1 comprimido en ayunas",
        activeDays = setOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
        ),
        isActive = true
    ),
    Schedule(
        id = UUID.randomUUID(),
        medicationId = sampleMedications[1].id,
        time = LocalTime.of(20, 0),
        doseDescription = "1 comprimido después de la comida",
        activeDays = setOf(
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        ),
        isActive = true
    )
)

