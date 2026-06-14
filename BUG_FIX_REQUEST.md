# Prankstar Bug Fix Request Template

Copy this prompt into any AI coding assistant to assign one bug fix.

```markdown
You are fixing one Prankstar Android bug. Do not implement unrelated features or redesign UI.

## Bug

Bug ID: BUG-###
Title: TBD
Severity: S0/S1/S2/S3/S4

## Evidence Provided

Logs/screenshots/repro steps:

- TBD

## Expected Behavior

TBD

## Actual Behavior

TBD

## Required Reading

- `PROJECT_CONTEXT.md`
- `BUG_TRACKER.md`
- `AI_RULES.md`
- `WORKFLOW.md`
- Relevant feature docs: TBD

## Instructions

1. Quote the key failing log/error lines if provided.
2. Reproduce or reason from code if runtime reproduction is unavailable.
3. Identify whether the cause is Gradle, AGP, Kotlin, JDK, dependency resolution, manifest/resource, runtime logic, lifecycle, asset, or UI.
4. List exact affected files before editing.
5. Make the smallest safe fix.
6. Preserve existing functionality.
7. Add or update validation where appropriate.
8. Update `BUG_TRACKER.md` with root cause, resolution, and verification.

## Forbidden

- No unrelated cleanup.
- No wholesale rewrites.
- No fake success paths.
- No removal of custom UI or media behavior to avoid the bug.

## Validation Required

- Run targeted checks.
- If Gradle is needed, run the Android environment check first.
- Verify regression areas related to the bug.
- Provide rollback instructions.

## Output Required

A. Acceptance Criteria
B. Findings / Inspection Notes
C. Edit Plan
D. Exact Changes by File Path
E. Verification Checklist with commands and expected success
F. Root Cause
G. Risks and Rollback Instructions
```
