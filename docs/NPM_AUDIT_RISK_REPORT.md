# npm Audit Risk Report

Last updated: 2026-06-11

## Commands

- `npm audit --json > qa/npm_audit.json`
- `npm audit > qa/npm_audit.txt`
- `npm audit fix`
- `npm ci`
- `npm run lint`
- `npm run build`
- final `npm audit --json > qa/npm_audit.json`
- final `npm audit > qa/npm_audit.txt`

## Result

Safe non-force remediation was applied. `package-lock.json` was updated for non-breaking dependency fixes, including `express`, `qs`, `protobufjs`, `@protobufjs/*`, and `ws`-related transitive packages. Verification passed:

- `npm ci`: PASS.
- `npm run lint`: PASS.
- `npm run build`: PASS.

Final audit status: 2 high vulnerabilities remain.

## Remaining Findings

| Package | Severity | Direct/transitive | Affected feature | Fix available | Risk classification |
| --- | --- | --- | --- | --- | --- |
| `google-tts-api` | High | Direct dependency | JavaScript/web tooling or server-adjacent TTS path, not the Android bundled sound catalog | `npm audit fix --force` would install `google-tts-api@0.0.6` | Needs dependency replacement or removal task before shipping any Node/web backend using this dependency. |
| `axios` | High | Transitive through `google-tts-api` | Same `google-tts-api` path | Only through forced breaking `google-tts-api` downgrade | Must fix before shipping any production web/server path that can make attacker-influenced network requests through this graph. |

## Resolved By Safe Fix

The initial audit reported 7 vulnerabilities: 3 high and 4 moderate. The non-force fix removed the moderate findings involving `@protobufjs/utf8`, `protobufjs`, `qs`/`express`, and `ws`, leaving only the `google-tts-api -> axios` high-risk chain.

## Decision

Do not run `npm audit fix --force` in this blocker pass. It would install an older semver-breaking `google-tts-api@0.0.6`, which needs compatibility review and replacement planning.

For Android closed testing, this can be risk-accepted only if the Node/web build and any `google-tts-api` code path are not shipped or exposed. For any production backend, web deployment, or server-adjacent feature, this remains a must-fix blocker.
