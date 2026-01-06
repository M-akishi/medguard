# Business Rules

- A dose must be explicitly confirmed by the user
- A dose cannot be confirmed before its scheduled time
- Scheduled time refers to the local device time defined by the Schedule.
- Confirmation requires biometric or system credential authentication
- Dose status transitions are one-way:
  pending → taken | missed
- If not confirmed within the configured window, a dose is marked as missed
- The confirmation window starts at the scheduled time of the dose.
- Missed doses remain permanently visible in the treatment history and are never deleted or altered.
- Editing a schedule does not affect historical doses
