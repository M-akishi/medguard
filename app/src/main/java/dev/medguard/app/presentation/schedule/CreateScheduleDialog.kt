@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package dev.medguard.app.presentation.schedule
import android.R.attr.enabled
import android.R.attr.type
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.medguard.app.domain.model.DayOfWeek
import dev.medguard.app.domain.model.Medication
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.foundation.AnchorType
import dev.medguard.app.R
import dev.medguard.app.domain.usecase.CreateScheduleUseCase
import dev.medguard.app.ui.theme.MedGuardTheme

@Composable
fun CreateScheduleDialog(
    medications: List<Medication>,
    onDismiss: () -> Unit,
    onConfirm: (
        medicationId: UUID,
        time: LocalTime,
        doseDescription: String,
        activeDays: Set<DayOfWeek>,
        isActive: Boolean
    ) -> Unit
) {
    var selectedMedication by remember { mutableStateOf<Medication?>(medications.firstOrNull()) }
    var doseDescription by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(setOf<DayOfWeek>()) }
    var isActive by remember { mutableStateOf(true) }

    // Hora manual: HH y MM
    val now = LocalTime.now()
    var hourText by remember { mutableStateOf(now.hour.toString().padStart(2, '0')) }
    var minuteText by remember { mutableStateOf(((now.minute / 5) * 5).toString().padStart(2, '0')) }

    val hour = hourText.toIntOrNull()
    val minute = minuteText.toIntOrNull()
    val isTimeValid = hour != null && minute != null && hour in 0..23 && minute in 0..59

    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_hour_dosis)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (medications.isEmpty()) {
                    Text(
                        text = stringResource(R.string.msg_no_medications),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    // Medicamento
                    Text(
                        text = stringResource(R.string.label_medicine),
                        style = MaterialTheme.typography.labelMedium
                    )
                    MedicationDropdown(
                        medications = medications,
                        selectedMedication = selectedMedication,
                        onMedicationSelected = { selectedMedication = it }
                    )

                    // Hora manual
                    Text(
                        text = "Hora",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = hourText,
                            onValueChange = { new ->
                                // Solo dígitos y máx 2 caracteres
                                val filtered = new.filter { it.isDigit() }.take(2)
                                hourText = filtered
                            },
                            label = { Text("HH") },
                            singleLine = true,
                            modifier = Modifier.width(80.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            )
                        )


                        Text(":")

                        OutlinedTextField(
                            value = minuteText,
                            onValueChange = { new ->
                                val filtered = new.filter { it.isDigit() }.take(2)
                                minuteText = filtered
                            },
                            label = { Text("MM") },
                            singleLine = true,
                            modifier = Modifier.width(80.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            )
                        )

                    }

                    if (!isTimeValid && (hourText.isNotBlank() || minuteText.isNotBlank())) {
                        Text(
                            text = "Hora inválida. Usa formato 00–23 y 00–59.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (isTimeValid) {
                        Text(
                            text = "Hora seleccionada: " +
                                    LocalTime.of(hour!!, minute!!).format(timeFormatter),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Días
                    Text(
                        text = "Días de la semana",
                        style = MaterialTheme.typography.labelMedium
                    )
                    DaysOfWeekSelector(
                        selectedDays = selectedDays,
                        onDayToggle = { day ->
                            selectedDays = if (day in selectedDays) {
                                selectedDays - day
                            } else {
                                selectedDays + day
                            }
                        }
                    )

                    // Descripción
                    OutlinedTextField(
                        value = doseDescription,
                        onValueChange = { doseDescription = it },
                        label = { Text("Descripción de la dosis") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3
                    )

                    // Activo
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Horario activo")
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = isActive,
                            onCheckedChange = { isActive = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            val canSave =
                medications.isNotEmpty() &&
                        selectedMedication != null &&
                        selectedDays.isNotEmpty() &&
                        doseDescription.isNotBlank() &&
                        isTimeValid

            TextButton(
                enabled = canSave,
                onClick = {
                    val med = selectedMedication ?: return@TextButton
                    val safeHour = hour ?: return@TextButton
                    val safeMinute = minute ?: return@TextButton
                    val time = LocalTime.of(safeHour, safeMinute)

                    onConfirm(
                        med.id,
                        time,
                        doseDescription.trim(),
                        selectedDays,
                        isActive
                    )
                }
            ) {
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

@Composable
private fun MedicationDropdown(
    medications: List<Medication>,
    selectedMedication: Medication?,
    onMedicationSelected: (Medication) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedMedication?.name ?: "Selecciona un medicamento",
            onValueChange = {},
            readOnly = true,
            label = { Text("Medicamento") },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            medications.forEach { med ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = med.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        onMedicationSelected(med)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DaysOfWeekSelector(
    selectedDays: Set<DayOfWeek>,
    onDayToggle: (DayOfWeek) -> Unit
) {
    val orderedDays = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    )

    val labels = mapOf(
        DayOfWeek.MONDAY to "L",
        DayOfWeek.TUESDAY to "M",
        DayOfWeek.WEDNESDAY to "X",
        DayOfWeek.THURSDAY to "J",
        DayOfWeek.FRIDAY to "V",
        DayOfWeek.SATURDAY to "S",
        DayOfWeek.SUNDAY to "D"
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        orderedDays.forEach { day ->
            val selected = day in selectedDays
            FilterChip(
                selected = selected,
                onClick = { onDayToggle(day) },
                label = { Text(labels[day] ?: day.name.first().toString()) }
            )
        }
    }
}


@Preview(showBackground = true, name = "CreateSchedule - Success")
@Composable
fun CreateScheduleDialogPreview() {
    MedGuardTheme {
        CreateScheduleDialog(
            medications = sampleMedications,
            onDismiss = {},
            onConfirm = { medicationId, time, doseDescription, activeDays, isActive ->
            }
        )
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


