$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'infra-common.ps1')

$jar = Join-Path $ProcessRoot 'server\sky-server\target\sky-server-1.0.0.jar'
if (-not (Test-Path -LiteralPath $jar)) {
    throw "Backend jar not found: $jar"
}

$env:MYSQL_HOST = '127.0.0.1'
$env:MYSQL_PORT = '3306'
$env:MYSQL_DATABASE = 'sky_take_out'
$env:MYSQL_USERNAME = 'root'
$env:MYSQL_PASSWORD = 'root'
$env:REDIS_HOST = '127.0.0.1'
$env:REDIS_PORT = '6379'
$env:SERVER_PORT = '18080'
$env:MINIO_ENDPOINT = 'http://127.0.0.1:19000'
$env:MINIO_ACCESS_KEY = 'minioadmin'
$env:MINIO_SECRET_KEY = 'minioadmin'
$env:MINIO_BUCKET = 'sky-take-out'
$env:MINIO_PUBLIC_URL = 'http://127.0.0.1:19000'
$env:LOGGING_FILE_NAME = Join-Path $LogRoot 'backend.log'

Set-Location $ProcessRoot
Write-Host 'Starting backend on http://127.0.0.1:18080'
& java -jar $jar
