$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'infra-common.ps1')
Initialize-RuntimeDirectories

foreach ($name in @('h5', 'admin', 'backend')) {
    Stop-ManagedProcess -Name $name
    Write-Host "$name stopped."
}

& (Join-Path $PSScriptRoot 'stop-infra.ps1')
