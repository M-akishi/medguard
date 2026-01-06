# Security Model

- Every dose confirmation requires device authentication.
- Authentication uses Android BiometricPrompt API.
- If biometric authentication is unavailable, system credentials are used.
- Late intake confirmation follows the same authentication rules.
- There is no bypass or manual override.
- Platform-specific implementations (e.g. scheduling, authentication)
must conform to the domain rules but are not part of the domain itself.
