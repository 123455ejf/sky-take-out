$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'infra-common.ps1')

$base = 'http://127.0.0.1:18080'
$health = Invoke-RestMethod -Uri "$base/health" -TimeoutSec 10
if ($health.code -ne 1) {
    throw 'Backend health check failed.'
}

$loginBody = @{ username = 'admin'; password = '123456' } | ConvertTo-Json
$login = Invoke-RestMethod -Method Post -Uri "$base/admin/employee/login" `
    -ContentType 'application/json' -Body $loginBody -TimeoutSec 10
if ($login.code -ne 1) {
    throw "Login failed: $($login.msg)"
}
$token = $login.data.token
$headers = @{ token = $token }
Write-Host 'PASS  login'

$page = Invoke-RestMethod -Uri "$base/admin/employee/page?page=1&pageSize=10" `
    -Headers $headers -TimeoutSec 10
if ($page.code -ne 1) {
    throw "Employee page failed: $($page.msg)"
}
Write-Host "PASS  employee page total=$($page.data.total)"

$suffix = Get-Date -Format 'HHmmss'
$username = "demo_$suffix"
$createBody = @{
    username = $username
    name = '联调员工'
    phone = '13800138002'
    sex = '男'
    idNumber = '110101199001011234'
} | ConvertTo-Json
$created = Invoke-RestMethod -Method Post -Uri "$base/admin/employee" `
    -Headers $headers -ContentType 'application/json' -Body $createBody -TimeoutSec 10
if ($created.code -ne 1) {
    throw "Create employee failed: $($created.msg)"
}
Write-Host "PASS  create employee $username"

$encodedName = [uri]::EscapeDataString('联调员工')
$search = Invoke-RestMethod -Uri "$base/admin/employee/page?page=1&pageSize=10&name=$encodedName" `
    -Headers $headers -TimeoutSec 10
$employee = $search.data.records | Where-Object { $_.username -eq $username } | Select-Object -First 1
if (-not $employee) {
    throw 'Created employee was not found.'
}
Write-Host "PASS  query employee id=$($employee.id)"

$updateBody = @{
    id = $employee.id
    username = $username
    name = '联调员工已更新'
    phone = '13900139000'
    sex = '男'
    idNumber = '110101199001011234'
} | ConvertTo-Json
$updated = Invoke-RestMethod -Method Put -Uri "$base/admin/employee" `
    -Headers $headers -ContentType 'application/json' -Body $updateBody -TimeoutSec 10
if ($updated.code -ne 1) {
    throw "Update employee failed: $($updated.msg)"
}
Write-Host 'PASS  update employee'

$disabled = Invoke-RestMethod -Method Put -Uri "$base/admin/employee/status/0?id=$($employee.id)" `
    -Headers $headers -TimeoutSec 10
if ($disabled.code -ne 1) {
    throw "Disable employee failed: $($disabled.msg)"
}
Write-Host 'PASS  disable employee'

$asset = Join-Path $RepoRoot 'assets\1107_上传联调示例_v1.svg'
$uploadRaw = & curl.exe -sS -X POST -H "token: $token" -F "file=@$asset" "$base/admin/common/upload"
if ($LASTEXITCODE -ne 0) {
    throw 'File upload request failed.'
}
$upload = $uploadRaw | ConvertFrom-Json
if ($upload.code -ne 1) {
    throw "Upload failed: $($upload.msg)"
}
$download = Invoke-WebRequest -Uri $upload.data -UseBasicParsing -TimeoutSec 10
if ($download.StatusCode -ne 200) {
    throw 'Uploaded file is not publicly readable.'
}
Write-Host "PASS  MinIO upload $($upload.data)"

$logout = Invoke-RestMethod -Method Post -Uri "$base/admin/employee/logout" `
    -Headers $headers -TimeoutSec 10
if ($logout.code -ne 1) {
    throw "Logout failed: $($logout.msg)"
}
$blacklistVerified = $false
try {
    Invoke-RestMethod -Uri "$base/admin/employee/page?page=1&pageSize=10" `
        -Headers $headers -TimeoutSec 10 | Out-Null
} catch {
    $blacklistVerified = $true
}
if (-not $blacklistVerified) {
    throw 'Blacklisted token was still accepted.'
}
Write-Host 'PASS  logout and Redis blacklist'
Write-Host 'ALL CHECKS PASSED'
