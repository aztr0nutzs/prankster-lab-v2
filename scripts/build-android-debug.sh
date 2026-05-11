#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

"$PROJECT_ROOT/scripts/android-env-check.sh"

if [[ ! -f "$PROJECT_ROOT/gradlew" ]]; then
  echo "ERROR: Missing Gradle wrapper: $PROJECT_ROOT/gradlew"
  exit 1
fi

chmod +x "$PROJECT_ROOT/gradlew"

./gradlew --version

./gradlew clean assembleDebug --stacktrace --console=plain

mapfile -t APK_OUTPUTS < <(find "$PROJECT_ROOT" -path "*/build/outputs/apk/debug/*.apk" -type f | sort)

if [[ "${#APK_OUTPUTS[@]}" -eq 0 ]]; then
  echo "ERROR: Build completed but no debug APK outputs were found."
  exit 1
fi

echo "Debug APK outputs:"
for apk in "${APK_OUTPUTS[@]}"; do
  size_bytes="$(wc -c < "$apk" | tr -d '[:space:]')"
  echo "  $apk"
  echo "    size: $size_bytes bytes"
done
