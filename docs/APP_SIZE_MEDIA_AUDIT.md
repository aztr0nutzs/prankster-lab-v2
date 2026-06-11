# App Size Media Audit

Last updated: 2026-06-11

## Artifact Sizes

| Artifact | Size |
| --- | ---: |
| `app/build/outputs/apk/debug/app-debug.apk` | 268,866,295 bytes |
| `app/build/outputs/apk/release/app-release-unsigned.apk` | 224,211,386 bytes |
| `app/build/outputs/bundle/release/app-release.aab` | 217,569,217 bytes |

## Media Totals

| Group | Size |
| --- | ---: |
| Stable V9 videos under `app/src/main/assets/prankstar/assets` | 77,899,394 bytes |
| Raw Prankstar Bot videos under `app/src/main/res/raw/prankstar_bot_*.mp4` | 43,197,568 bytes |
| Twak Bot videos under `app/src/main/res/raw/twakbot_*.mp4` | 13,315,703 bytes |
| Sound assets under `app/src/main/assets/sounds` | 34,856,070 bytes |
| All `app/src/main/assets` files | 119,291,706 bytes |
| All `app/src/main/res` files | 129,748,160 bytes |

## Largest Assets

Top asset and resource inventories were captured to:

- `qa/top_assets.csv`
- `qa/top_res.csv`

Largest individual files observed:

| File | Size |
| --- | ---: |
| `app/src/main/assets/prankstar/assets/prankstar_header.mp4` | 7,756,333 bytes |
| `app/src/main/assets/prankstar/assets/bot/high.mp4` | 7,229,286 bytes |
| `app/src/main/assets/prankstar/assets/prankstar_boot2.mp4` | 6,375,846 bytes |
| `app/src/main/res/raw/prankstar_header.mp4` | 7,756,333 bytes |
| `app/src/main/res/raw/prankstar_bot_happy.mp4` | 6,343,218 bytes |
| `app/src/main/res/raw/prankstar_bot_processing.mp4` | 4,908,189 bytes |
| `app/src/main/res/raw/twakbot_excited.mp4` | 3,204,039 bytes |
| `app/src/main/res/drawable/twak_attack_header.png` | 3,119,986 bytes |

## Risk

The media footprint is high for a single install-time Android package. The post-fix release unsigned APK is about 213.8 MiB and the release AAB is about 207.5 MiB. This can affect Play upload policy margins, install conversion, update reliability, lower-end storage availability, and media decoder coverage.

## Recommendations

- Transcode MP4 assets with target bitrates/resolutions matched to their on-screen size.
- Audit duplicate boot, reactor, launcher, and identity assets before compression or removal.
- Consider Play Asset Delivery for optional high-media packs.
- Consider optional download packs for non-core sound/media groups.
- Keep the core install under a defined target before closed testing.
- Do not delete or compress media without a separate approval pass and visual/runtime verification.

## Closure Status

Measured and documented. Size optimization remains open and should be completed before broad testing or production submission.
