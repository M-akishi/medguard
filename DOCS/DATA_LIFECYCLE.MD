# Data Lifecycle

## Creation
- Schedules generate doses ahead of time.
- Doses are immutable once marked taken or missed.

## Updates
- Schedule edits only affect future doses.
- Historical doses are never modified.

## Deletion
- Medications can be marked inactive.
- Doses and late intake records are never deleted.

## Retention
- All historical records remain available for review.
