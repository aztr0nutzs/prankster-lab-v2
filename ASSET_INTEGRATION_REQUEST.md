# Prankstar Asset Integration Request Template

Copy this prompt into any AI coding assistant to integrate newly added assets.

```markdown
You are integrating assets into Prankstar. This is an asset-focused task. Do not redesign unrelated UI or change build files unless explicitly required.

## Asset Batch

| Asset | Source Path | Intended Target Path | Purpose |
|---|---|---|---|
| TBD | TBD | TBD | TBD |

## Goal

TBD

## Acceptance Criteria

- [ ] Exact source assets exist.
- [ ] Target Android package/resource paths are correct.
- [ ] Referencing code/catalog entries point to packaged assets.
- [ ] Fallback behavior exists for optional assets.
- [ ] `ASSET_REGISTRY.md` is updated.
- [ ] Existing assets are not removed or overwritten without approval.

## Required Reading

- `PROJECT_CONTEXT.md`
- `ASSET_REGISTRY.md`
- `AI_RULES.md`
- Feature docs related to the target screen: TBD

## Inspect First

Report:

1. Existing assets with similar names.
2. Existing code/resource references.
3. Whether assets belong in `assets`, `res/raw`, `res/drawable`, or `mipmap`.
4. Size/performance concerns.
5. Exact files to add/modify.

## Integration Rules

- Never assume root assets are packaged.
- Keep file names Android-resource safe when using `res/`.
- Avoid duplicate catalog IDs and duplicate conflicting resource names.
- Validate audio headers and video/image render behavior where possible.
- Do not edit manifest/build files unless explicitly required and scoped.

## Validation Required

- File existence checks.
- Resource/catalog reference checks.
- Audio catalog validator if sound assets change.
- Build after Android environment verification if Android resources/code change.
- Runtime or screenshot QA when UI-visible assets change.

## Output Required

A. Acceptance Criteria
B. Findings / Inspection Notes
C. Edit Plan
D. Exact Changes by File Path
E. Verification Checklist with commands and expected success
F. Asset Registry Updates
G. Risks and Rollback Instructions
```
