#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

echo ""
echo "========================================"
echo " Android Debug Build"
echo "========================================"
echo ""

if [ -f "scripts/android-env-check.sh" ]; then
  chmod +x scripts/android-env-check.sh
  ./scripts/android-env-check.sh
else
  echo "ERROR: scripts/android-env-check.sh is missing."
  exit 1
fi

if [ ! -f "./gradlew" ]; then
  echo "ERROR: ./gradlew is missing."
  echo "This project does not include a Gradle wrapper script."
  exit 1
fi

chmod +x ./gradlew

echo ""
echo "Checking Gradle wrapper..."
./gradlew --version

echo ""
echo "Running clean debug build..."
./gradlew clean assembleDebug --stacktrace

echo ""
echo "Build complete."
echo ""
echo "APK output locations to check:"
find "$PROJECT_ROOT" -path "*build/outputs/apk/debug/*.apk" -type f -print || true
echo ""