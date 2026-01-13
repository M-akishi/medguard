package dev.medguard.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import dev.medguard.app.data.local.room.MedGuardDatabase
import dev.medguard.app.data.repository.MedicationRepositoryImpl
import dev.medguard.app.data.repository.ScheduleRepositoryImpl
import dev.medguard.app.domain.usecase.CreateMedicationUseCase
import dev.medguard.app.domain.usecase.GetAllMedicationsUseCase
import dev.medguard.app.domain.usecase.GetAllActiveSchedulesUseCase
import dev.medguard.app.presentation.HomeSection
import dev.medguard.app.presentation.medication.MedicationListRoute
import dev.medguard.app.presentation.medication.MedicationListViewModelFactory
import dev.medguard.app.presentation.schedule.ScheduleListRoute
import dev.medguard.app.presentation.schedule.ScheduleListViewModelFactory
import dev.medguard.app.ui.theme.MedGuardTheme
import kotlinx.coroutines.launch
import dev.medguard.app.data.repository.DoseRepositoryImpl
import dev.medguard.app.domain.model.Medication
import dev.medguard.app.domain.usecase.GetDosesForDateUseCase
import dev.medguard.app.domain.usecase.ConfirmDoseTakenUseCase
import dev.medguard.app.presentation.dose.DoseListRoute
import dev.medguard.app.presentation.dose.DoseListViewModelFactory


class MainActivity : ComponentActivity() {

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            MedGuardDatabase::class.java,
            "medguard.db"
        ).build()
    }

    private val medicationRepository by lazy {
        MedicationRepositoryImpl(database.medicationDao())
    }

    private val scheduleRepository by lazy {
        ScheduleRepositoryImpl(database.scheduleDao())
    }

    private val getAllMedicationsUseCase by lazy {
        GetAllMedicationsUseCase(medicationRepository)
    }

    private val createMedicationUseCase by lazy {
        CreateMedicationUseCase(medicationRepository)
    }

    private val getAllActiveSchedulesUseCase by lazy {
        GetAllActiveSchedulesUseCase(scheduleRepository)
    }

    private val doseRepository by lazy {
        DoseRepositoryImpl(database.doseDao())
    }

    private val getDosesForDateUseCase by lazy {
        GetDosesForDateUseCase(doseRepository)
    }

    private val confirmDoseTakenUseCase by lazy {
        ConfirmDoseTakenUseCase(doseRepository)
    }


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedGuardTheme {
                var currentSection by remember { mutableStateOf(HomeSection.SCHEDULES) }

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var medications by remember { mutableStateOf<List<Medication>>(emptyList()) }
                LaunchedEffect(currentSection) {
                    if (currentSection == HomeSection.SCHEDULES ||
                        currentSection == HomeSection.MEDICATIONS
                    ) {
                        medications = getAllMedicationsUseCase()
                    }
                }
                val scheduleFactory = ScheduleListViewModelFactory(
                    getAllActiveSchedules = getAllActiveSchedulesUseCase
                )

                val medicationFactory = MedicationListViewModelFactory(
                    getAllMedications = getAllMedicationsUseCase,
                    createMedication = createMedicationUseCase
                )
                val doseFactory = DoseListViewModelFactory(
                    getDosesForDate = getDosesForDateUseCase,
                    confirmDoseTaken = confirmDoseTakenUseCase
                )


                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        DrawerContent(
                            currentSection = currentSection,
                            onSectionSelected = { section ->
                                currentSection = section
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        when (currentSection) {
                                            HomeSection.SCHEDULES -> "Horarios"
                                            HomeSection.MEDICATIONS -> "Medicamentos"
                                            HomeSection.DOSES -> "Tomas de Hoy"
                                        }
                                    )
                                },
                                navigationIcon = {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                drawerState.open()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Abrir menú"
                                        )
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            when (currentSection) {
                                HomeSection.SCHEDULES -> {
                                    ScheduleListRoute(
                                        factory = scheduleFactory,
                                        medications = medications,
                                        onScheduleClick = { /* TODO */ },
                                        onCreateSchedule = { medId, time, desc, days, isActive ->
                                            // TODO: aquí vas a llamar a tu UseCase para crear Schedule
                                        }
                                    )
                                }
                                HomeSection.MEDICATIONS -> {
                                    MedicationListRoute(
                                        factory = medicationFactory,
                                        onMedicationClick = { /* TODO: detalle medicamento */ }
                                    )
                                }
                                HomeSection.DOSES -> {
                                    DoseListRoute(
                                        factory = doseFactory,
                                        onDoseClick = { /* TODO: detalle de la toma si quieres */ }
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    @Composable
    private fun DrawerContent(
        currentSection: HomeSection,
        onSectionSelected: (HomeSection) -> Unit
    ) {
        ModalDrawerSheet(
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Spacer(modifier = Modifier.padding(top = 24.dp))
            Text(
                text = "MedGuard",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Spacer(modifier = Modifier.padding(vertical = 8.dp))

            NavigationDrawerItem(
                label = { Text("Horarios") },
                selected = currentSection == HomeSection.SCHEDULES,
                onClick = { onSectionSelected(HomeSection.SCHEDULES) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { Text("Medicamentos") },
                selected = currentSection == HomeSection.MEDICATIONS,
                onClick = { onSectionSelected(HomeSection.MEDICATIONS) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { Text("Tomas de hoy") },
                selected = currentSection == HomeSection.DOSES,
                onClick = { onSectionSelected(HomeSection.DOSES) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }

}
