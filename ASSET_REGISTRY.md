# Prankstar Asset Registry

## Rules

- Never assume assets exist. Inspect the exact path before referencing an asset.
- Never assume a root-level asset is packaged into Android.
- Every new or newly wired asset must be added to this registry.
- Do not remove, compress, rename, or replace assets without documenting references and verification.
- Validate audio headers/duration and video/image render behavior before release.

## Status Legend

| Status | Meaning |
|---|---|
| Proposed | Requested but not present. |
| Present | Exists in repo. |
| Packaged | Exists in Android package path. |
| Wired | Referenced by app code/catalog. |
| Verified | Runtime or automated validation complete. |
| Deprecated | Retained but no longer actively used. |
| Broken | Missing, corrupt, mismatched, or crashing. |

## Images

| Asset Name | Purpose | Location | Referencing Files | Status | Notes |
|---|---|---|---|---|---|
| `prankstar_banner.png` | Branded banner source | Repository root | TBD | Present | Confirm if packaged before use. |
| `prankstar_dock.png` | Dock visual source | Repository root | TBD | Present | Confirm Android resource mapping. |
| `prankstar_robot_power.png` | Robot/static power visual | Repository root | TBD | Present | Confirm fallback usage. |
| `twak_attack_header.png` | Twak-Attacks header source | Repository root / drawable when integrated | TBD | Present | Track packaged drawable reference. |

## Icons

| Asset Name | Purpose | Location | Referencing Files | Status | Notes |
|---|---|---|---|---|---|
| `ic_launcher.*` | Launcher icon | `app/src/main/res/mipmap-*` | Manifest/launcher config | TBD | Do not edit manifest without explicit scope. |
| TBD | Dock/action icons | TBD | TBD | Proposed | Add rows as icons are identified. |

## Videos

| Asset Name | Purpose | Location | Referencing Files | Status | Notes |
|---|---|---|---|---|---|
| `thinking.mp4` | Bot/assistant mood source | Repository root | TBD | Present | Confirm packaging/mapping before use. |
| `surprised.mp4` | Bot/assistant mood source | Repository root | TBD | Present | Confirm packaging/mapping before use. |
| `angry.mp4` | Bot/assistant mood source | Repository root | TBD | Present | Confirm packaging/mapping before use. |
| `reactor4.mp4` | Reactor video source | Repository root and/or packaged asset path | TBD | Present | Verify exact packaged path. |
| `reactor7.mp4` | Reactor video source | Repository root and/or packaged asset path | TBD | Present | Verify exact packaged path. |
| `reactor11.mp4` | Reactor video source | Repository root and/or packaged asset path | TBD | Present | Verify exact packaged path. |

## Audio

| Asset Name | Purpose | Location | Referencing Files | Status | Notes |
|---|---|---|---|---|---|
| Sound catalog entries | Prank playback | `app/src/main/assets/sounds/`, `public/sounds/` | Sound repository/catalog files | TBD | Validate no missing, corrupt, duplicate IDs/paths. |
| Generated clips | User-generated playback | App-private storage | Forge/Stash metadata | TBD | Validate cleanup and missing-file state. |

## Animations

| Asset Name | Purpose | Location | Referencing Files | Status | Notes |
|---|---|---|---|---|---|
| Reactor pulse/waveform | Core state feedback | Code-driven Compose animation | Reactor components | TBD | Must respect reduced animation where modified. |
| Header video loop | Branded motion header | `res/raw` or assets | Header components | TBD | Must release player safely. |
| Bot mood loops | Assistant state | `res/raw` or drawable fallback | Bot video component | TBD | Missing mood must fallback safely. |

## Robot Assets

| Asset Name | Mood/Use | Location | Referencing Files | Status | Notes |
|---|---|---|---|---|---|
| `prankstar_bot_*` | Default bot moods | `app/src/main/res/raw/` when packaged | Bot video resolver | TBD | Confirm exact resource names. |
| `twakbot_*` | Twak-Attacks bot moods | `app/src/main/res/raw/` or drawable when packaged | Twak/Voice Lab components | TBD | Feature-scoped assets. |
| `prankstar_robot_power.png` | Static fallback/source | Repository root or drawable | Bot fallback UI | Present | Confirm packaged fallback. |

## Reactor Assets

| Asset Name | Reactor Mode | Location | Referencing Files | Status | Notes |
|---|---|---|---|---|---|
| `reactor1.mp4` | Reactor video variant | `app/src/main/assets/prankstar/assets/` when packaged | Native/WebView Home | TBD | Confirm exact file. |
| `reactor2.mp4` | Reactor video variant | `app/src/main/assets/prankstar/assets/` when packaged | Native/WebView Home | TBD | Confirm exact file. |
| `reactor3.mp4` | Reactor video variant | `app/src/main/assets/prankstar/assets/` when packaged | Native/WebView Home | TBD | Confirm exact file. |
| `reactor4.mp4` | Reactor video variant | `app/src/main/assets/prankstar/assets/` when packaged | Native/WebView Home | TBD | Existing docs mention packaging; reverify before release. |
| `reactor5.mp4` | Reactor video variant | `app/src/main/assets/prankstar/assets/` when packaged | Native/WebView Home | TBD | Confirm exact file. |
| `reactor6.mp4` | Reactor video variant | `app/src/main/assets/prankstar/assets/` when packaged | Native/WebView Home | TBD | Confirm exact file. |
| `reactor7.mp4` | Reactor video variant | `app/src/main/assets/prankstar/assets/` when packaged | Native/WebView Home | TBD | Confirm exact file. |

## New Asset Intake Template

| Asset Name | Purpose | Source Location | Target Location | Referencing Files | Status | Validation Needed | Notes |
|---|---|---|---|---|---|---|---|
| TBD | TBD | TBD | TBD | TBD | Proposed | TBD | TBD |
