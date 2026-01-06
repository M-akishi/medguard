# Architecture Overview

## Platform
- Android only

## Technology Stack
- Language: Kotlin
- UI: Jetpack Compose
- Architecture: MVVM
- Local storage: Room (SQLite)
- Background tasks: AlarmManager / Foreground Services
- Authentication: Android BiometricPrompt API

## Core Principles
- Local-first design
- No cloud dependency
- Explicit user confirmation for dose intake
- Clear separation of UI and business logic

## Non-Goals
- Medical recommendations
- Automatic dose adjustments
- Remote monitoring
