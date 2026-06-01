# STASH_LIBRARY_STATUS.md

Last updated: 2026-06-01

## Current Role

The Library is branded as **Sound Stash**. It remains a major pillar alongside Jokes/Voice Lab.

## Implemented

- The bundled `sound_catalog.json` was not modified.
- The catalog still contains 369 bundled prank sounds.
- Search remains available across name, category, tags, and pack ID.
- Favorites remain available.
- Category and pack filters remain available.
- New top-level filters are exposed when browsing Stash:
  - All
  - Bundled
  - Generated
  - Voice Lab
  - Forge
  - Favorites
  - Categories from real catalog metadata
- Generated Voice Lab metadata is rendered when available, including preset and source text preview.
- Generated clip created date is displayed.
- Stash item action that previously pointed toward Sequencer now opens Jokes.
- Timer shortcut remains secondary and uses the existing pending sound handoff.

## Generated Sound Model

Voice Lab clips save as generated/custom sounds with category `VOICE_GENERATED` and `packId = voice_lab`.

Sound Forge clips save as generated/custom sounds with category `FORGE_GENERATED` and `packId = sound_forge`.

## Remaining Runtime Checks

- Confirm Stash loads without crash on device/emulator.
- Confirm five bundled sounds play from Stash.
- Confirm generated Voice Lab and Forge clips appear in Stash after save.
- Confirm search and filters include generated clips without duplicates.
