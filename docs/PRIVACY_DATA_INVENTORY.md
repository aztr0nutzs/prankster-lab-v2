# Privacy Data Inventory

Last updated: 2026-06-10

## Summary

Prankstar is currently designed around local playback, local generated clips, local settings, and optional future backend voice generation. This inventory documents current and planned data categories for privacy policy and Play Console Data safety preparation.

## Data Categories

| Data | Current handling | Sent off device | Stored on device | Notes |
| --- | --- | --- | --- | --- |
| Bundled sound playback | Local app assets | No | No user-specific playback storage except app state/recent items | Core soundboard playback is offline. |
| Generated text | User-entered or locally generated in Voice Lab/Bot/Twak-Attacks | No by default | May be stored in generated clip metadata if saved | Users control Generate and Save actions. |
| Generated audio | Created locally by Android TTS or Sound Forge | No by default | Yes, when saved to Sound Stash | Stored in app-private storage and metadata DataStore. |
| Local settings | Volume, safety acknowledgment, animation and bot toggles, favorites, custom sound metadata | No | Yes | Stored with Android DataStore preferences. |
| ElevenLabs/backend text processing | Planned for premium British narrator generation | Yes, only when configured and user requests premium generation | Resulting audio may be saved locally | Production must route through backend; Android must not include provider secrets. |
| Purchases and entitlements | Planned | Yes, through Google Play and backend verification | Cached display state may be stored later | Backend should be source of truth. |
| Voice credits | Planned | Yes, backend ledger | Cached display state may be stored later | Credits must not be granted solely from client state. |
| Crash logs | Not currently documented as integrated | If added later | Provider-dependent | Update policy and Play Data safety before adding. |
| Analytics | Not currently documented as integrated | If added later | Provider-dependent | Update policy and Play Data safety before adding. |

## Deletion Controls

Current in-app deletion controls in Settings include:

- Clear recent sounds.
- Clear favorites.
- Delete generated voice clips.
- Clear missing generated voice entries.
- Reset Sound Forge presets.

Generated audio files saved inside app-private storage can also be removed when the app is uninstalled or app storage is cleared by Android.

## Future Backend Requirements

Before enabling production premium narration or purchases:

- Publish a privacy policy URL.
- Document backend retention periods.
- Document payment/entitlement verification data.
- Document voice provider text/audio processing.
- Add account deletion or support contact instructions if accounts are introduced.
- Update Play Console Data safety answers after integrations are final.
