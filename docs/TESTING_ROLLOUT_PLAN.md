# Testing Rollout Plan

Last updated: 2026-06-10

## Internal Test

Goal: verify installability, startup, core navigation, playback, and generated clip workflows before wider testing.

Participants:

- Project owner.
- 2-5 trusted internal testers.

Build:

- Debug or internal app sharing build.
- Release signing is not required for this prompt unless already configured.

## Closed Test

Goal: validate the app across more devices, Android versions, and usage styles before production.

Participants:

- 10-25 testers.
- Include at least one lower-end device and one recent flagship-class device.

Duration:

- At least one full test cycle after the internal test passes.
- Extend if crashes, playback failures, or save failures are reported.

## Tester Instructions

Ask testers to:

- Install the app fresh.
- Run through each critical workflow.
- Use normal volume in a private setting.
- Keep tests harmless and consensual.
- Report the exact screen, action, device, Android version, and whether the issue repeats.

## Bug Report Template

```text
Title:
Device model:
Android version:
App version/build:
Screen:
Steps to reproduce:
Expected result:
Actual result:
Can reproduce again? Yes/No:
Screenshot or screen recording:
Logs if available:
```

## Device Matrix

Minimum recommended matrix:

- Android 8.0 or 8.1 device/emulator, matching minSdk 26.
- Android 10 or 11 mid-range device.
- Android 12 or 13 device.
- Android 14 device.
- Small screen phone.
- Large screen phone.
- Device with limited storage.
- Device with TTS engine enabled.
- Device with TTS engine unavailable or disabled, if feasible.

## Critical Workflows

1. Boot
   - App launches from cold start.
   - Boot sequence finishes or can be skipped.
   - No black screen or crash.

2. Stable V9 Home
   - Home loads and remains responsive.
   - Dock navigation works.
   - Reactor visuals do not block navigation.

3. Sound Playback
   - Sound Stash opens.
   - Bundled sounds play audibly.
   - Stop controls stop active playback.
   - Random/safe playback does not crash.

4. Voice Lab
   - Local Android TTS initializes or shows a clear unavailable state.
   - User can enter text, generate, preview, stop preview, and save if within free limits.
   - Generated clips remain playable after navigation away and back.

5. Twak-Attacks
   - User can generate harmless mock-documentary text.
   - Advanced tones show Pro locked state when billing is not configured.
   - British Narrator shows configured/locked state honestly.
   - No premium generation occurs without entitlement and credits.

6. Save To Stash
   - Generated clip saves to Sound Stash.
   - Saved clip has a readable name.
   - Saved clip plays.
   - Delete generated clips removes local generated entries.

7. Bot AI
   - Bot searches real sounds.
   - Bot can stop playback.
   - Bot can draft safe joke text and send it to Voice Lab.
   - Unsafe requests are refused.
   - Premium planning actions show locked state when not entitled.

8. Forge
   - Generate procedural audio.
   - Preview generated audio.
   - Save generated audio where supported.
   - Safety guardrails block restricted patterns.

9. Settings
   - Volume slider works.
   - Safety acknowledgment persists.
   - Animation and bot toggles persist.
   - Data controls do not crash.
   - Audio diagnostics refresh works.

## Pass Criteria

- No reproducible crash in critical workflows.
- No ANR during boot, playback, generation, save, or Settings.
- Bundled sound validation passes.
- Generated clips save and replay on at least two Android versions.
- Safety copy is visible in Voice Lab, Twak-Attacks, Bot AI, and Settings.
- Free core features remain usable without Pro entitlement.
- Premium features do not pretend to purchase or grant access.

## Fail Criteria

- App cannot boot on a supported Android version.
- Bundled playback is silent or crashes.
- Generated clip save corrupts metadata or creates unplayable files.
- Twak-Attacks or Bot AI encourages unsafe use.
- Premium narration can run in production without verified entitlement and credits.
- Settings deletion controls remove unrelated app data.
