# Domain Model

## Medication
- Name
- Optional notes
- Can have multiple schedules
- Can be marked as inactive
- Can only be permanently deleted if it has no associated schedules, doses, or late intake records

## Schedule
- Medication reference
- One fixed time per day
- Dose description
- Active weekdays
- Multiple schedules may share the same time

## Dose
- Generated from a Schedule
- Scheduled time
- Dose description
- Status: pending | taken | missed
- Immutable once confirmed or missed

## Late Intake
Represents a user-declared intake after a dose was missed.
- Associated dose
- Actual intake time
- Requires confirmation
- Does not alter dose status

