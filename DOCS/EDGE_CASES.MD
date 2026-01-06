# Edge Cases and Decisions

## Multiple schedules at the same time
Allowed. The application does not enforce uniqueness.

## Medication deletion
Medications can be marked inactive.
Historical doses are never deleted.

## Time Zone Changes

- All schedules are defined in local device time.
- A schedule always triggers at the same local clock time, regardless of time zone changes.
- Time zone changes do not retroactively modify existing doses.
- Historical doses keep their original scheduled time.
- Daylight saving time changes do not generate extra doses.
- Missed or taken doses are not recalculated during DST transitions.

## Device Downtime

- If the device is powered off during a scheduled time, the dose is still generated.
- The reminder may not be shown.
- The dose can still be confirmed later within the allowed window.

## Late Intake

- If a dose is marked as missed, it cannot transition to taken.
- The user may record a late intake after a dose is missed.
- Late intake does not change the dose status.
- Late intake requires authentication.
- Late intake time is stored for historical reference only.