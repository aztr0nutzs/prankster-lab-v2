# Remaining Blockers

## Runtime QA

Manual device/emulator QA depends on `adb` availability. If `adb` is not available in the execution environment, screenshots and logcat should not be faked.

Recommended manual checks when a device is available:

1. Bot visible on Home.
2. Type `find creepy sounds`.
3. Verify real recommendations appear.
4. Tap Play on a recommendation.
5. Verify sound plays.
6. Type `make a joke about being late`.
7. Verify safe generated text appears.
8. Tap Send to Voice Lab.
9. Verify Voice Lab receives text and preset when available.
10. Tap Stop All.
11. Verify playback stops.
12. Type an unsafe emergency impersonation request.
13. Verify refusal.

## Known limitations

- Parser is deterministic and keyword-based; it is not a cloud LLM.
- Library screen natural-language filtering is not deeply embedded yet; Home bot can recommend and navigate to Sound Stash.
- Prank plans are displayed but not runnable. This avoids hidden or destructive automation.
- Voice Lab prefill uses an in-memory app bridge. It works during the same app process; persistent cross-process drafts are future work.
- Suggested voice preset selection only applies when the suggested preset ID exists in `VoicePresetLibrary`.

## Future work

- Add an interface for optional cloud LLM routing after local safety checks.
- Add Library-scoped assistant search chips that do not clutter every sound card.
- Add plan review/run UX with explicit per-step user confirmation.
- Add richer bot memory through DataStore if product requires process-persistent drafts.
