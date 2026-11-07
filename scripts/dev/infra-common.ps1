$script:RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
$script:AsciiRoot = 'D:\sky-take-out-demo'
$script:ProcessRoot = if (Test-Path -LiteralPath $AsciiRoot) { $AsciiRoot } else { $RepoRoot }
$script:ToolsRoot = Join-Path $ProcessRoot '.tools\infra'
$script:RuntimeRoot = Join-Path $ProcessRoot '.runtime'
$script:MySqlHome = Join-Path $ToolsRoot 'mysql\mysql-8.0.43-winx64'
$script:MySqlBin = Join-Path $MySqlHome 'bin'
$script:MySqlData = Join-Path $RuntimeRoot 'mysql\data'
$script:RedisHome = Join-Path $ToolsRoot 'redis'
$script:RedisData = Join-Path $RuntimeRoot 'redis'
$script:MinioExe = Join-Path $ToolsRoot 'minio\minio.exe'
$script:MinioData = Join-Path $RuntimeRoot 'minio-community'
$script:PidRoot = Join-Path $RuntimeRoot 'pids'
$script:LogRoot = Join-Path $RuntimeRoot 'logs'

function Initialize-RuntimeDirectories {
    $paths = @(
        $MySqlData,
        $RedisData,
        $MinioData,
        $PidRoot,
        $LogRoot,
        (Join-Path $RuntimeRoot 'mysql\files')
    )
    foreach ($path in $paths) {
        New-Item -ItemType Directory -Force -Path $path | Out-Null
    }
}

function Test-TcpPort {
    param(
        [Parameter(Mandatory = $true)][int]$Port,
        [string]$HostName = '127.0.0.1'
    )

    $client = New-Object System.Net.Sockets.TcpClient
    try {
        $task = $client.ConnectAsync($HostName, $Port)
        return $task.Wait(1200) -and $client.Connected
    } catch {
        return $false
    } finally {
        $client.Dispose()
    }
}

function Wait-TcpPort {
    param(
        [Parameter(Mandatory = $true)][int]$Port,
        [int]$TimeoutSeconds = 60
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        if (Test-TcpPort -Port $Port) {
            return $true
        }
        Start-Sleep -Milliseconds 500
    }
    return $false
}

function Set-PidFile {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [int]$ProcessId = 0
    )
    Set-Content -LiteralPath (Join-Path $PidRoot "$Name.pid") -Value $ProcessId -Encoding ASCII
}

function Get-PidFile {
    param([Parameter(Mandatory = $true)][string]$Name)

    $path = Join-Path $PidRoot "$Name.pid"
    if (-not (Test-Path -LiteralPath $path)) {
        return 0
    }
    $value = Get-Content -LiteralPath $path -Raw -ErrorAction SilentlyContinue
    $number = 0
    if ([int]::TryParse($value.Trim(), [ref]$number)) {
        return $number
    }
    return 0
}

function Start-ManagedProcess {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][string]$FilePath,
        [string]$ArgumentList = '',
        [string]$WorkingDirectory = $RepoRoot,
        [switch]$ShowWindow
    )

    $startInfo = New-Object System.Diagnostics.ProcessStartInfo
    $startInfo.FileName = $FilePath
    $startInfo.Arguments = $ArgumentList
    $startInfo.WorkingDirectory = $WorkingDirectory
    $startInfo.UseShellExecute = $true
    $startInfo.WindowStyle = if ($ShowWindow) {
        [System.Diagnostics.ProcessWindowStyle]::Normal
    } else {
        [System.Diagnostics.ProcessWindowStyle]::Hidden
    }

    $process = [System.Diagnostics.Process]::Start($startInfo)
    Set-PidFile -Name $Name -ProcessId $process.Id
    return $process
}

function Stop-ManagedProcess {
    param([Parameter(Mandatory = $true)][string]$Name)

    $processId = Get-PidFile -Name $Name
    if ($processId -le 0) {
        return
    }

    $process = Get-CimInstance Win32_Process -Filter "ProcessId = $processId" -ErrorAction SilentlyContinue
    if ($process) {
        $commandLine = $process.CommandLine
        if ($commandLine -and ($commandLine.Contains($RepoRoot) -or $commandLine.Contains($ProcessRoot))) {
            & "$env:SystemRoot\System32\taskkill.exe" /PID $processId /T /F | Out-Null
        }
    }
    Set-PidFile -Name $Name -ProcessId 0
}

function Show-ManagedStatus {
    param([Parameter(Mandatory = $true)][string]$Name)

    $processId = Get-PidFile -Name $Name
    $running = $false
    if ($processId -gt 0) {
        $running = $null -ne (Get-Process -Id $processId -ErrorAction SilentlyContinue)
    }
    $state = if ($running) { 'RUNNING' } else { 'STOPPED' }
    Write-Host ("{0,-12} {1,-8} PID={2}" -f $Name, $state, $processId)
}
