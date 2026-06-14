# Prankstar UI Specification

## Design Principles

Prankstar must look like a premium cyberpunk prank command system. UI changes must preserve dark layered surfaces, neon accents, custom headers, waveform/glow systems, animated reactor visuals, and premium dock navigation.

## Header Requirements

| Requirement | Acceptance Criteria |
|---|---|
| Branded header present where specified | Each primary screen has either the approved Prankstar header, feature-specific header, or documented intentional omission. |
| Aspect ratio preserved | Baked text/art is not cropped on 360dp-wide and common large-screen widths. |
| Readability | Header content remains readable over backgrounds and adjacent panels. |
| Fallback | Missing optional image/video falls back to a safe branded UI without crashing. |
| Accessibility | Decorative headers are hidden from accessibility or named appropriately if informative. |

## Dock Requirements

| Requirement | Acceptance Criteria |
|---|---|
| Custom dock retained | Do not replace with generic Material tabs/navigation bar. |
| Single dock instance | No duplicate bottom dock on any route. |
| Active state visible | Current route is visually distinguishable. |
| Touch targets | Primary dock controls provide approximately 48dp minimum touch targets. |
| Route stability | Dock buttons navigate to intended screens without stack loops or crashes. |
| Safe area | Dock does not block important content or system gestures. |

## Button Requirements

| Requirement | Acceptance Criteria |
|---|---|
| Style consistency | Buttons use neon/HUD styling on premium screens. |
| State clarity | Default, pressed, disabled, loading, selected, and active states are distinguishable. |
| Accessibility | Interactive controls have meaningful labels/content descriptions. |
| Hit target | Controls are at least 48dp where practical; smaller visual elements use larger clickable containers. |
| No silent failure | Button actions either complete, show progress, show error, or are disabled with explanation. |

## Reactor Display Requirements

| Requirement | Acceptance Criteria |
|---|---|
| Focal layout | Reactor is prominent on Home/Core and not visually demoted. |
| Playback state | Idle, charging, playing, stopped, and disabled states are visually distinct. |
| Category state | Selected category/mode is visible and persistent where applicable. |
| Touch feedback | Tap/drag/category interactions provide immediate visual feedback. |
| Responsive sizing | Reactor remains usable on 360dp-wide screens and larger devices. |
| Non-overlap | Bot, header, dock, and action strip do not cover the reactor's primary target. |

## Animation Requirements

| Requirement | Acceptance Criteria |
|---|---|
| Purposeful motion | Animations communicate state or reinforce style; no distracting random motion. |
| Reduced animation | New/modified animation paths honor the reduced-animation preference. |
| Performance | No obvious jank during idle, playback, navigation, or scroll. |
| Lifecycle safety | Video/animation resources pause/release when composables leave composition where applicable. |
| No layout jump | State transitions do not unexpectedly move primary controls. |

## Typography Requirements

| Requirement | Acceptance Criteria |
|---|---|
| Hierarchy | Screen title, section title, body, metadata, and control labels are distinct. |
| Contrast | Text meets readable contrast against dark/video backgrounds. |
| Size | Primary controls and body text remain readable on small phones. |
| Baked text | Header images containing text must not be cropped or obscured. |
| Consistency | Use existing typography patterns unless the task explicitly updates the design system. |

## Layout Requirements

| Requirement | Acceptance Criteria |
|---|---|
| 360dp support | Primary flows are usable on a 360dp-wide device. |
| Scroll safety | Content scrolls when vertical space is limited; dock/header do not trap content. |
| Visual grouping | Related controls are grouped in panels/cards with clear hierarchy. |
| Empty/error/loading states | Lists, generators, and asset-dependent screens show clear states. |
| No unrelated reflows | UI changes should not alter unrelated screens without documented need. |

## Responsive Behavior

| Screen Width | Expected Behavior |
|---|---|
| Compact phone | Prioritize header, reactor/core action, active controls, and scrollable secondary panels. |
| Large phone | Use richer spacing while preserving same hierarchy. |
| Tablet/foldable | Avoid excessive stretching; center core content and use panels where practical. |
| Landscape | If supported, preserve controls and avoid clipped dock/header. If not supported, fail gracefully. |

## Accessibility Considerations

- Provide content descriptions for meaningful images and controls.
- Mark decorative visuals as decorative where possible.
- Keep touch targets usable.
- Ensure stop controls are accessible while audio is playing.
- Avoid conveying critical state by color alone.
- Support reduced animation preference for new motion.
- Ensure text input fields expose labels, errors, and hints.

## UI Acceptance Criteria Template

| Criteria | Required Evidence | Result |
|---|---|---|
| Primary route renders | Screenshot or manual QA note. | TBD |
| Header readable | Screenshot at compact width. | TBD |
| Dock active state correct | Screenshot or navigation test. | TBD |
| Reactor/control interaction works | Runtime note/log. | TBD |
| Accessibility labels checked | Code review or UI test. | TBD |
| Reduced animation considered | Code path or documented non-impact. | TBD |
