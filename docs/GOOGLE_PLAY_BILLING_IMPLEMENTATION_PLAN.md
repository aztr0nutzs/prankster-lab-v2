# Google Play Billing Implementation Plan

Last updated: 2026-06-10

## Scope

This plan prepares Prankstar Pro and voice generation credits without adding fake purchases or trusting client-side entitlement state. Android should expose product offers and local UI state, but the backend must verify purchases and issue authoritative entitlements before premium ElevenLabs generation spends credits.

## Product Model

Free:

- Bundled Sound Stash playback.
- Basic local narration generation.
- Local Android TextToSpeech.
- Limited generated clip saves.
- Limited Twak-Attacks text generation.
- Basic Twak-Attacks tones.

Prankstar Pro:

- Dedicated ElevenLabs British narrator voice.
- Higher monthly voice credits.
- Unlimited generated clip saves or a much higher server-configured save limit.
- Advanced Twak-Attacks tones.
- Premium bot assistant actions.
- No ads if ads are later added.

Consumables:

- Extra voice credit packs.

## Product IDs

Subscriptions:

| Product ID | Type | Purpose |
| --- | --- | --- |
| `prankstar_pro_monthly` | Subscription | Monthly Pro entitlement and monthly voice credit allowance. |
| `prankstar_pro_yearly` | Subscription | Yearly Pro entitlement and monthly voice credit allowance. |

One-time non-consumable:

| Product ID | Type | Purpose |
| --- | --- | --- |
| `prankstar_pro_lifetime` | Non-consumable | Lifetime Pro entitlement. Monthly credit policy must be server-defined. |

Consumables:

| Product ID | Type | Purpose |
| --- | --- | --- |
| `voice_credits_25` | Consumable | Small top-up pack. |
| `voice_credits_100` | Consumable | Medium top-up pack. |
| `voice_credits_300` | Consumable | Large top-up pack. |

## Android Architecture

Add the Google Play Billing dependency only after dependency approval:

```kotlin
implementation("com.android.billingclient:billing-ktx:<approved-version>")
```

Planned Android components:

- `BillingClientManager`: owns BillingClient connection, product querying, purchase launch, and purchase update callbacks.
- `PlayBillingRepository`: exposes product details, purchase state, restore, and pending purchase status.
- `EntitlementRepository`: combines verified backend entitlement and credit state into `UserEntitlement` and `VoiceCreditBalance`.
- `FeatureGate`: remains a pure model layer consumed by UI and generation flows.

The existing `app/src/main/java/com/pranksterlab/core/billing/` models are intentionally dependency-free and can be reused by the billing repository.

## Server Verification Requirement

The Android app must send purchase tokens to the backend. The backend must:

- Verify purchase tokens with the Google Play Developer API.
- Validate package name, product ID, purchase state, acknowledgement state, and subscription status.
- Bind purchase tokens to the authenticated app user.
- Acknowledge non-consumable/subscription purchases after successful entitlement persistence.
- Consume voice credit pack purchases only after credits are added to the ledger.
- Return authoritative `UserEntitlement` and `VoiceCreditBalance` to Android.

Premium ElevenLabs generation must check backend entitlement and credits again at `POST /api/generate-narration`; Android UI gating is advisory only.

## Entitlement Refresh

Refresh entitlement:

- at app launch
- when opening Voice Lab
- before premium narration generation
- after purchase completion
- after restore
- when backend returns `UNAUTHENTICATED`, `NOT_ENTITLED`, or `OUT_OF_CREDITS`
- periodically while the app is active if the last verification is stale

Cache entitlements locally only for display and graceful offline messaging. Do not allow offline cached Pro state to spend voice credits.

## Purchase Restore

Restore flow:

- Query existing purchases from Play Billing.
- Send purchase tokens to backend for verification.
- Reconcile backend entitlement and credit ledger.
- Display restored plan and credits only after backend success.
- If backend is unreachable, show a recoverable "Restore unavailable" state, not Pro access.

## Consumable Credit Packs

Credit pack flow:

1. Android launches Play Billing purchase.
2. Play returns a purchased token.
3. Android sends token to backend.
4. Backend verifies token.
5. Backend adds credits with an idempotent ledger entry.
6. Backend consumes the Play purchase token.
7. Backend returns updated credit balance.

Never add credits solely because BillingClient reported purchase success.

## Failure States

Android must handle:

- Billing unavailable on device.
- Product details unavailable.
- User canceled purchase.
- Purchase pending.
- Purchase failed.
- Backend verification failed.
- Purchase token already claimed.
- Restore unavailable.
- Entitlement revoked.
- Subscription expired.
- Out of credits.
- Rate limited.

## Refunds, Revocation, And Expiration

The backend must listen to Real-time Developer Notifications and/or poll Google Play Developer API for:

- subscription renewal
- subscription cancellation
- subscription expiration
- refund
- chargeback
- account hold
- grace period
- pause/resume
- product revocation

On revocation, downgrade entitlement server-side and prevent new premium generation. Previously saved user-generated clips should remain available unless policy or legal requirements say otherwise.

## Sandbox Testing Plan

Before production rollout:

- Create Play Console products and base plans.
- Add license test accounts.
- Test monthly subscription purchase.
- Test yearly subscription purchase.
- Test lifetime purchase.
- Test each credit pack.
- Test pending purchase.
- Test cancellation and restore.
- Test refund/revocation via backend simulation.
- Verify app restart entitlement refresh.
- Verify premium generation checks backend entitlement before ElevenLabs.
- Verify free users cannot call premium backend generation.
- Verify generated Sound Stash playback remains available for free users.

## Current Android Prep Status

- `BillingProductIds` defines planned product IDs.
- `BillingProducts` defines dependency-free product metadata for subscriptions, lifetime Pro, and credit packs.
- `UserEntitlement` represents Free, Pro Monthly, Pro Yearly, and Lifetime Pro.
- `VoiceCreditBalance` represents configured or missing credit state.
- `FeatureGate` centralizes premium narration, generated saves, advanced Twak tones, premium bot actions, and remaining credits.
- `EntitlementRepository` is the injection point for backend-verified entitlement and credit state. The current Android app wires `UnconfiguredEntitlementRepository`, which only exposes Free / not configured state until real billing and backend verification are accepted.
- Voice Lab and System settings show non-fake "not configured" states.
- Production backend mode does not bypass `FeatureGate`; premium narration still requires a verified Pro entitlement and usable voice credits. Debug direct ElevenLabs mode remains a local developer path only.
- Premium bot planning actions are routed through `FeatureGate.canUsePremiumBotActions`; free users keep sound search, playback, local joke text, navigation, and stop controls.
- No Google Play Billing dependency or fake purchase flow has been added.
