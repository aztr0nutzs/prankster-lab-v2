# Prankstar Bug Tracker

## Severity Levels

| Severity | Definition | Examples |
|---|---|---|
| S0 Critical | Blocks launch, build, install, or core app startup. | App will not build, app crashes on launch. |
| S1 High | Breaks core feature or causes data/media corruption. | Playback broken, reactor crashes, generated clips lost. |
| S2 Medium | Important feature degraded with workaround. | One route broken, asset missing but fallback works. |
| S3 Low | Cosmetic, copy, minor layout, non-blocking issue. | Slight spacing issue, typo. |
| S4 Trivial | Cleanup or documentation only. | Tracker update, non-user-facing note. |

## Bug Status

| Status | Meaning |
|---|---|
| New | Reported but not investigated. |
| Reproduced | Confirmed with steps/logs. |
| Root Cause Found | Cause identified. |
| Fix In Progress | Active work underway. |
| Fixed | Code/assets changed. |
| Verified | Fix validated with evidence. |
| Won't Fix | Documented product/technical decision. |

## Active Bugs

| Bug ID | Title | Severity | Status | Area | Owner/Session | First Seen | Notes |
|---|---|---|---|---|---|---|---|
| BUG-000 | Template row | S3 | New | TBD | Unassigned | TBD | Replace with real bug. |

## Bug Report Template

### Bug ID

`BUG-###`

### Title

Short user-visible problem statement.

### Severity

S0 / S1 / S2 / S3 / S4

### Status

New / Reproduced / Root Cause Found / Fix In Progress / Fixed / Verified / Won't Fix

### Affected Area

Screen, feature, route, asset, or subsystem.

### Environment

| Field | Value |
|---|---|
| Device/emulator | TBD |
| Android version | TBD |
| App build/commit | TBD |
| Build variant | TBD |
| Network state | TBD |
| Reduced animation setting | TBD |

### Reproduction Steps

1. TBD
2. TBD
3. TBD

### Expected Result

TBD

### Actual Result

TBD

### Evidence

- Logs:
- Screenshots:
- Videos:
- Commands:

### Root Cause

TBD. Include exact files/functions and why the failure occurs.

### Resolution

TBD. Include exact files changed and why the fix is minimal-risk.

### Verification

| Check | Command/Action | Expected Result | Result |
|---|---|---|---|
| Build | TBD | Build succeeds. | TBD |
| Runtime | TBD | Bug no longer reproduces. | TBD |
| Regression | TBD | Related flows still pass. | TBD |

### Rollback Plan

TBD. Include exact commit/file rollback instructions.
