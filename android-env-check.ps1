$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "========================================"
Write-Host " Android SDK Environment Check"
Write-Host "========================================"
Write-Host ""

$ProjectRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$LocalProperties = Join-Path $ProjectRoot "local.properties"

Write-Host "Project root: $ProjectRoot"
Write-Host ""

function Find-AndroidSdk {
    if ($env:ANDROID_HOME -and (Test-Path $env:ANDROID_HOME)) {
        return $env:ANDROID_HOME
    }

    if ($env:ANDROID_SDK_ROOT -and (Test-Path $env:ANDROID_SDK_ROOT)) {
        return $env:ANDROID_SDK_ROOT
    }

    $CommonPaths = @(
        "$env:LOCALAPPDATA\Android\Sdk",
        "$env:USERPROFILE\AppData\Local\Android\Sdk",
        "$env:USERPROFILE\Android\Sdk",
        "C:\Android\Sdk",
        "C:\Android\sdk"
    )

    foreach ($Path in $CommonPaths) {
        if (Test-Path $Path) {
            return $Path
        }
    }

    return $null
}

$SdkPath = Find-AndroidSdk

if (-not $SdkPath) {
    Write-Host "ERROR: Android SDK was not found." -ForegroundColor Red
    Write-Host ""
    Write-Host "Install Android Studio and confirm SDK location here:"
    Write-Host "  Android Studio > Settings > Languages & Frameworks > Android SDK"
    Write-Host ""
    Write-Host "Common Windows SDK path:"
    Write-Host "  C:\Users\<YourName>\AppData\Local\Android\Sdk"
    Write-Host ""
    exit 1
}

Write-Host "Detected Android SDK:"
Write-Host "  $SdkPath"
Write-Host ""

$PlatformsPath = Join-Path $SdkPath "platforms"
$PlatformToolsPath = Join-Path $SdkPath "platform-tools"
$BuildToolsPath = Join-Path $SdkPath "build-tools"

if (-not (Test-Path $PlatformsPath)) {
    Write-Host "ERROR: SDK path exists, but platforms directory is missing:" -ForegroundColor Red
    Write-Host "  $PlatformsPath"
    Write-Host ""
    Write-Host "Open Android Studio SDK Manager and install at least one Android SDK Platform."
    exit 1
}

if (-not (Test-Path $PlatformToolsPath)) {
    Write-Host "WARNING: platform-tools directory is missing:" -ForegroundColor Yellow
    Write-Host "  $PlatformToolsPath"
}

if (-not (Test-Path $BuildToolsPath)) {
    Write-Host "WARNING: build-tools directory is missing:" -ForegroundColor Yellow
    Write-Host "  $BuildToolsPath"
}

$EscapedSdkPath = $SdkPath.Replace("\", "\\")
Set-Content -Path $LocalProperties -Value "sdk.dir=$EscapedSdkPath"

Write-Host ""
Write-Host "Wrote local.properties:"
Write-Host "  $LocalProperties"
Write-Host ""
Get-Content $LocalProperties
Write-Host ""

[Environment]::SetEnvironmentVariable("ANDROID_HOME", $SdkPath, "User")
[Environment]::SetEnvironmentVariable("ANDROID_SDK_ROOT", $SdkPath, "User")

$CurrentUserPath = [Environment]::GetEnvironmentVariable("Path", "User")
$AddPaths = @(
    "$SdkPath\platform-tools",
    "$SdkPath\cmdline-tools\latest\bin",
    "$SdkPath\tools",
    "$SdkPath\tools\bin"
)

foreach ($PathToAdd in $AddPaths) {
    if ((Test-Path $PathToAdd) -and ($CurrentUserPath -notlike "*$PathToAdd*")) {
        $CurrentUserPath = "$CurrentUserPath;$PathToAdd"
    }
}

[Environment]::SetEnvironmentVariable("Path", $CurrentUserPath, "User")

Write-Host "Environment variables updated for your Windows user."
Write-Host "Close and reopen PowerShell before running Gradle again."
Write-Host ""
Write-Host "Then run:"
Write-Host "  .\gradlew.bat assembleDebug"
Write-Host ""