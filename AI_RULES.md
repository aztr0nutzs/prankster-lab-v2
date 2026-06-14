# Mandatory AI Rules for Prankstar

Every AI assistant working on Prankstar must follow these rules.

## Project Safety Rules

1. Never remove existing functionality without verification and explicit scope.
2. Never assume assets exist; inspect exact paths first.
3. Never assume layouts, routes, composables, resources, or catalogs exist.
4. Always inspect dependencies and build tooling before changing code that relies on them.
5. Always identify affected files before editing.
6. Always verify build impact for code/resource changes.
7. Always preserve backward compatibility unless a migration is explicitly approved.
8. Always provide implementation validation steps.
9. Always provide rollback instructions for code, asset, UI, and build changes.
10. Never fake playback, fake catalog entries, or fake validation results.

## Android Build Rules

1. Do not run Gradle blindly.
2. First run `chmod +x scripts/build-android-debug.sh && ./scripts/build-android-debug.sh` when a Gradle build is needed.
3. Confirm `local.properties` has a valid `sdk.dir`.
4. Confirm `ANDROID_HOME` or `ANDROID_SDK_ROOT` points to a real SDK directory.
5. Confirm SDK has `platforms/`, `platform-tools/`, and `build-tools/`.
6. Only then run additional Gradle commands.
7. Build-system changes must be isolated in their own PR.

## UI Preservation Rules

Do not:

- Replace custom UI with generic Material layouts.
- Flatten layered surfaces.
- Remove waveform systems.
- Remove glow systems.
- Remove reactor interactions.
- Replace custom navigation with default tabs.
- Downgrade animated interfaces into plain columns.
- Replace premium cards with plain lists.
- Remove neon styling.
- Reduce visual hierarchy.

Always preserve:

- Dark cyberpunk aesthetic.
- Neon edge lighting.
- Layered surfaces.
- Animated transitions.
- Waveform headers.
- Premium dock navigation.
- Glowing category chips.
- HUD-inspired composition.
- Animated reactor core.
- Rich interaction feedback.

## Asset Rules

1. Register new or newly wired assets in `ASSET_REGISTRY.md`.
2. Verify exact package/resource path before referencing.
3. Do not overwrite existing assets without approval.
4. Do not remove duplicate-looking assets without reference audit.
5. Validate sound headers, duration, duplicates, and catalog references.
6. Provide fallback behavior for optional images/videos.
7. Keep Android resource names lowercase and safe when using `res/`.

## Audio Rules

1. Use real playback only.
2. Preserve stop support and active state tracking.
3. Validate corrupt/missing file handling.
4. Do not add placeholder audio as if it were real content.
5. Do not silently ignore playback failure.
6. Ensure media resources are released safely.

## Reactor Rules

1. Reactor remains central on Home/Core.
2. Reactor tap/deploy/stop behavior must remain testable.
3. Category or mode changes must be visible to the user.
4. Missing reactor assets must not crash the app.
5. Action strip/dock navigation must stay consistent.
6. Animations must be lifecycle-safe and reduced-animation aware where modified.

## Bot Rules

1. Bot must remain safe by default.
2. Bot must not claim cloud AI behavior unless actually implemented.
3. Bot must use real sounds/routes/actions only.
4. Unsafe requests must be refused with a safe alternative.
5. Mood/video assets must have fallbacks.

## Collaboration Rules

1. One PR equals one purpose.
2. Avoid conflict-magnet files unless explicitly scoped.
3. No wholesale rewrites.
4. No broad reformatting.
5. Update trackers and `SESSION_HANDOFF.md` before ending work.
6. Commit changes on the current branch when work is complete.
7. Provide exact verification commands and results.
