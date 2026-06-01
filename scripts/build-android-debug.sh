#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

"$PROJECT_ROOT/scripts/android-env-check.sh"

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
  cmd.exe /c "set \"JAVA_HOME=$WINDOWS_JAVA\"&& gradlew.bat assembleDebug --stacktrace --console=plain"
  exit $?
fi

WRAPPER="./gradlew"
if head -n 1 "$WRAPPER" | grep -q $'\r'; then
  TEMP_WRAPPER="./.gradlew.lf"
  trap 'rm -f "$TEMP_WRAPPER"' EXIT
  tr -d '\r' < "$WRAPPER" > "$TEMP_WRAPPER"
  chmod +x "$TEMP_WRAPPER"
  "$TEMP_WRAPPER" assembleDebug --stacktrace --console=plain
else
  "$WRAPPER" assembleDebug --stacktrace --console=plain
fi
