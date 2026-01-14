package dev.medguard.app.presentation.dose
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.medguard.app.MainActivity
import dev.medguard.app.domain.model.Dose
import dev.medguard.app.domain.model.DoseStatus
import dev.medguard.app.domain.model.Medication
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay


@Composable
fun DoseListScreen(
    state: DoseListUiState,
    onRetry: () -> Unit,
    onDoseClick: (UUID) -> Unit,
    medications: List<Medication>
) {
    val activity = LocalContext.current as? MainActivity

    // Mapa medicamentoId -> Medication
    val medById = remember(medications) {
        medications.associateBy { it.id }
    }

    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalDateTime.now()
            val nowMillis = System.currentTimeMillis()
            val millisUntilNextMinute = 60_000 - (nowMillis % 60_000)
            delay(millisUntilNextMinute)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.errorMessage != null && state.doses.isEmpty() -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Error al cargar tomas")
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

            state.doses.isEmpty() -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No hay tomas para hoy")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Configura horarios para que MedGuard genere tus tomas.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.doses, key = { it.id }) { dose ->
                        val medicationName =
                            medById[dose.medicationId]?.name ?: "Medicamento desconocido"

                        DoseItem(
                            medicationName = medicationName,
                            dose = dose,
                            currentTime = currentTime,
                            isConfirming = state.confirmingId == dose.id,
                            onConfirm = { activity?.authenticateConfirmDose(dose.id) },
                            onRecordLateIntake = { activity?.authenticateLateIntake(dose.id) },
                        )
                    }
                }
            }
        }

        // Mensaje de error global abajo
        state.errorMessage?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            )
        }
    }
}
@Composable
private fun DoseItem(
    dose: Dose,
    medicationName: String,
    currentTime: LocalDateTime,
    isConfirming: Boolean,
    onConfirm: () -> Unit,
    onRecordLateIntake: () -> Unit,
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val timeText = dose.scheduledDateTime.toLocalTime().format(timeFormatter)

    val statusText = when (dose.status) {
        DoseStatus.PENDING -> "Pendiente"
        DoseStatus.TAKEN -> "Tomada"
        DoseStatus.MISSED -> "Omitida"
        else -> dose.status.name
    }

    val statusColor = when (dose.status) {
        DoseStatus.PENDING -> MaterialTheme.colorScheme.primary
        DoseStatus.TAKEN -> MaterialTheme.colorScheme.tertiary
        DoseStatus.MISSED -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }

    val graceEnd = remember(dose.scheduledDateTime) {
        dose.scheduledDateTime.plusHours(1)
    }

    val canConfirmNow = dose.status == DoseStatus.PENDING &&
            currentTime >= dose.scheduledDateTime &&
            currentTime <= graceEnd &&
            dose.lateIntakeAt == null

    val canRecordLateNow = dose.status == DoseStatus.PENDING &&
            currentTime > graceEnd &&
            dose.lateIntakeAt == null


    Card(
        modifier = Modifier
            .fillMaxWidth()
        // .clickable(onClick = onClick) si luego quieres navegar al detalle
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = medicationName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                AssistChip(
                    onClick = {},
                    label = { Text(statusText) },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = statusColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = dose.doseDescription,
                style = MaterialTheme.typography.bodyMedium
            )

            if (dose.takenAt != null && dose.status == DoseStatus.TAKEN) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tomada a las ${
                        dose.takenAt.toLocalTime().format(timeFormatter)
                    }",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (dose.status == DoseStatus.PENDING && dose.lateIntakeAt == null) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    when {
                        isConfirming -> {
                            Button(onClick = {}, enabled = false) {
                                Text("Procesando...")
                            }
                        }

                        canConfirmNow -> {
                            Button(
                                onClick = onConfirm,
                                enabled = true
                            ) {
                                Text("Marcar como tomada")
                            }
                        }

                        canRecordLateNow -> {
                            TextButton(
                                onClick = onRecordLateIntake
                            ) {
                                Text("Registrar toma tardía")
                            }
                        }

                        else -> {
                            // Antes de la hora programada
                            OutlinedButton(onClick = {}, enabled = false) {
                                Text("Aún no es la hora")
                            }
                        }
                    }
                }
            }



            if (dose.lateIntakeAt != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tomada tarde a las ${
                        dose.lateIntakeAt.toLocalTime().format(timeFormatter)
                    }",
                    style = MaterialTheme.typography.bodySmall
                )
            }


            if (dose.status == DoseStatus.MISSED && dose.lateIntakeAt == null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = onRecordLateIntake,
                        enabled = !isConfirming
                    ) {
                        Text(
                            if (isConfirming) "Guardando..." else "Registrar toma tardía"
                        )
                    }
                }
            }
        }
    }
}
