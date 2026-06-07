# Home Screen HTML Reference Mapping

## Updated runtime mapping

| HTML element | Android runtime | Status |
| --- | --- | --- |
| Home/Core screen | `PrankstarStableHomeWebViewScreen` | Default `home` route |
| HTML bundle | `file:///android_asset/prankstar/prankstar_new_home_bot_screen.html` | Loaded in WebView |
| Header video | `assets/prankstar_header.mp4` | Preserved in HTML bundle |
| Reactor videos | `assets/reactor1.mp4` to `assets/reactor7.mp4` | Preserved in HTML bundle |
| Bot videos | `assets/bot/high.mp4`, `scanning2.mp4`, `powerup2.mp4`, `dancing.mp4`, `celebrate2.mp4` | Preserved in HTML bundle |
| Deploy action | `PrankstarWebBridge.deployRandom()` | Real catalog sound playback |
| Stop action | `PrankstarWebBridge.stopAll()` | Stops Android audio |
| Stash button | `PrankstarWebBridge.openStash()` | Navigates to native Library |
| Jokes button | `PrankstarWebBridge.openJokes()` | Navigates to native Voice Lab |
| Forge button | `PrankstarWebBridge.openForge()` | Navigates to native Forge |
| System button | `PrankstarWebBridge.openSystem()` | Navigates to native Settings/System |

## Notes
- The HTML keeps its own animation/state behavior.
- The native app still owns Library, Voice Lab, Forge, and System screens.
- The global bottom dock is hidden on Home to avoid duplicate dock UI.
