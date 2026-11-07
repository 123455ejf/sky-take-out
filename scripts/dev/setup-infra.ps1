param([switch]$AllowOutsideProjectJunction)

$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'infra-common.ps1')
Initialize-RuntimeDirectories

$downloadRoot = Join-Path $ToolsRoot 'downloads'
New-Item -ItemType Directory -Force -Path $downloadRoot | Out-Null

if ($ProcessRoot -eq $RepoRoot -and $RepoRoot -match '[^\x00-\x7F]') {
    if (-not $AllowOutsideProjectJunction) {
        Write-Host "Project path contains non-ASCII characters: $RepoRoot"
        Write-Host "Run again with -AllowOutsideProjectJunction to create D:\sky-take-out-demo."
        throw 'ASCII path alias is required.'
    }

    if (Test-Path -LiteralPath $AsciiRoot) {
        $target = (Get-Item -LiteralPath $AsciiRoot).Target
        if ($target -and $target -ne $RepoRoot) {
            throw "Existing junction points elsewhere: $AsciiRoot -> $target"
        }
    } else {
        New-Item -ItemType Junction -Path $AsciiRoot -Target $RepoRoot | Out-Null
    }
}

$downloads = @(
    @{
        Name = 'mysql-8.0.43-winx64.zip'
        Url = 'https://dev.mysql.com/get/Downloads/MySQL-8.0/mysql-8.0.43-winx64.zip'
        Type = 'zip'
    },
    @{
        Name = 'Redis-x64-5.0.14.1.zip'
        Url = 'https://github.com/tporadowski/redis/releases/download/v5.0.14.1/Redis-x64-5.0.14.1.zip'
        Type = 'zip'
    },
    @{
        Name = 'minio-community.exe'
        Url = 'https://github.com/minio/minio/releases/download/RELEASE.2024-12-18T13-15-44Z/minio.windows-amd64.RELEASE.2024-12-18T13-15-44Z.exe'
        Type = 'exe'
    }
)

foreach ($item in $downloads) {
    $path = Join-Path $downloadRoot $item.Name
    if (-not (Test-Path -LiteralPath $path)) {
        Write-Host "Missing $($item.Name)"
        Write-Host "Download from: $($item.Url)"
        throw "Required portable file is missing: $path"
    }
}

$mysqlHome = Join-Path $ToolsRoot 'mysql\mysql-8.0.43-winx64'
if (-not (Test-Path -LiteralPath (Join-Path $mysqlHome 'bin\mysqld.exe'))) {
    Write-Host 'Extracting MySQL...'
    Expand-Archive -LiteralPath (Join-Path $downloadRoot 'mysql-8.0.43-winx64.zip') `
        -DestinationPath (Join-Path $ToolsRoot 'mysql') -Force
}

$redisServer = Join-Path $ToolsRoot 'redis\redis-server.exe'
if (-not (Test-Path -LiteralPath $redisServer)) {
    Write-Host 'Extracting Redis...'
    Expand-Archive -LiteralPath (Join-Path $downloadRoot 'Redis-x64-5.0.14.1.zip') `
        -DestinationPath (Join-Path $ToolsRoot 'redis') -Force
}

$minioPath = Join-Path $ToolsRoot 'minio\minio.exe'
if (-not (Test-Path -LiteralPath $minioPath)) {
    $minioDir = Split-Path -Parent $minioPath
    New-Item -ItemType Directory -Force -Path $minioDir | Out-Null
    Copy-Item -LiteralPath (Join-Path $downloadRoot 'minio-community.exe') `
        -Destination $minioPath -Force
}

$expectedMinioHash = '5dd4fdc24e3f583f158bdf51671efdd11b4cc7a33d2bf47aa178403d9e5c03a8'
$actualMinioHash = (Get-FileHash -Algorithm SHA256 -LiteralPath $minioPath).Hash.ToLowerInvariant()
if ($actualMinioHash -ne $expectedMinioHash) {
    throw 'MinIO community checksum mismatch.'
}

Write-Host 'Portable infrastructure is ready.'
Write-Host 'Next: .\scripts\dev\start-demo.ps1'
