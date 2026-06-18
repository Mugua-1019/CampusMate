create database if not exists campusmate
  default character set utf8mb4
  default collate utf8mb4_unicode_ci;

use campusmate;

drop table if exists home_post;
drop table if exists home_pending_meet;
drop table if exists home_user_stat;
drop table if exists home_user_summary;
drop table if exists profile_publish;
drop table if exists profile_recent_chat;
drop table if exists profile_safety_item;
drop table if exists profile_safety;
drop table if exists profile_activity_bar;
drop table if exists profile_activity_summary;
drop table if exists profile_preference;
drop table if exists profile_campus_verify;
drop table if exists user_profile;
drop table if exists user_account;
drop table if exists home_plaza_category;
drop table if exists home_plaza_tab;

source ../schema/001_homepage.sql;
source ../schema/003_profile_center.sql;
source ../seed/001_homepage_seed.sql;
