package dev.medguard.app
import androidx.activity.viewModels
import dev.medguard.app.biometric.BiometricAuthManager
import android.os.Bundle
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
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import dev.medguard.app.data.android.DoseReminderSchedulerImpl
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
import dev.medguard.app.domain.reminder.DoseReminderScheduler
import dev.medguard.app.domain.usecase.ConfirmDoseUseCase
import dev.medguard.app.domain.usecase.GetDosesForDateUseCase
import dev.medguard.app.domain.usecase.CreateScheduleAndGenerateDosesUseCase
import dev.medguard.app.presentation.dose.DoseListRoute
import dev.medguard.app.presentation.dose.DoseListViewModelFactory
import dev.medguard.app.domain.usecase.CreateScheduleUseCase
import dev.medguard.app.domain.usecase.GenerateDailyDosesUseCase
import dev.medguard.app.domain.usecase.RecordLateIntakeUseCase
import dev.medguard.app.presentation.dose.DoseListViewModel
import java.time.Clock
import java.util.UUID
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dev.medguard.app.data.android.DailyDosesWorker
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit



class MainActivity : FragmentActivity() {

    private lateinit var biometricAuthManager: BiometricAuthManager
    private val reminderScheduler: DoseReminderScheduler by lazy {
        DoseReminderSchedulerImpl(applicationContext)
    }

    private fun scheduleDailyDosesWorker() {
        val targetTime = LocalTime.of(2, 0)

        val now = LocalDateTime.now()
        var firstRun = now.withHour(targetTime.hour).withMinute(targetTime.minute)
            .withSecond(0).withNano(0)

        if (firstRun.isBefore(now)) {
            firstRun = firstRun.plusDays(1)
        }

        val initialDelayMillis = Duration.between(now, firstRun).toMillis()

        val request = PeriodicWorkRequestBuilder<DailyDosesWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "daily_doses_generation",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                "dose_reminders",
                "Recordatorios de dosis",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones cuando es hora de tomar un medicamento"
            }

            val manager = getSystemService(android.app.NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

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
        ConfirmDoseUseCase(doseRepository)
    }

    private val createScheduleUseCase by lazy {
        CreateScheduleUseCase(scheduleRepository)
    }

    private val generateDailyDosesUseCase by lazy {
        GenerateDailyDosesUseCase(
            scheduleRepository = scheduleRepository,
            doseRepository = doseRepository,
            reminderScheduler = reminderScheduler
        )
    }

    private val createScheduleAndGenerateDosesUseCase by lazy {
        CreateScheduleAndGenerateDosesUseCase(
            createScheduleUseCase = createScheduleUseCase,
            generateDailyDosesUseCase = generateDailyDosesUseCase
        )
    }

    private val scheduleFactory by lazy {
        ScheduleListViewModelFactory(
            getAllActiveSchedules = getAllActiveSchedulesUseCase,
            createScheduleAndGenerateDoses = createScheduleAndGenerateDosesUseCase
        )
    }

    private val recordLateIntakeUseCase by lazy {
        RecordLateIntakeUseCase(
            doseRepository = doseRepository,
            clock = Clock.systemDefaultZone()
        )
    }

    private val doseListViewModel: DoseListViewModel by viewModels {
        DoseListViewModelFactory(
            getDosesForDate = getDosesForDateUseCase,
            confirmDoseTaken = confirmDoseTakenUseCase,
            recordLateIntake = recordLateIntakeUseCase
        )
    }


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }

        biometricAuthManager = BiometricAuthManager(this)
        createNotificationChannel()

        scheduleDailyDosesWorker()

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

                val medicationFactory = MedicationListViewModelFactory(
                    getAllMedications = getAllMedicationsUseCase,
                    createMedication = createMedicationUseCase
                )
                val doseFactory = DoseListViewModelFactory(
                    getDosesForDate = getDosesForDateUseCase,
                    confirmDoseTaken = confirmDoseTakenUseCase,
                    recordLateIntake = recordLateIntakeUseCase
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
                                        onScheduleClick = { /* TODO: detalle schedule */ }
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
                                        onDoseClick = { /* TODO: detalle de la toma si quieres */ },
                                        medications = medications
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
    fun authenticateConfirmDose(doseId: UUID) {
        biometricAuthManager.authenticateDose(doseId) { id ->
            doseListViewModel.confirmDose(id)
        }
    }

    fun authenticateLateIntake(doseId: UUID) {
        biometricAuthManager.authenticateDose(doseId) { id ->
            doseListViewModel.recordLateIntake(id)
        }
    }
}
