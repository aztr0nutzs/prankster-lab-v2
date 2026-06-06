# PrankStar Bot Screen Patch

This patch adds a new interactive PrankStar robot/avatar screen while preserving the existing reactor screen style.

## Main file

`app/src/main/assets/prankstar/prankstar_new_home_bot_screen.html`

Load in Android WebView with:

`file:///android_asset/prankstar/prankstar_new_home_bot_screen.html`

## Added / changed

- Adds a mapped `BOT` dock button/tab.
- Adds a new full PrankStar robot screen using the same black/neon/aggressive italic style.
- Reuses `prankstar_header.mp4` as the smooth looping top header.
- Keeps the reactor screen intact and switches screens without destroying playback.
- Replaces `reactor2.mp4` by changing reactor 2 to use `assets/reactor2.mp4`.
- Adds interactive robot touch zones:
  - Head: scan action
  - Chest: power action
  - Left arm/body: dance action
  - Right arm/body: celebrate action
  - Feet/lower zone: idle loop restore
- Adds button controls: SCAN, POWER, DANCE, PARTY, IDLE.
- Robot action videos return to the idle loop automatically when finished.

## Bot video mapping

- Idle loop: `assets/bot/high.mp4`
- Scan action: `assets/bot/scanning2.mp4`
- Power action: `assets/bot/powerup2.mp4`
- Dance action: `assets/bot/dancing.mp4`
- Celebrate action: `assets/bot/celebrate2.mp4`

## Asset paths

Copy the included `app/` folder directly into the Android project root, or copy these files manually:

```text
app/src/main/assets/prankstar/prankstar_new_home_bot_screen.html
app/src/main/assets/prankstar/assets/prankstar_header.mp4
app/src/main/assets/prankstar/assets/reactor2.mp4
app/src/main/assets/prankstar/assets/reactor5.mp4
app/src/main/assets/prankstar/assets/reactor6.mp4
app/src/main/assets/prankstar/assets/reactor7.mp4
app/src/main/assets/prankstar/assets/bot/high.mp4
app/src/main/assets/prankstar/assets/bot/scanning2.mp4
app/src/main/assets/prankstar/assets/bot/powerup2.mp4
app/src/main/assets/prankstar/assets/bot/dancing.mp4
app/src/main/assets/prankstar/assets/bot/celebrate2.mp4
```
