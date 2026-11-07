$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'infra-common.ps1')

$adminRoot = Join-Path $RepoRoot 'admin-web'
Set-Location $adminRoot
$env:SKY_API_TARGET = 'http://127.0.0.1:18080'
if (-not (Test-Path -LiteralPath (Join-Path $adminRoot 'node_modules'))) {
    Write-Host 'Installing admin dependencies...'
    & npm install
    if ($LASTEXITCODE -ne 0) {
        throw 'npm install failed for admin-web.'
    }
}

Write-Host 'Starting admin-web on http://127.0.0.1:8081'
& npm run serve
