$ErrorActionPreference = "Stop"

$ProjectRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $ProjectRoot

Write-Host ""
Write-Host "========================================"
Write-Host " Android Debug Build"
Write-Host "========================================"
Write-Host ""

$EnvCheck = Join-Path $ProjectRoot "scripts\android-env-check.ps1"

if (-not (Test-Path $EnvCheck)) {
    Write-Host "ERROR: scripts\android-env-check.ps1 is missing." -ForegroundColor Red
    exit 1
}

powershell -ExecutionPolicy Bypass -File $EnvCheck

$GradlewBat = Join-Path $ProjectRoot "gradlew.bat"

if (-not (Test-Path $GradlewBat)) {
    Write-Host "ERROR: gradlew.bat is missing." -ForegroundColor Red
    Write-Host "This project does not include a Gradle wrapper script."
    exit 1
}

Write-Host ""
Write-Host "Checking Gradle wrapper..."
& $GradlewBat --version

Write-Host ""
Write-Host "Running clean debug build..."
& $GradlewBat clean assembleDebug --stacktrace

Write-Host ""
Write-Host "Build complete."
Write-Host ""
Write-Host "APK output locations:"
Get-ChildItem -Path $ProjectRoot -Recurse -Filter "*.apk" |
    Where-Object { $_.FullName -like "*build\outputs\apk\debug*" } |
    ForEach-Object { Write-Host $_.FullName }

Write-Host ""