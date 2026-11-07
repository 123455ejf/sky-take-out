$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'infra-common.ps1')

$h5Root = Join-Path $RepoRoot 'customer-h5'
Set-Location $h5Root
$env:SKY_API_TARGET = 'http://127.0.0.1:18080'
if (-not (Test-Path -LiteralPath (Join-Path $h5Root 'node_modules'))) {
    Write-Host 'Installing customer H5 dependencies...'
    & npm install
    if ($LASTEXITCODE -ne 0) {
        throw 'npm install failed for customer-h5.'
    }
}

Write-Host 'Starting customer H5 on http://127.0.0.1:8082'
& npm run serve
