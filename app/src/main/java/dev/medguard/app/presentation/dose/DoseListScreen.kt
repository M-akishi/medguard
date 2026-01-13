package dev.medguard.app.presentation.dose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.medguard.app.domain.model.Dose
import dev.medguard.app.domain.model.DoseStatus
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun DoseListScreen(
    state: DoseListUiState,
    onRetry: () -> Unit,
    onConfirmDose: (UUID) -> Unit,
    onDoseClick: (UUID) -> Unit
) {
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
                        DoseItem(
                            dose = dose,
                            isConfirming = state.confirmingId == dose.id,
                            onConfirm = { onConfirmDose(dose.id) },
                            onClick = { onDoseClick(dose.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DoseItem(
    dose: Dose,
    isConfirming: Boolean,
    onConfirm: () -> Unit,
    onClick: () -> Unit
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

    Card(
        modifier = Modifier.fillMaxWidth()
        // .clickable(onClick = onClick) si luego quieres navegar al detalle de la toma
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
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
                    text = "Tomada a las ${dose.takenAt.toLocalTime().format(timeFormatter)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (dose.status == DoseStatus.PENDING) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onConfirm,
                        enabled = !isConfirming
                    ) {
                        Text(if (isConfirming) "Confirmando..." else "Marcar como tomada")
                    }
                }
            }
        }
    }
}
