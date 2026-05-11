$ErrorActionPreference = "Stop"

$ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
Set-Location $ProjectRoot

& (Join-Path $ProjectRoot "scripts/android-env-check.ps1")

$GradleWrapper = Join-Path $ProjectRoot "gradlew.bat"
if (-not (Test-Path $GradleWrapper)) {
    throw "Missing Gradle wrapper: $GradleWrapper"
}

& $GradleWrapper "--version"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& $GradleWrapper "clean" "assembleDebug" "--stacktrace" "--console=plain"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$ApkOutputs = Get-ChildItem -Path $ProjectRoot -Recurse -File -Filter "*.apk" |
    Where-Object { $_.FullName -like "*\build\outputs\apk\debug\*" } |
    Sort-Object FullName

if (-not $ApkOutputs) {
    throw "Build completed but no debug APK outputs were found."
}

Write-Host "Debug APK outputs:"
foreach ($Apk in $ApkOutputs) {
    Write-Host "  $($Apk.FullName)"
    Write-Host "    size: $($Apk.Length) bytes"
}
