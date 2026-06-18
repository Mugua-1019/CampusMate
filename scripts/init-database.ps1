param(
    [string]$HostName = "127.0.0.1",
    [int]$Port = 3306,
    [string]$User = "root",
    [string]$Password = "123456",
    [string]$Mysql = "mysql"
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$homepageSchema = Join-Path $root "database\schema\001_homepage.sql"
$profileSchema = Join-Path $root "database\schema\003_profile_center.sql"
$authCenterSchema = Join-Path $root "database\schema\004_auth_center.sql"
$seed = Join-Path $root "database\seed\001_homepage_seed.sql"
$authCenterSeed = Join-Path $root "database\seed\002_auth_center_seed.sql"

if (-not (Test-Path $homepageSchema)) {
    throw "Schema file not found: $homepageSchema"
}
if (-not (Test-Path $profileSchema)) {
    throw "Schema file not found: $profileSchema"
}
if (-not (Test-Path $authCenterSchema)) {
    throw "Schema file not found: $authCenterSchema"
}
if (-not (Test-Path $seed)) {
    throw "Seed file not found: $seed"
}
if (-not (Test-Path $authCenterSeed)) {
    throw "Seed file not found: $authCenterSeed"
}

& $Mysql "-h$HostName" "-P$Port" "-u$User" "-p$Password" -e "create database if not exists campusmate default character set utf8mb4 default collate utf8mb4_unicode_ci;"
& $Mysql "-h$HostName" "-P$Port" "-u$User" "-p$Password" campusmate -e "drop table if exists auth_center_phone_code, auth_center_record, auth_center_right, auth_center_material_sample, auth_center_benefit, profile_publish, profile_recent_chat, profile_safety_item, profile_safety, profile_activity_bar, profile_activity_summary, profile_preference, profile_campus_verify, home_post, home_pending_meet, home_user_stat, home_user_summary, user_profile, user_account, home_plaza_category, home_plaza_tab;"
& $Mysql "-h$HostName" "-P$Port" "-u$User" "-p$Password" campusmate --default-character-set=utf8mb4 -e "source $homepageSchema"
& $Mysql "-h$HostName" "-P$Port" "-u$User" "-p$Password" campusmate --default-character-set=utf8mb4 -e "source $profileSchema"
& $Mysql "-h$HostName" "-P$Port" "-u$User" "-p$Password" campusmate --default-character-set=utf8mb4 -e "source $authCenterSchema"
& $Mysql "-h$HostName" "-P$Port" "-u$User" "-p$Password" campusmate --default-character-set=utf8mb4 -e "source $seed"
& $Mysql "-h$HostName" "-P$Port" "-u$User" "-p$Password" campusmate --default-character-set=utf8mb4 -e "source $authCenterSeed"

Write-Host "CampusMate database initialized: campusmate"
