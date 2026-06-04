param(
    [string]$JsonPath = "naknada.json",
    [string]$PrivateKeyPath = "$env:USERPROFILE\.postonosa-signing\naknada_private_key.pem",
    [string]$OutputPath = ""
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$sourceFile = Join-Path $PSScriptRoot "SignNaknada.java"
$buildDir = Join-Path $env:TEMP ("postonosa-sign-" + [guid]::NewGuid().ToString("N"))

$javaHomeCandidates = @(
    $env:JAVA_HOME,
    "C:\Program Files\Android\Android Studio\jbr"
) | Where-Object { $_ -and (Test-Path -LiteralPath $_) }

$javaHome = $javaHomeCandidates | Select-Object -First 1
if (-not $javaHome) {
    throw "Java nije pronađena. Postavite JAVA_HOME ili instalirajte Android Studio JBR."
}

$javac = Join-Path $javaHome "bin\javac.exe"
$java = Join-Path $javaHome "bin\java.exe"
if (-not (Test-Path -LiteralPath $javac) -or -not (Test-Path -LiteralPath $java)) {
    throw "Java alati nisu pronađeni u: $javaHome"
}

if (-not [System.IO.Path]::IsPathRooted($JsonPath)) {
    $JsonPath = Join-Path $repoRoot $JsonPath
}

if (-not [System.IO.Path]::IsPathRooted($PrivateKeyPath)) {
    $PrivateKeyPath = Join-Path $repoRoot $PrivateKeyPath
}

if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = "$JsonPath.sig"
} elseif (-not [System.IO.Path]::IsPathRooted($OutputPath)) {
    $OutputPath = Join-Path $repoRoot $OutputPath
}

if (-not (Test-Path -LiteralPath $JsonPath)) {
    throw "JSON fajl nije pronađen: $JsonPath"
}

if (-not (Test-Path -LiteralPath $PrivateKeyPath)) {
    throw "Privatni ključ nije pronađen: $PrivateKeyPath"
}

try {
    New-Item -ItemType Directory -Force -Path $buildDir | Out-Null
    & $javac -encoding UTF-8 -d $buildDir $sourceFile
    if ($LASTEXITCODE -ne 0) {
        throw "Kompajliranje SignNaknada.java nije uspjelo."
    }

    & $java -cp $buildDir SignNaknada $JsonPath $PrivateKeyPath $OutputPath
    if ($LASTEXITCODE -ne 0) {
        throw "Potpisivanje nije uspjelo."
    }
} finally {
    if (Test-Path -LiteralPath $buildDir) {
        Remove-Item -LiteralPath $buildDir -Recurse -Force
    }
}
