# Play Console Submission Checklist

Last updated: 2026-06-10

## App Access

- App does not require login for core soundboard, Voice Lab, Sound Stash, Forge, or Settings.
- If backend accounts are added later for Pro or voice credits, provide Play review test credentials.
- Confirm no reviewer needs external hardware beyond a normal Android device.

## Ads

- Current status: no ads documented as integrated.
- Play Console answer draft: Ads = No.
- If ads are added later, update app disclosures, Data safety, Families/target audience answers, and Pro "no ads" entitlement behavior.

## Content Rating Notes

- App category: entertainment / audio tools.
- Content style: prank soundboard, voice meme studio, generated comedy clips.
- Safety posture: harmless comedy only.
- Do not market or configure content around threats, harassment, emergency hoaxes, official impersonation, illegal activity, or targeting strangers.
- Generated text/audio features include safety warnings and should reject restricted emergency/official impersonation prompts where possible.

## Target Audience

- Recommended target audience: teens and adults.
- Not positioned for children.
- Avoid store metadata that encourages unsafe pranks, public nuisance, emergency impersonation, or harassment.

## Data Safety Draft

Current core app:

- Collects personal info: No, unless future account/auth is added.
- Shares personal info: No.
- Collects audio files: Generated audio is stored locally by user action; not sent off device by default.
- Collects app activity: No analytics documented.
- Crash logs: Not documented as integrated.
- Purchases: Not yet enabled; future purchases will use Google Play and backend verification.

Future premium voice generation:

- User-provided text may be sent to backend/voice provider when the user requests premium narration.
- Purchase tokens and entitlement state may be processed for billing verification.
- Update Data safety before enabling these integrations.

## Privacy Policy URL

- Required before production launch.
- Replace draft contact and hosting placeholders.
- Confirm policy covers local generated clips, backend voice processing, purchases/entitlements, deletion controls, and support contact.

## Closed/Internal Testing Plan

- Start with internal testing for staff/device smoke tests.
- Expand to closed testing after build, sound validation, and core workflow checks pass.
- Collect tester feedback through a structured bug report template.
- Review device coverage before open or production rollout.

## Pre-Launch Report Review

- Upload an internal or closed test artifact.
- Review crashes, ANRs, startup issues, rendering screenshots, accessibility warnings, and security findings.
- Re-test any issue that affects boot, playback, Voice Lab, Twak-Attacks, save-to-Stash, Bot AI, Forge, or Settings.

## Android Vitals Review

- Monitor crash rate, ANR rate, startup time, excessive wakeups, slow rendering, and bad behavior by device model.
- Treat MediaPlayer playback crashes and generated-file save failures as launch blockers.

## Final Submission Gates

- Debug/internal build passes Gradle and validators.
- Release signing configured when preparing production artifact.
- Privacy policy URL live.
- Store listing avoids unsafe prank claims and creator/channel references.
- Data safety answers match the actual build.
- Content rating questionnaire completed consistently with harmless comedy positioning.
- Manual runtime QA completed on real device or emulator.
