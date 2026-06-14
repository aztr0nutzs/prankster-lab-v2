# Prankstar Development Workflow

## Feature Development Workflow

1. Read `PROJECT_CONTEXT.md`, `AI_RULES.md`, and `FEATURE_TRACKER.md`.
2. Select exactly one feature ID.
3. Define acceptance criteria and what must not change.
4. Inspect existing code, assets, routes, dependencies, and QA docs.
5. List exact files to modify before editing.
6. If additional files are needed, stop and expand scope before editing them.
7. Implement the smallest safe change.
8. Preserve public behavior and add fallbacks for missing assets or unsupported states.
9. Validate with targeted checks; verify Android environment before Gradle.
10. Update `FEATURE_TRACKER.md`, relevant docs, and `SESSION_HANDOFF.md`.
11. Commit a focused change with a clear message.

## Asset Integration Workflow

1. Read `ASSET_REGISTRY.md`, `PROJECT_CONTEXT.md`, and feature-specific docs.
2. Inspect exact source asset paths and metadata.
3. Decide correct Android destination: `assets`, `res/raw`, `res/drawable`, or `mipmap`.
4. Check for duplicate names, duplicate catalog IDs, and existing references.
5. Copy/rename assets using Android-safe names where required.
6. Wire references in code/catalog only after target assets exist.
7. Add safe fallback behavior for optional assets.
8. Update `ASSET_REGISTRY.md` with location, purpose, references, status, and notes.
9. Run validators for audio or resource checks where available.
10. Build after Android environment verification if Android resources/code changed.
11. Capture runtime/screenshot proof for visible assets when possible.

## Bug Fix Workflow

1. Read `BUG_TRACKER.md`, `AI_RULES.md`, and relevant feature docs.
2. Capture or quote key error lines, logs, screenshots, and reproduction steps.
3. Classify the failure: Gradle, AGP, Kotlin, JDK, dependency, manifest/resource, runtime, lifecycle, asset, UI, or data.
4. Reproduce if environment allows; otherwise inspect the likely code path.
5. Identify root cause and exact affected files.
6. Make the smallest safe fix.
7. Validate the fix and closest regression paths.
8. Update `BUG_TRACKER.md` with root cause, resolution, verification, and rollback.
9. Commit a focused bug-fix change.

## UI Revision Workflow

1. Read `UI_SPECIFICATION.md`, `PROJECT_CONTEXT.md`, and `AI_RULES.md`.
2. Identify exact screen/component and routes affected.
3. Inspect existing composables, state, events, navigation, assets, and screenshots.
4. Preserve custom neon/HUD styling, headers, dock, reactor, waveform, and glow systems.
5. List exact files to modify.
6. Implement focused UI changes only.
7. Include accessibility labels, touch targets, loading/error/empty states where relevant.
8. Respect reduced-animation preference for changed animations.
9. Validate compact width, navigation, primary interactions, and screenshots if possible.
10. Update trackers/specs/handoff.

## Release Workflow

1. Freeze feature scope.
2. Review `FEATURE_TRACKER.md`, `BUG_TRACKER.md`, `ASSET_REGISTRY.md`, and `SESSION_HANDOFF.md`.
3. Resolve or explicitly defer S0/S1 bugs.
4. Verify Android SDK environment before any Gradle build.
5. Run clean debug/release builds as appropriate.
6. Run audio catalog validation and asset checks.
7. Execute runtime smoke tests on device/emulator.
8. Verify UI, navigation, animation, audio, and regression checklists in `RELEASE_CHECKLIST.md`.
9. Review privacy/safety/store copy.
10. Record final artifact, commit, known issues, and rollback plan.

## AI Collaboration Workflow

1. Every AI session starts by reading root and nested `AGENTS.md` files.
2. Attempt upstream sync when a remote is configured.
3. Read `PROJECT_CONTEXT.md`, `AI_RULES.md`, and the task-specific docs.
4. Use one PR for one purpose.
5. Avoid conflict-magnet files unless explicitly scoped.
6. Before editing, list exact files to modify.
7. Keep changes small, reviewable, and isolated.
8. Do not duplicate another AI's active work; check `SESSION_HANDOFF.md`.
9. Update trackers and handoff at the end.
10. Commit focused changes and provide verification receipts.

## Context Recovery Workflow

Use this when an AI conversation resets or context is lost.

1. Run `git status --short --branch`.
2. Read `AGENTS.md` files relevant to the repo/path.
3. Read `SESSION_HANDOFF.md`.
4. Read `PROJECT_CONTEXT.md` and `AI_RULES.md`.
5. Read the tracker for the active work area.
6. Inspect recent commits and unstaged changes.
7. Reconstruct active file scope from handoff and git diff.
8. Continue only after confirming current task and validation state.

## Emergency Recovery Workflow

Use this when the project stops building, launch crashes, or core playback breaks.

1. Stop feature work immediately.
2. Preserve evidence: command output, logs, screenshots, current commit.
3. Run `git status --short --branch`.
4. Identify the last known good commit/build report.
5. Classify failure type: build/tooling/resource/runtime/asset/UI.
6. If build-related, inspect Gradle/AGP/Kotlin/JDK/dependency/manifest changes first.
7. If runtime-related, isolate screen/route and recent file changes.
8. If asset-related, verify exact paths, resource names, catalog entries, and file validity.
9. Revert the smallest suspect change or create a focused fix.
10. Validate and update `BUG_TRACKER.md` and `SESSION_HANDOFF.md`.
