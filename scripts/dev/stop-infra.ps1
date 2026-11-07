$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'infra-common.ps1')
Initialize-RuntimeDirectories

foreach ($name in @('minio', 'redis', 'mysql')) {
    Stop-ManagedProcess -Name $name
    Write-Host "$name stopped."
}
