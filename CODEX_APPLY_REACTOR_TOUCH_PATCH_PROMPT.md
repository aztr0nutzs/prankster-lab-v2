You are working inside the PrankStar Android project.

TASK:
Apply the updated PrankStar HTML/assets package from this zip.

Copy the folder contents exactly into the project, preserving paths. The primary file is:

app/src/main/assets/prankstar/prankstar_new_home_bot_screen.html

Do not redesign the app. Do not replace the dock, header, bot screen, audio logic, reactor videos, or existing panels. This patch only improves reactor touch interaction, mapped overlay controls, label legibility, visual response, and hold/drag/tap feedback.

VERIFY:
1. The WebView opens file:///android_asset/prankstar/prankstar_new_home_bot_screen.html.
2. All reactor selector buttons r1-r7 remain visible and readable.
3. Tapping, holding, and dragging over each reactor produces visual feedback.
4. Existing bot screen and corrected reactor2.mp4 still work.
5. Build the app and report changed files.
