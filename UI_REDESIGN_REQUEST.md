# Prankstar UI Redesign Request Template

Copy this prompt into any AI coding assistant to assign focused UI revision work.

```markdown
You are performing a focused Prankstar UI revision. Preserve the premium cyberpunk identity and existing functionality. Do not replace custom UI with generic Material layouts.

## UI Area

Screen/component: TBD
Route: TBD
Tracker IDs: TBD

## Goal

TBD

## Acceptance Criteria

- [ ] Header requirements from `UI_SPECIFICATION.md` are met where applicable.
- [ ] Dock requirements are preserved where applicable.
- [ ] Reactor display requirements are preserved where applicable.
- [ ] Accessibility basics are included.
- [ ] Layout works on compact phone width.
- [ ] Existing feature behavior is unchanged unless explicitly listed.

## Required Reading

- `PROJECT_CONTEXT.md`
- `UI_SPECIFICATION.md`
- `AI_RULES.md`
- Relevant existing UI docs/screenshots: TBD

## Visual Constraints

Must preserve:

- Dark cyberpunk base.
- Neon edge lighting/glow.
- Layered HUD surfaces.
- Custom headers.
- Custom bottom dock.
- Reactor interactions.
- Waveform/animated systems where present.

## Inspect First

Report:

1. Existing composables/layout files.
2. Existing state/events/navigation.
3. Existing assets and exact resource names.
4. Potential overlap with dock/header/system bars.
5. Exact files to modify.

## Implementation Rules

- Avoid broad reformatting.
- Keep state handling for loading/error/empty states.
- Do not remove accessibility labels.
- Do not crop baked art/text.
- Respect reduced-animation preference for changed animations.
- If screenshots are possible, capture before/after or final state.

## Validation Required

- Static/build checks as appropriate.
- Manual compact-width QA instructions.
- Screenshot evidence if environment supports it.
- Regression checks for navigation and primary interactions.

## Output Required

A. Acceptance Criteria
B. Findings / Inspection Notes
C. Edit Plan
D. Exact Changes by File Path
E. Verification Checklist with commands and expected success
F. Visual QA Notes
G. Risks and Rollback Instructions
```
