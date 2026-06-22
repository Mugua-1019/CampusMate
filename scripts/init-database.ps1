param(
    [string]$HostName = "127.0.0.1",
    [int]$Port = 3306,
    [string]$User = "root",
    [string]$Password = "123456",
    [string]$Mysql = "mysql"
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$initSql = Join-Path $root "database\init.sql"

if (-not (Test-Path $initSql)) {
    throw "Init SQL file not found: $initSql"
}

& $Mysql "-h$HostName" "-P$Port" "-u$User" "-p$Password" --default-character-set=utf8mb4 -e "source $initSql"

Write-Host "CampusMate database initialized: campusmate"
