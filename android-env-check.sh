#!/usr/bin/env bash
set -euo pipefail

echo ""
echo "========================================"
echo " Android SDK Environment Check"
echo "========================================"
echo ""

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOCAL_PROPERTIES="$PROJECT_ROOT/local.properties"

echo "Project root: $PROJECT_ROOT"
echo ""

detect_sdk() {
  if [ -n "${ANDROID_HOME:-}" ] && [ -d "$ANDROID_HOME" ]; then
    echo "$ANDROID_HOME"
    return 0
  fi

  if [ -n "${ANDROID_SDK_ROOT:-}" ] && [ -d "$ANDROID_SDK_ROOT" ]; then
    echo "$ANDROID_SDK_ROOT"
    return 0
  fi

  COMMON_PATHS=(
    "$HOME/Android/Sdk"
    "$HOME/Library/Android/sdk"
    "$HOME/android-sdk"
    "$HOME/.android-sdk"
    "/opt/android-sdk"
    "/usr/local/lib/android/sdk"
    "/usr/lib/android-sdk"
    "/android-sdk"
  )

  for path in "${COMMON_PATHS[@]}"; do
    if [ -d "$path" ]; then
      echo "$path"
      return 0
    fi
  done

  return 1
}

SDK_PATH="$(detect_sdk || true)"

if [ -z "${SDK_PATH:-}" ]; then
  echo "ERROR: Android SDK was not found."
  echo ""
  echo "Set one of these before building:"
  echo "  export ANDROID_HOME=/path/to/android/sdk"
  echo "  export ANDROID_SDK_ROOT=/path/to/android/sdk"
  echo ""
  echo "Common Linux path:"
  echo "  export ANDROID_HOME=\$HOME/Android/Sdk"
  echo ""
  echo "Common macOS path:"
  echo "  export ANDROID_HOME=\$HOME/Library/Android/sdk"
  echo ""
  echo "Common Windows path:"
  echo "  C:\\Users\\<YourName>\\AppData\\Local\\Android\\Sdk"
  echo ""
  exit 1
fi

echo "Detected Android SDK:"
echo "  $SDK_PATH"
echo ""

if [ ! -d "$SDK_PATH/platforms" ]; then
  echo "ERROR: SDK path exists, but platforms directory is missing:"
  echo "  $SDK_PATH/platforms"
  echo ""
  echo "The SDK is incomplete. Install Android SDK Platform packages using Android Studio SDK Manager or sdkmanager."
  exit 1
fi

if [ ! -d "$SDK_PATH/platform-tools" ]; then
  echo "WARNING: platform-tools directory is missing:"
  echo "  $SDK_PATH/platform-tools"
  echo ""
fi

if [ ! -d "$SDK_PATH/build-tools" ]; then
  echo "WARNING: build-tools directory is missing:"
  echo "  $SDK_PATH/build-tools"
  echo ""
fi

ESCAPED_SDK_PATH="${SDK_PATH//\\/\\\\}"

cat > "$LOCAL_PROPERTIES" <<EOF
sdk.dir=$ESCAPED_SDK_PATH
EOF

echo "Wrote local.properties:"
echo "  $LOCAL_PROPERTIES"
echo ""
cat "$LOCAL_PROPERTIES"
echo ""

export ANDROID_HOME="$SDK_PATH"
export ANDROID_SDK_ROOT="$SDK_PATH"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/tools:$ANDROID_HOME/tools/bin:$PATH"

echo "Environment check complete."
echo ""