#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

"$PROJECT_ROOT/scripts/android-env-check.sh"

if [[ ! -f "$PROJECT_ROOT/gradlew" ]]; then
  echo "ERROR: Missing Gradle wrapper: $PROJECT_ROOT/gradlew"
  exit 1
fi

WINDOWS_JAVA_HOME=""
if ! command -v java >/dev/null 2>&1; then
  for candidate in \
    "/mnt/c/Program Files/Eclipse Adoptium"/* \
    "/mnt/c/Program Files/Java"/* \
    "/mnt/c/Program Files/Android/Android Studio/jbr"; do
    if [[ -x "$candidate/bin/java.exe" || -x "$candidate/bin/java" ]]; then
      WINDOWS_JAVA_HOME="$candidate"
      break
    fi
  done
fi

print_apk_outputs() {
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
}

if [[ -n "$WINDOWS_JAVA_HOME" ]] && command -v cmd.exe >/dev/null 2>&1; then
  WINDOWS_SDK="$(grep -E '^sdk\.dir=' "$PROJECT_ROOT/local.properties" | tail -n 1 | cut -d= -f2-)"
  if [[ "$WINDOWS_SDK" =~ ^/mnt/([A-Za-z])/(.*)$ ]]; then
    drive="${BASH_REMATCH[1]^^}"
    rest="${BASH_REMATCH[2]//\//\\}"
    WINDOWS_SDK="$drive:\\$rest"
    printf 'sdk.dir=%s\n' "${WINDOWS_SDK//\\/\\\\}" > "$PROJECT_ROOT/local.properties"
  fi
  WINDOWS_JAVA="${WINDOWS_JAVA_HOME#/mnt/}"
  WINDOWS_DRIVE="${WINDOWS_JAVA%%/*}"
  WINDOWS_REST="${WINDOWS_JAVA#*/}"
  WINDOWS_JAVA="${WINDOWS_DRIVE^^}:\\${WINDOWS_REST//\//\\}"
  cmd.exe /c "set \"JAVA_HOME=$WINDOWS_JAVA\"&& gradlew.bat --version && gradlew.bat clean assembleDebug --stacktrace --console=plain"
  print_apk_outputs
  exit 0
fi

chmod +x "$PROJECT_ROOT/gradlew"

WRAPPER="./gradlew"
if head -n 1 "$WRAPPER" | grep -q $'\r'; then
  TEMP_WRAPPER="./.gradlew.lf"
  trap 'rm -f "$TEMP_WRAPPER"' EXIT
  tr -d '\r' < "$WRAPPER" > "$TEMP_WRAPPER"
  chmod +x "$TEMP_WRAPPER"
  "$TEMP_WRAPPER" --version
  "$TEMP_WRAPPER" clean assembleDebug --stacktrace --console=plain
else
  "$WRAPPER" --version
  "$WRAPPER" clean assembleDebug --stacktrace --console=plain
fi

print_apk_outputs
