You are working inside the PrankStar Android project.

TASK:
Apply the PrankStar Bot Screen patch exactly.

STRICT PRESERVATION:
- Do not redesign the existing PrankStar UI.
- Do not remove existing reactor controls, docks, animations, sounds, or navigation.
- Do not replace the main reactor screen.
- Do not remove prankstar_header.mp4.
- Preserve the dark neon PrankStar visual identity: black background, aggressive italic lettering, neon lime/cyan/orange/pink accents.

FILES TO COPY:
Copy the included app/ folder into the project root so these files exist:

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

WEBVIEW TARGET:
Update the PrankStar home WebView URL to:
file:///android_asset/prankstar/prankstar_new_home_bot_screen.html

ACCEPTANCE CHECKS:
- Top header video loops smoothly.
- Existing reactor screen still loads.
- Reactor 2 uses the corrected external assets/reactor2.mp4.
- BOT dock button opens the new robot/avatar screen.
- Robot idle video loops continuously.
- SCAN, POWER, DANCE, PARTY buttons play their action MP4s and return to idle.
- Touch zones on robot head/chest/arms/feet trigger mapped actions.
- No placeholder UI, no fake broken buttons, no removed app functions.
