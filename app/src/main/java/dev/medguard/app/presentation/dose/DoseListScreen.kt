package dev.medguard.app.presentation.dose
import android.content.res.Configuration
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import dev.medguard.app.ui.theme.MedGuardTheme
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
            state.isLoading && state.doses.isEmpty() -> {
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

        if (state.isLoading && state.doses.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .background(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.3f)
                    )
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

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

@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true)
@Composable
fun DoseListScreenPreviewDark() {
    MedGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val fakeMedications = sampleMedications

            val fakeDoses = sampleDoses

            DoseListScreen(
                state = DoseListUiState(
                    isLoading = false,
                    errorMessage = null,
                    doses = fakeDoses,
                    // si tu UiState tiene más campos (ej: confirmingId), agrégalos aquí:
                    // confirmingId = null
                ),
                onRetry = {},
                onDoseClick = {},
                medications = fakeMedications
            )
        }
    }
}

@Preview(
    name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true)
@Composable
fun DoseListScreenPreviewLight() {
    MedGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val fakeMedications = sampleMedications

            val fakeDoses = sampleDoses

            DoseListScreen(
                state = DoseListUiState(
                    isLoading = false,
                    errorMessage = null,
                    doses = fakeDoses,
                ),
                onRetry = {},
                onDoseClick = {},
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

private val sampleDoses = listOf(
    Dose(
        id = UUID.randomUUID(),
        scheduleId = UUID.randomUUID(),
        medicationId = sampleMedications[0].id,
        scheduledDateTime = LocalDateTime.now().plusMinutes(15),
        doseDescription = "1 comprimido después de comer",
        status = DoseStatus.PENDING,
        takenAt = null,
        lateIntakeAt = null
    ),
    Dose(
        id = UUID.randomUUID(),
        scheduleId = UUID.randomUUID(),
        medicationId = sampleMedications[1].id,
        scheduledDateTime = LocalDateTime.now().minusMinutes(10),
        doseDescription = "1 comprimido con agua",
        status = DoseStatus.MISSED,
        takenAt = null,
        lateIntakeAt = null
    )
)

