# Prankstar Feature Implementation Request Template

Copy this prompt into any AI coding assistant to assign one feature implementation.

```markdown
You are working on Prankstar, an Android Kotlin/Java app with a premium cyberpunk prank-lab UI. Implement exactly one feature described below. Do not combine unrelated build, UI, or feature work.

## Feature

Name: TBD
Tracker ID: F-###

## Goal

TBD

## Acceptance Criteria

- [ ] TBD
- [ ] TBD
- [ ] Existing functionality is preserved.
- [ ] All affected files are listed before editing.
- [ ] Validation steps are provided and run where available.

## Required Reading

Read these files first:

- `PROJECT_CONTEXT.md`
- `FEATURE_TRACKER.md`
- `AI_RULES.md`
- `WORKFLOW.md`
- Any feature-specific docs: TBD

## Scope

Allowed files/modules:

- TBD

Forbidden unless explicitly approved:

- Gradle/build files unless this is a build-only PR.
- Android manifest unless directly required.
- Unrelated screens or assets.
- Wholesale rewrites or broad reformatting.

## Inspect First

Before editing, report:

1. Existing implementation files and dependencies.
2. Existing routes/state/assets involved.
3. Regression risks.
4. Exact files you will modify.

## Implementation Rules

- Preserve current behavior unless explicitly changing it.
- Never assume assets exist; verify exact paths.
- Never fake playback or catalog data.
- Add safe fallback/error states.
- Keep UI consistent with Prankstar neon/HUD style.
- Include accessibility basics for UI changes.
- Respect lifecycle, threading, nullability, and resource cleanup.

## Validation Required

- If code/resources change, verify Android SDK environment before Gradle.
- Run the smallest useful tests/checks available.
- If UI changes are visible, capture or request screenshot QA.
- Update trackers/docs touched by the feature.

## Output Required

Use this structure:

A. Acceptance Criteria
B. Findings / Inspection Notes
C. Edit Plan
D. Exact Changes by File Path
E. Verification Checklist with commands and expected success
F. Risks and Rollback Instructions
```
