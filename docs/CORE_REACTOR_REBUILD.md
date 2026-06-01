# CORE_REACTOR_REBUILD.md

Last updated: 2026-06-01

## Summary

The Prankster Core Reactor has been rebuilt and greatly enhanced as a native
Jetpack Compose interactive component while preserving every existing function
from the original ReactorCorePanel.kt.

---

## Files Changed

| File | Action | Notes |
|---|---|---|
| `components/reactor/ReactorUiState.kt` | **REPLACED** | Extended with all new fields; ReactorState enum and ReactorMode typealias retained |
| `components/reactor/PranksterCoreReactor.kt` | **NEW** | Premium animated reactor composable; canonical implementation |
| `components/reactor/ReactorCorePanel.kt` | **REPLACED** | Now a thin delegation wrapper; forwards all original params to PranksterCoreReactor |
| `screens/HomeScreen.kt` | **UPDATED** | Passes `onOpenStash`, `onOpenJokes`, `onOpenForge` to ReactorCorePanel; all other items preserved |

Files **not touched** (preserved exactly):
- `components/reactor/NeonControlPanel.kt` — unchanged
- `core/audio/AudioPlayerController.kt` — unchanged
- `core/repository/SoundRepository.kt` — unchanged
- `core/model/PrankSound.kt` — unchanged
- `assets/sound_catalog.json` — unchanged
- `drawable/prankstar_core.png` — unchanged
- All other screens, components, theme, and build files — unchanged

---

## Original Reactor Functions Preserved

1. **Tap reactor to trigger random sound** — preserved via `onCoreTap` → `onTrigger(category, 1)`
2. **Tapping while PLAYING stops playback** — preserved; tap routes to `onStopAll()` when state == PLAYING
3. **Stop All / Kill Audio** — preserved; KillAudioPanel and ReactorActionStrip both call `onStopAll`
4. **Playing state accuracy** — preserved via `AudioPlaybackState.isPlaying` flow
5. **Current sound label updates** — preserved via `currentSoundName` / `lastSoundName`
6. **Random play uses real catalog sounds** — preserved; `triggerReactor()` in HomeScreen unchanged
7. **Safe random mode respected** — preserved; `isSafeForRandomMode` filter unchanged
8. **Invalid/corrupt sounds skipped** — preserved; `isSoundPlayable()` + `markInvalid()` still active
9. **Playback errors shown visually** — preserved; ERROR state + FAULT strip in info readout
10. **Sound count / safe count shown** — preserved in ReactorStatusBar pills (LOADED / SAFE)
11. **Category selection ring** — preserved; icon orbit + arc ring + swipe gesture
12. **Long-press charge** — preserved; full charge coroutine with haptic ticks and `onCoreLongPressEnd`
13. **Haptic feedback** — preserved on tap, long-press, category select, stop
14. **Reactor image `prankstar_core.png`** — preserved; centered, circular clip, breath scale
15. **Core/Home screen navigation shortcuts** — preserved; ModeGridSection unchanged
16. **Prankstar Bot mood integration** — preserved; HomeScreen botMood logic unchanged
17. **No sound catalog regression** — confirmed; zero catalog changes

---

## New / Enhanced Reactor Functions

1. **`PranksterCoreReactor` composable** — canonical implementation, delegates ReactorCorePanel
2. **`ReactorUiState`** — extended with `generatedSoundCount`, `favoriteSoundCount`, `playbackProgress`, `isMuted`, `lastAction`, `recentSounds`, `isSafeMode`, `selectedCategory`
3. **Animated waveform halo strip** — 14-bar animated waveform visible during PLAYING/GENERATING, driven by sine phase
4. **Charge arc overlay** — circular progress arc around inner ring during CHARGING, shows integer percent
5. **ReactorActionStrip** — built-in Stop All / Stash / Jokes / Forge buttons below reactor, wired to navigation
6. **SAFE mode badge** — rendered in category label row when `isSafeMode == true`
7. **Waveform concentric rings** — 3 concentric play rings animate during PLAYING/GENERATING
8. **Warning state** — new `ReactorState.WARNING` with orange accent; distinct from ERROR
9. **Generating state** — new `ReactorState.GENERATING` with cyan accent; used for future Voice Lab preview integration
10. **Disabled state** — `ReactorState.DISABLED` renders greyed-out reactor, safe for conditional disable
11. **Semantic content descriptions** — added on reactor box, action buttons, category orbit nodes
12. **Charge text overlay** — percent shown inside core node during CHARGING (vs. just a progress bar)

---

## Visual Layer Implementation

| Layer | Description |
|---|---|
| 1 | HUD corner brackets — Canvas, fade with `glowAlpha` |
| 2 | Outer radial glow halo — `haloPulse` scale, concentric rings during PLAYING |
| 3 | Rotating tick ring — 72 ticks, major every 6, speed varies with state |
| 4 | Radar sweep beam — sweepGradient arc, rotates continuously |
| 5 | Category arc ring — counter-rotates at 0.4× speed, selected segment bright |
| 6 | Category icon orbit — 6 BoxComposables placed via cos/sin, tappable |
| 7 | Inner 40-segment energy ring — counter-rotates, fills with charge or playback |
| 8 | Central core node — circular clip housing for prankstar_core.png + overlays |
| 8a | HUD crosshair backplate — faint lines + circle behind artwork |
| 8b | prankstar_core.png — circular clip, breath scale, alpha by state |
| 8c | Colour wash overlay — radial gradient + centre bloom during activity |
| 8d | State affordance overlay — Stop icon (PLAYING), charge percent (CHARGING), status icon (else) |
| 9 | Charge arc overlay — circular progress arc during CHARGING |

