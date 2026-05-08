<div align="center">
  <img src="./prankstar_sn3.png" alt="Prankster Lab v2 banner" width="100%" />
</div>

# Prankster Lab v2

**Prankster Lab v2** is a premium, playful prank soundboard and audio utility designed for fast-access sound playback, organized sound discovery, and polished prank-centric interactions. Built with a modern Android-first foundation and supporting web/tooling workflows, the project focuses on a high-quality user experience with an expandable sound ecosystem.

---

## What this project is

Prankster Lab v2 is a curated audio experience for prank-style sound triggering and sound library management. It combines:

- fast sound playback
- categorized sound browsing
- prank-focused audio packs
- voice and meme-style sound assets
- randomization and sequencing foundations
- validation tooling for audio assets
- a premium visual direction with cyberpunk-inspired presentation

The repository currently reflects an active product-in-progress with a strong foundation and clearly defined next-stage features.

---

## Key features

### Soundboard experience
- Category-based sound browsing
- Quick-tap sound triggering
- Sound previews with clear labels
- Loopable and non-loopable playback support
- Prank-style tagging for discoverability
- Intensity and safety metadata for sound organization

### Audio library
- Large catalog of prank sounds across multiple categories
- Support for varied asset formats including `.ogg` and `.mp3`
- Catalog-driven sound metadata
- Organized asset paths under the app asset root
- Validation-oriented structure to support audio integrity checks

### Planned / advanced capabilities
Based on the project documentation and task lists, the app is intended to include or expand into:

- randomizer mode
- scheduled playback
- playback stop handling
- sequence builder
- waveform previews
- DSP / sound forge effects
- reactive “reactor core” visuals
- haptic-driven interactions
- generated sound management
- procedural voice generation
- offline playback support

### Tooling and quality checks
- automated sound asset validation
- sound catalog validation
- build verification workflows
- release readiness documentation
- dependency audit awareness

---

## Highlighted sound categories

The repository’s audio catalog and planning docs show a broad prank library organized into themed categories such as:

- Funny
- Creepy Lite
- Office
- Robot
- Cartoon
- Animal
- Glitch
- Sci-Fi
- Voice / Fighter-style voice lines
- Miscellaneous prank audio

These categories are designed to make the app feel fun, structured, and easy to explore.

---

## Product direction

The project documentation describes Prankster Lab as a **professional prank soundboard and prankster utility app** with a polished identity and room for expansion into a more advanced audio studio experience.

Primary direction includes:

- immersive UI polish
- stable app architecture
- offline-first playback behavior
- safe audio sourcing and validation
- category-rich sound collection
- advanced interaction layers

---

## Technology stack

### Primary stack
- Kotlin
- Jetpack Compose
- Android MediaPlayer
- DataStore
- Gradle Kotlin DSL

### Supporting web/tooling stack
- React
- Vite
- TypeScript
- Tailwind CSS
- Express
- Node.js tooling
- Gemini / TTS related packages

---

## Repository structure

Common top-level areas include:

- `app/` — Android application source
- `src/` — shared web/tooling source and catalog data
- `public/` — static web assets
- `docs/` — implementation notes, inspection reports, and task lists
- `tools/` — validation and maintenance scripts
- `prankster_*` image and media assets

---

## Available scripts

From `package.json`:

- `npm run dev` — start the local Vite dev server
- `npm run build` — build the web app
- `npm run preview` — preview the production build
- `npm run clean` — remove build output
- `npm run lint` — run TypeScript checks
- `npm run validate:sounds` — validate sound assets

---

## Local development

### Requirements
- Node.js
- Android build tooling for the mobile app portion

### Web/tooling workflow
```bash
npm install
npm run dev
```

### Android validation workflow
```bash
./gradlew clean assembleDebug --stacktrace
```

Additional validation commands referenced in the docs include audio catalog and sound asset checks.

---

## Audio asset philosophy

This project places a strong emphasis on safe and compliant audio sourcing:

- use royalty-free or fully owned audio
- avoid copyrighted clips, TV/movie/audio ripoffs, or meme rips
- avoid panic-inducing emergency tones and similar sensitive sounds
- normalize and trim audio carefully for consistent playback
- keep filenames clean and predictable

This makes the project more maintainable and more suitable for long-term distribution.

---

## Current project status

The documentation suggests the project is **not yet release-ready for production store submission**. The current focus areas are:

- manual device QA
- dependency audit remediation
- build/toolchain cleanup
- improved audio library integrity
- completion of advanced randomizer and sequence features
- upgraded visual/audio interaction systems

---

## Roadmap highlights

Planned next steps documented in the repo include:

1. Repair build system
2. Repair Android audio library
3. Implement true audio validator
4. Complete randomizer
5. Rebuild sequence builder
6. Upgrade reactor core visuals
7. Upgrade sound forge / waveform features

---

## Why users will enjoy it

Prankster Lab v2 is designed to be:

- fun to browse
- quick to use
- visually polished
- organized by theme
- expandable for advanced audio play
- suitable for playful interactions and prank-style moments

---

## Contributing / extending

If you continue growing the project, a strong next focus would be:

- refining the README with screenshots and feature demos
- adding release badges and build status
- documenting the sound catalog format
- adding a more detailed setup guide for Android and web tooling
- listing the current feature set vs. planned roadmap

---

## License

No license file is currently present in the repository. Add one if you plan to distribute the project publicly.

---

## Acknowledgements

Built as part of the Prankster Lab v2 project, with a strong emphasis on playful audio design, clean asset organization, and a premium product feel.
