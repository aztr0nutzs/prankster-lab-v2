$ErrorActionPreference = "Stop"

$ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
Set-Location $ProjectRoot

& (Join-Path $ProjectRoot "scripts/android-env-check.ps1")

& (Join-Path $ProjectRoot "gradlew.bat") "assembleDebug" "--stacktrace" "--console=plain"
