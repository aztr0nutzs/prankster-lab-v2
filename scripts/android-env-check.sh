#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOCAL_PROPERTIES="$PROJECT_ROOT/local.properties"

printf "Android SDK environment check\n"
printf "Project root: %s\n" "$PROJECT_ROOT"

find_sdk() {
  if [[ -n "${ANDROID_HOME:-}" && -d "${ANDROID_HOME}" ]]; then
    printf '%s' "$ANDROID_HOME"
    return 0
  fi
  if [[ -n "${ANDROID_SDK_ROOT:-}" && -d "${ANDROID_SDK_ROOT}" ]]; then
    printf '%s' "$ANDROID_SDK_ROOT"
    return 0
  fi

  local candidates=(
    "$HOME/Android/Sdk"
    "$HOME/Library/Android/sdk"
    "/opt/android-sdk"
    "/usr/local/lib/android/sdk"
    "/usr/lib/android-sdk"
    "/mnt/c/Users/${USER:-}/AppData/Local/Android/Sdk"
  )

  if command -v wslpath >/dev/null 2>&1 && [[ -n "${USERPROFILE:-}" ]]; then
    candidates+=("$(wslpath -u "$USERPROFILE")/AppData/Local/Android/Sdk")
  fi

  local path
  for path in "${candidates[@]}"; do
    if [[ -d "$path" ]]; then
      printf '%s' "$path"
      return 0
    fi
  done
  return 1
}

SDK_PATH="$(find_sdk || true)"
if [[ -z "$SDK_PATH" ]]; then
  echo "ERROR: No Android SDK found via ANDROID_HOME/ANDROID_SDK_ROOT or common paths."
  exit 1
fi

MISSING=0
for req in platforms platform-tools build-tools; do
  if [[ ! -d "$SDK_PATH/$req" ]]; then
    echo "ERROR: Missing required SDK directory: $SDK_PATH/$req"
    MISSING=1
  fi
done
if [[ "$MISSING" -ne 0 ]]; then
  exit 1
fi

ESCAPED_SDK_PATH="${SDK_PATH//\\/\\\\}"
printf 'sdk.dir=%s\n' "$ESCAPED_SDK_PATH" > "$LOCAL_PROPERTIES"

export ANDROID_HOME="$SDK_PATH"
export ANDROID_SDK_ROOT="$SDK_PATH"

echo "SDK OK: $SDK_PATH"
echo "Wrote $LOCAL_PROPERTIES"
cat "$LOCAL_PROPERTIES"
