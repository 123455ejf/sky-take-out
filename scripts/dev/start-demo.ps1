param([switch]$NoBrowser)

$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'infra-common.ps1')
Initialize-RuntimeDirectories

& (Join-Path $PSScriptRoot 'start-infra.ps1') -ShowWindows

$jar = Join-Path $ProcessRoot 'server\sky-server\target\sky-server-1.0.0.jar'
if (-not (Test-Path -LiteralPath $jar)) {
    $maven = Get-Command mvn -ErrorAction SilentlyContinue
    if (-not $maven) {
        $localMaven = Join-Path $ProcessRoot '.tools\apache-maven-3.9.9\bin\mvn.cmd'
        if (Test-Path -LiteralPath $localMaven) {
            $maven = Get-Command $localMaven
        }
    }
    if (-not $maven) {
        throw 'Maven not found. Build the backend jar first.'
    }
    Set-Location (Join-Path $ProcessRoot 'server')
    & $maven.Source -DskipTests package
    if ($LASTEXITCODE -ne 0) {
        throw 'Backend build failed.'
    }
}

$powershellExe = Join-Path $env:SystemRoot 'System32\WindowsPowerShell\v1.0\powershell.exe'
$windows = @(
    @{ Name = 'backend'; Script = 'run-backend.ps1' },
    @{ Name = 'admin'; Script = 'run-admin.ps1' },
    @{ Name = 'h5'; Script = 'run-h5.ps1' }
)

foreach ($item in $windows) {
    $scriptPath = Join-Path $PSScriptRoot $item.Script
    $arguments = "-NoExit -NoProfile -ExecutionPolicy Bypass -File `"$scriptPath`""
    $startInfo = New-Object System.Diagnostics.ProcessStartInfo
    $startInfo.FileName = $powershellExe
    $startInfo.Arguments = $arguments
    $startInfo.WorkingDirectory = $ProcessRoot
    $startInfo.UseShellExecute = $true
    $startInfo.WindowStyle = [System.Diagnostics.ProcessWindowStyle]::Normal
    $process = [System.Diagnostics.Process]::Start($startInfo)
    Set-PidFile -Name $item.Name -ProcessId $process.Id
}

if (-not (Wait-TcpPort -Port 18080 -TimeoutSeconds 90)) {
    throw 'Backend did not start within 90 seconds. Check the backend window.'
}
if (-not (Wait-TcpPort -Port 8081 -TimeoutSeconds 60)) {
    throw 'Admin web did not start within 60 seconds. Check the admin window.'
}
if (-not (Wait-TcpPort -Port 8082 -TimeoutSeconds 60)) {
    throw 'Customer H5 did not start within 60 seconds. Check the H5 window.'
}

Write-Host ''
Write-Host 'Demo is ready:'
Write-Host '  Admin:  http://127.0.0.1:8081/#/login'
Write-Host '  H5:     http://127.0.0.1:8082/#/home'
Write-Host '  API:    http://127.0.0.1:18080/doc.html'
Write-Host '  Login:  admin / 123456'

if (-not $NoBrowser) {
    Start-Process 'http://127.0.0.1:8081/#/login'
    Start-Process 'http://127.0.0.1:8082/#/home'
}
