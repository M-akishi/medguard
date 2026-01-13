package dev.medguard.app.presentation.medication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.medguard.app.domain.model.Medication
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationListScreen(
    state: MedicationListUiState,
    onRetry: () -> Unit,
    onAddClick: () -> Unit,
    onDismissAddDialog: () -> Unit,
    onNewNameChange: (String) -> Unit,
    onNewNotesChange: (String) -> Unit,
    onConfirmAddMedication: () -> Unit,
    onMedicationClick: (UUID) -> Unit
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
            state.errorMessage != null && state.medications.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Error al cargar medicamentos")
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
            state.medications.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No hay medicamentos aún")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Toca el botón + para agregar tu primer medicamento.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            else -> {
                MedicationList(
                    medications = state.medications,
                    onMedicationClick = onMedicationClick
                )
            }
        }

        // botón flotante manual arriba de la UI
        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar medicamento"
            )
        }

        if (state.isAddDialogVisible) {
            AddMedicationDialog(
                name = state.newName,
                notes = state.newNotes,
                onNameChange = onNewNameChange,
                onNotesChange = onNewNotesChange,
                onConfirm = onConfirmAddMedication,
                onDismiss = onDismissAddDialog
            )
        }
    }
}

@Composable
private fun MedicationList(
    medications: List<Medication>,
    onMedicationClick: (UUID) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(medications, key = { it.id }) { medication ->
            MedicationItem(
                medication = medication,
                onClick = { onMedicationClick(medication.id) }
            )
        }
    }
}

@Composable
private fun MedicationItem(
    medication: Medication,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = medication.name,
                style = MaterialTheme.typography.titleMedium
            )
            if (!medication.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = medication.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (medication.isActive) "Activo" else "Inactivo",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun AddMedicationDialog(
    name: String,
    notes: String,
    onNameChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo medicamento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Nombre") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    label = { Text("Notas (opcional)") },
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
