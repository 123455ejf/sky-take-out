$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'infra-common.ps1')
Initialize-RuntimeDirectories

Write-Host 'Port status:'
foreach ($item in @(
    @{ Name = 'MySQL'; Port = 3306 },
    @{ Name = 'Redis'; Port = 6379 },
    @{ Name = 'MinIO'; Port = 19000 },
    @{ Name = 'Backend'; Port = 18080 },
    @{ Name = 'Admin'; Port = 8081 },
    @{ Name = 'H5'; Port = 8082 }
)) {
    $state = if (Test-TcpPort -Port $item.Port) { 'LISTENING' } else { 'CLOSED' }
    Write-Host ("  {0,-8} {1,-10} {2}" -f $item.Name, $state, $item.Port)
}

Write-Host ''
Write-Host 'Managed processes:'
foreach ($name in @('mysql', 'redis', 'minio', 'backend', 'admin', 'h5')) {
    Show-ManagedStatus -Name $name
}
