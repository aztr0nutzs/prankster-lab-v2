# Voice Credits Architecture

Last updated: 2026-06-10

## Goal

Premium narration must be profitable by enforcing credits server-side before any ElevenLabs request is made. The Android app can display credit state, but it must not be trusted to enforce credit balance, entitlement, safety, or rate limits.

## Proposed Product Tiers

Initial values should be finalized against real ElevenLabs cost, conversion, refund, and retention data.

| Tier | Included premium narration credits | Notes |
| --- | ---: | --- |
| Free | 5 per month | Enough to sample Twak-Attacks without creating meaningful provider exposure. |
| Pro | 150 per month | Monthly allowance for paid users. Unused rollover should be a deliberate product decision. |
| Extra pack small | 25 one-time credits | Low-friction top-up. |
| Extra pack medium | 100 one-time credits | Better value for active users. |
| Extra pack large | 300 one-time credits | Highest value, monitor for abuse and sharing. |

One credit should map to one successful narration generation up to the feature text limit. If longer text or future voice models are added, price by character bucket or provider cost multiplier.

## Server-Side Enforcement

The backend must enforce:

- authenticated user identity
- active entitlement
- credit balance
- per-user request limits
- device/IP abuse limits
- text length and safety checks
- feature and voice allowlists
- idempotency so retries do not double charge

Credits should be decremented after ElevenLabs returns usable audio and storage succeeds. If the backend streams bytes directly, decrement only after validating non-empty audio bytes.

## Client Display

The Android app should display remaining credits after backend responses. Client values are advisory only. The server response is the source of truth.

Client states to support:

- no backend configured
- sign-in required
- premium not enabled
- out of credits
- rate limited
- text rejected
- provider failed
- offline or network error

The current Android prep maps those states through `NarrationVoiceResult.Failure`. Release builds are allowed to reach `ProductionBackendVoiceProvider` so those backend/credit states can be displayed, but the Android client remains advisory; the backend must reject unauthenticated, unentitled, or out-of-credit requests before calling ElevenLabs.

## Abuse Controls

- Require auth for production narration.
- Enforce per-user daily and monthly caps even for paid users.
- Apply stricter limits to new accounts and suspicious device fingerprints.
- Block repeated failed safety attempts.
- Use provider-side text limits and server-side caps before spending ElevenLabs quota.
- Keep voice ID allowlists per feature.
- Record idempotency keys and request hashes to suppress retry storms.

## Logging And Privacy

Default logging should store metadata only:

- user ID or internal account ID
- feature
- voice ID
- tone
- status or error code
- credit delta
- provider latency
- request size
- client request ID

Do not store raw narration text unless the privacy policy and in-app disclosure explicitly permit it. If text retention is enabled for moderation or support, define retention windows and deletion controls.

## Remaining Product Work

- Finalize credit amounts from provider cost and pricing.
- Define refund and failed-generation policies.
- Add billing and entitlement backend integration.
- Add app account auth integration.
- Add privacy disclosure for premium narration processing.
