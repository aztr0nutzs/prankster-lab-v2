# Backend ElevenLabs Proxy Contract

Last updated: 2026-06-10

## Purpose

Production Android builds must never call ElevenLabs directly and must never contain an ElevenLabs API key. Premium Tweaker Geographic / Twak-Attacks narration should route through a backend that verifies identity, entitlement, credits, safety, and rate limits before spending provider quota.

## Android Client

Production builds use `VoiceGenerationMode.PRODUCTION_BACKEND`. The Android client calls only the app backend and sends:

- the generated narration text
- the feature identifier
- the configured ElevenLabs voice ID
- the requested tone
- a client request ID for idempotency and support correlation

The client must not send or receive the ElevenLabs API key.

## Endpoint

`POST /api/generate-narration`

Headers:

```http
Authorization: Bearer <app-auth-token>
Content-Type: application/json
Accept: audio/mpeg, application/json
```

Request:

```json
{
  "feature": "twak_attacks",
  "text": "...",
  "voiceId": "wV67xHKrIHTU0gtChZiQ",
  "tone": "CHAOTIC",
  "clientRequestId": "uuid"
}
```

## Backend Responsibilities

- Verify the app auth token and resolve the user account.
- Verify the user has access to premium narration for the requested feature.
- Verify remaining credits before calling ElevenLabs.
- Apply per-user, per-device, and per-IP rate limits.
- Safety-check text before provider submission.
- Enforce maximum text length and feature-specific voice allowlists.
- Call ElevenLabs server-side using a secret from backend secret storage.
- Store the MP3 or stream audio bytes back to the client.
- Decrement credits only on successful generation unless a product policy explicitly charges for provider failures after retry.
- Persist `clientRequestId` idempotency state to avoid double charging retries.
- Log request metadata without storing sensitive narration text unless the privacy disclosure permits it.
- Never expose the ElevenLabs API key to the Android app, logs, analytics, or client-visible errors.

## Response Options

Option A, direct audio response:

```http
HTTP/1.1 200 OK
Content-Type: audio/mpeg
X-Remaining-Credits: 12
```

Response body is MP3 bytes.

Option B, signed URL response:

```json
{
  "success": true,
  "audioUrl": "https://storage.example/signed-url",
  "remainingCredits": 12
}
```

The signed URL should be short-lived and scoped to one generated asset.

## Error Response

```json
{
  "success": false,
  "errorCode": "OUT_OF_CREDITS",
  "remainingCredits": 0
}
```

Supported error codes:

| Code | Meaning | Suggested HTTP status |
| --- | --- | --- |
| `UNAUTHENTICATED` | Missing, expired, or invalid app auth token. | 401 |
| `NOT_ENTITLED` | User is authenticated but lacks premium narration entitlement. | 403 |
| `OUT_OF_CREDITS` | User has no usable narration credits. | 402 |
| `RATE_LIMITED` | User, device, or IP exceeded rate limits. | 429 |
| `TEXT_REJECTED` | Text failed safety, length, or policy checks. | 422 |
| `PROVIDER_FAILED` | ElevenLabs or backend generation failed after retry policy. | 502 |
| `NETWORK_ERROR` | Backend could not reach a required upstream dependency. | 503 |

## Security Requirements

- Store ElevenLabs credentials only in backend secret storage.
- Rotate the ElevenLabs key if it is ever exposed in a client build, repo, log, crash report, or analytics event.
- Keep a server-side allowlist mapping `twak_attacks` and `tweaker_geographic` to `wV67xHKrIHTU0gtChZiQ`.
- Reject arbitrary ElevenLabs voice IDs unless explicitly enabled for a product tier.
- Use TLS only. Do not allow cleartext backend URLs in production.
- Use idempotency keyed by user ID plus `clientRequestId`.
- Avoid including raw text in crash logs or analytics events.

## Android Integration Status

`ProductionBackendVoiceProvider` is present in the Android app and prepared to call this endpoint. Production auth token retrieval, backend deployment, billing, and entitlement checks remain backend/product work before Play testing.
