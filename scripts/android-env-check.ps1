$ErrorActionPreference = "Stop"

$ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$LocalProperties = Join-Path $ProjectRoot "local.properties"

function Find-AndroidSdk {
    if ($env:ANDROID_HOME -and (Test-Path $env:ANDROID_HOME)) { return $env:ANDROID_HOME }
    if ($env:ANDROID_SDK_ROOT -and (Test-Path $env:ANDROID_SDK_ROOT)) { return $env:ANDROID_SDK_ROOT }

    $Candidates = @(
        "$HOME/Android/Sdk",
        "$HOME/Library/Android/sdk",
        "/opt/android-sdk",
        "/usr/local/lib/android/sdk",
        "/usr/lib/android-sdk",
        "$env:LOCALAPPDATA\Android\Sdk"
    )

    foreach ($Candidate in $Candidates) {
        if ($Candidate -and (Test-Path $Candidate)) { return (Resolve-Path $Candidate).Path }
    }
    return $null
}

$SdkPath = Find-AndroidSdk
if (-not $SdkPath) { throw "No Android SDK found via ANDROID_HOME/ANDROID_SDK_ROOT or common paths." }

$Required = @("platforms", "platform-tools", "build-tools")
foreach ($Item in $Required) {
    $Full = Join-Path $SdkPath $Item
    if (-not (Test-Path $Full)) { throw "Missing required SDK directory: $Full" }
}

$Escaped = $SdkPath.Replace("\", "\\")
Set-Content -Path $LocalProperties -Value "sdk.dir=$Escaped"

$env:ANDROID_HOME = $SdkPath
$env:ANDROID_SDK_ROOT = $SdkPath

Write-Host "SDK OK: $SdkPath"
Write-Host "Wrote $LocalProperties"
Get-Content $LocalProperties
