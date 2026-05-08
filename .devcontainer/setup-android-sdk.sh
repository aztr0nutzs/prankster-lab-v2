#!/usr/bin/env bash
set -euo pipefail

ANDROID_HOME="/opt/android-sdk"
CMDLINE_TOOLS_DIR="$ANDROID_HOME/cmdline-tools"
LATEST_DIR="$CMDLINE_TOOLS_DIR/latest"

echo ""
echo "========================================"
echo " Installing Android SDK Command Line Tools"
echo "========================================"
echo ""

sudo mkdir -p "$CMDLINE_TOOLS_DIR"
sudo chown -R "$(id -u):$(id -g)" "$ANDROID_HOME"

cd /tmp

if [ ! -d "$LATEST_DIR" ]; then
  echo "Downloading Android command line tools..."
  curl -L -o commandlinetools-linux.zip "https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"

  rm -rf /tmp/android-cmdline-tools
  mkdir -p /tmp/android-cmdline-tools

  unzip -q commandlinetools-linux.zip -d /tmp/android-cmdline-tools

  mkdir -p "$LATEST_DIR"
  mv /tmp/android-cmdline-tools/cmdline-tools/* "$LATEST_DIR/"
fi

export ANDROID_HOME="$ANDROID_HOME"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"

yes | sdkmanager --licenses >/dev/null || true

sdkmanager \
  "platform-tools" \
  "platforms;android-35" \
  "build-tools;35.0.0" \
  "cmdline-tools;latest"

echo "sdk.dir=$ANDROID_HOME" > "$OLDPWD/local.properties" 2>/dev/null || true

echo ""
echo "Android SDK installed at:"
echo "  $ANDROID_HOME"
echo ""
sdkmanager --list_installed
echo ""
