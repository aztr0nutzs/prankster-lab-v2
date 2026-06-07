# PrankStar Stable V9 Surgical Patch

This patch is based on the original full uploaded `prankstar_new_home_bot_screen.html` and preserves the original embedded/base64 reactor payloads, menus, dock panels, bot screen, JavaScript functions, and Android bridge calls.

## Fixes
- Header forced visible and enlarged.
- All original panels/menus remain in DOM and are forced open/closed reliably by a JS safety patch.
- Reactor videos forced visible without `mix-blend-mode`.
- Visible touch rings and zone labels removed while preserving clickable zones.
- Reactor video circular clipping removed so black MP4 edges blend into the black screen instead of forming a ring.
- Bottom dock restyled as angular command tabs, not rounded/glowing panel buttons.
- Button inner glow/text glow removed.
- External assets included for header, reactors 1-7, and bot videos.

## Apply
Copy `app/src/main/assets/prankstar/` into the same path in the Android project.

The WebView URL should stay:
`file:///android_asset/prankstar/prankstar_new_home_bot_screen.html`

Look for stamp: `STABLE V9 SURGICAL`.
