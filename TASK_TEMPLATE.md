# Prankstar AI Task Template

Use this template for any AI assistant task. Fill every section before implementation.

## Task Title

TBD

## Acceptance Criteria

- [ ] Define the exact user-visible or developer-visible outcome.
- [ ] List every screen, feature, route, asset, or subsystem affected.
- [ ] State what must not change.
- [ ] State required validation commands or manual QA.

## Inspect First

Before editing, inspect and record:

- Relevant files/modules.
- Existing dependencies and versions.
- Existing UI/state/data flow.
- Existing assets and exact paths.
- Existing tests, validators, QA docs, screenshots, or logs.
- Risk areas and conflict-magnet files.

## Assumptions

List minimum-risk assumptions. Do not invent facts. If an asset, route, dependency, or API is uncertain, inspect it or mark it TBD.

## Affected Files

| File | Change Type | Reason | Risk |
|---|---|---|---|
| TBD | Add/Modify/Delete | TBD | TBD |

Do not modify files outside this list without stopping and expanding scope.

## Implementation Plan

1. TBD
2. TBD
3. TBD

## Preservation Requirements

- [ ] Do not remove existing functionality without verification.
- [ ] Do not replace custom UI with generic layouts.
- [ ] Preserve backward compatibility for public/state APIs.
- [ ] Preserve asset references or provide verified fallbacks.
- [ ] Preserve audio playback and stop-all behavior.
- [ ] Preserve navigation and dock active-state behavior.

## File-by-File Modifications

For each file, provide:

### `path/to/file`

- Why it changes:
- Exact sections/functions/classes changed:
- Before/after summary for important logic:
- Validation impact:

## Reasoning

Explain why the selected implementation is the lowest-risk path and how it avoids regressions.

## Validation Plan

| Validation | Command/Action | Success Criteria | Required? |
|---|---|---|---|
| Static check | TBD | TBD | Yes/No |
| Android environment check | `chmod +x scripts/build-android-debug.sh && ./scripts/build-android-debug.sh` | SDK and build environment verified. | Before Gradle |
| Build | TBD | TBD | If code/resources changed |
| Runtime QA | TBD | TBD | If user-visible behavior changed |
| Asset validation | TBD | No missing/corrupt/duplicate assets. | If assets changed |

## Risks

| Risk | Mitigation | Verification |
|---|---|---|
| TBD | TBD | TBD |

## Rollback Instructions

- Revert commit: `git revert <commit>`
- Or restore files: `git checkout -- <file1> <file2>`
- Re-run validation listed above.

## Session Handoff Update

At task end, update `SESSION_HANDOFF.md` with:

- Completed work.
- Remaining work.
- Active bugs.
- Active tasks.
- Important files touched.
- Current priorities.
