param([switch]$ShowWindows)

$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'infra-common.ps1')
Initialize-RuntimeDirectories

$mysqlExe = Join-Path $MySqlBin 'mysqld.exe'
$mysqlAdminExe = Join-Path $MySqlBin 'mysqladmin.exe'
$mysqlClientExe = Join-Path $MySqlBin 'mysql.exe'
$redisExe = Join-Path $RedisHome 'redis-server.exe'

foreach ($required in @($mysqlExe, $mysqlAdminExe, $mysqlClientExe, $redisExe, $MinioExe)) {
    if (-not (Test-Path -LiteralPath $required)) {
        throw "Missing file: $required"
    }
}

if (-not (Test-TcpPort -Port 3306)) {
    $mysqlSystemTable = Join-Path $MySqlData 'mysql'
    if (-not (Test-Path -LiteralPath $mysqlSystemTable)) {
        Write-Host 'Initializing MySQL data directory...'
        & $mysqlExe --initialize-insecure "--basedir=$MySqlHome" "--datadir=$MySqlData" --console
        if ($LASTEXITCODE -ne 0) {
            throw 'MySQL initialization failed.'
        }
    }

    $mysqlLog = Join-Path $LogRoot 'mysql.err.log'
    $mysqlArgs = "--basedir=`"$MySqlHome`" --datadir=`"$MySqlData`" --port=3306 --bind-address=127.0.0.1 --log-error=`"$mysqlLog`" --console"
    Start-ManagedProcess -Name 'mysql' -FilePath $mysqlExe -ArgumentList $mysqlArgs `
        -WorkingDirectory $MySqlBin -ShowWindow:$ShowWindows | Out-Null

    if (-not (Wait-TcpPort -Port 3306 -TimeoutSeconds 90)) {
        throw 'MySQL did not start within 90 seconds.'
    }
} else {
    Write-Host 'MySQL port 3306 is already available.'
}

$noPassword = $false
try {
    & $mysqlClientExe -uroot --connect-timeout=3 -e 'SELECT 1;' 2>$null | Out-Null
    $noPassword = ($LASTEXITCODE -eq 0)
} catch {
    $noPassword = $false
}

if ($noPassword) {
    Write-Host 'Setting local MySQL root password...'
    & $mysqlClientExe -uroot -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root'; FLUSH PRIVILEGES;"
    if ($LASTEXITCODE -ne 0) {
        throw 'Failed to set MySQL root password.'
    }
}

$sqlPath = (Join-Path $ProcessRoot 'sql\init.sql').Replace('\', '/')
& $mysqlClientExe -uroot "--password=root" '--default-character-set=utf8mb4' -e "source $sqlPath"
if ($LASTEXITCODE -ne 0) {
    throw 'Failed to execute sql/init.sql.'
}
Write-Host 'MySQL schema is ready.'

if (-not (Test-TcpPort -Port 6379)) {
    $redisLog = Join-Path $LogRoot 'redis.log'
    $redisArgs = "--port 6379 --bind 127.0.0.1 --dir `"$RedisData`" --logfile `"$redisLog`" --appendonly no --save 3600 1"
    Start-ManagedProcess -Name 'redis' -FilePath $redisExe -ArgumentList $redisArgs `
        -WorkingDirectory $RedisHome -ShowWindow:$ShowWindows | Out-Null

    if (-not (Wait-TcpPort -Port 6379 -TimeoutSeconds 30)) {
        throw 'Redis did not start within 30 seconds.'
    }
} else {
    Write-Host 'Redis port 6379 is already available.'
}

if (-not (Test-TcpPort -Port 19000)) {
    $env:MINIO_ROOT_USER = 'minioadmin'
    $env:MINIO_ROOT_PASSWORD = 'minioadmin'
    $minioArgs = "server `"$MinioData`" --address 127.0.0.1:19000 --console-address 127.0.0.1:19001"
    Start-ManagedProcess -Name 'minio' -FilePath $MinioExe -ArgumentList $minioArgs `
        -WorkingDirectory $MinioData -ShowWindow:$ShowWindows | Out-Null

    if (-not (Wait-TcpPort -Port 19000 -TimeoutSeconds 30)) {
        throw 'MinIO did not start within 30 seconds.'
    }
} else {
    Write-Host 'MinIO port 19000 is already available.'
}

Write-Host ''
Write-Host 'Infrastructure ready:'
Write-Host '  MySQL   127.0.0.1:3306  root / root'
Write-Host '  Redis   127.0.0.1:6379'
Write-Host '  MinIO   127.0.0.1:19000  console http://127.0.0.1:19001'
Write-Host '  MinIO   minioadmin / minioadmin'
