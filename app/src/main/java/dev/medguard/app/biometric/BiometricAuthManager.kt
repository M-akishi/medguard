package dev.medguard.app.biometric

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity   // 👈 en vez de ComponentActivity
import java.util.UUID

class BiometricAuthManager(
    private val activity: FragmentActivity
) {

    private val biometricPrompt: BiometricPrompt
    private val promptInfo: BiometricPrompt.PromptInfo

    private var pendingDoseId: UUID? = null
    private var pendingAction: ((UUID) -> Unit)? = null

    init {
        val executor = ContextCompat.getMainExecutor(activity)

        biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    val id = pendingDoseId
                    val action = pendingAction

                    // Limpiamos primero
                    pendingDoseId = null
                    pendingAction = null

                    if (id != null && action != null) {
                        action(id)
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    pendingDoseId = null
                    pendingAction = null
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            }
        )

        val authenticators =
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Confirmar toma")
            .setSubtitle("Autentícate para confirmar esta dosis")
            .setAllowedAuthenticators(authenticators)
            .build()
    }

    fun authenticateDose(doseId: UUID, action: (UUID) -> Unit) {
        pendingDoseId = doseId
        pendingAction = action
        biometricPrompt.authenticate(promptInfo)
    }
}