---

## Interaction Model

| Gesture | Behaviour |
|---|---|
| Tap (not PLAYING) | Triggers `onCoreTap` → random sound from selected category |
| Tap (PLAYING) | Calls `onStopAll` → stops all audio |
| Long press (IDLE/ARMED) | Starts coroutine, fills `chargeLevel` 0→1 over 1300 ms with haptic ticks |
| Release during charge > 40% | `onCoreLongPressEnd(true)` → charged trigger (intensity 2) |
| Release during charge = 100% | `onCoreLongPressEnd(true)` → max charge trigger (intensity 3) |
| Release during charge ≤ 40% | `onCoreLongPressEnd(false)` → charge cancelled |
| Swipe left/right on reactor | Changes selected category (wraps around) |
| Tap category icon | Selects that category, haptic tick |
| Tap STOP ALL button | `onStopAll()`, stronger haptic |
| Tap STASH button | `onOpenStash()` → `onNavigate("library")` |
| Tap JOKES button | `onOpenJokes()` → `onNavigate("voice_lab")` |
| Tap FORGE button | `onOpenForge()` → `onNavigate("forge")` |

---

## Audio / Random Selection Behaviour

- Uses existing `AudioPlayerController.playPrankSound()` — no second controller
- Uses existing `SoundRepository.isSoundPlayable()` for validity checks
- Random selection in HomeScreen `triggerReactor()` — unchanged logic:
  - Filter by `isSafeForRandomMode` (safe pool first)
  - Filter by category or generated-voice flag
  - Filter by `intensityLevel >= intensity` for charged shots
  - Fall back to any playable sound in category if safe pool empty
  - Log and set `playbackError` if no candidates found

---

## Category / Safe Mode Behaviour

- Safe mode badge shown when `isSafeMode == true` (always true in current HomeScreen)
- Category ring: 6 segments (FUNNY, CREEPY, ANIMAL, VOICE, FIGHTER, CARTOON)
- Selected segment has full-brightness arc and filled icon background
- Category colour drives all accent colours throughout reactor
- Swipe gesture wraps around category list
- If category has zero playable sounds, error state fires and trace log records it — no crash

---

## Prankstar Bot Integration

- HomeScreen `botMood` derivation is **unchanged**
- Mood mapping:
  - WAKEUP (first 1.6 s after launch)
  - ERROR (playbackError set or AudioPlaybackState.lastError non-null)
  - PLAYING (isPlaying)
  - THINKING (soundsList empty)
  - ARMED (default ready state)
- Bot placed above reactor, compact mode, message driven by current state
- Bot does not overlap reactor or action strip

---

## Settings / Animation Behaviour

- Animation drivers use `rememberInfiniteTransition`; no objects allocated per frame
- Rotation duration hot-switches when `ReactorState` changes
- Pulse duration hot-switches identically
- No object allocation inside Canvas draw loops
- REDUCED / MINIMAL animation intensity hook: add a guard on `uiState.isSafeMode`
  or a separate animationIntensity parameter to conditionally skip waveform/halo layers

---

## Known Limitations

1. `animationIntensity` setting from `SoundRepository.getAnimationIntensityFlow()` is not
   yet wired into PranksterCoreReactor — full/reduced/minimal animation switching requires
   passing the intensity value as a param and gating the heavier canvas layers behind it.
2. `favoriteSoundCount` and `generatedSoundCount` fields in ReactorUiState are populated as
   0 by the current ReactorCorePanel wrapper — HomeScreen would need to observe those counts
   from SoundRepository and pass them in to show them in the status pills.
3. Runtime QA still requires a device or emulator — no ADB-attached verification was possible.

---

## QA Checklist

- [ ] Build passes (`assembleDebug --stacktrace`)
- [ ] Catalog validator passes (369 entries, 0 missing)
- [ ] Tap reactor → real sound plays
- [ ] Current sound name updates in info strip
- [ ] Tap reactor while PLAYING → stops audio
- [ ] Long press → charge bar fills, haptics tick
- [ ] Release early → cancel, no crash
- [ ] Release at 100% → charged sound plays
- [ ] Tap category icon → category changes, colour changes
- [ ] Swipe left/right → category cycles
- [ ] Stop All button in action strip → audio stops
- [ ] Open Stash button → navigates to library
- [ ] Create Joke button → navigates to voice_lab
- [ ] Open Forge button → navigates to forge
- [ ] Inject a missing asset ID → ERROR state fires, red flash, trace log entry
- [ ] prankstar_core.png is centred and visible in all states
- [ ] Bottom dock is not covered by reactor content
- [ ] Bot panel visible above reactor, not overlapping controls
- [ ] All existing screens (Library, Forge, Voice Lab, Settings) unaffected
