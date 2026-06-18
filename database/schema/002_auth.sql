create table if not exists user_account (
    user_id bigint primary key auto_increment,
    account varchar(80) not null,
    phone varchar(20) null,
    email varchar(120) null,
    password_salt varchar(64) not null,
    password_hash varchar(64) not null,
    enabled tinyint(1) not null default 1,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    unique key uk_user_account_account (account),
    unique key uk_user_account_phone (phone),
    unique key uk_user_account_email (email)
) comment '用户登录账号表';

create table if not exists user_profile (
    user_id bigint primary key,
    avatar_url varchar(255) null,
    nickname varchar(60) not null,
    gender varchar(20) null,
    grade varchar(20) null,
    college varchar(80) null,
    major varchar(80) null,
    interest_tags varchar(255) null,
    bio varchar(500) null,
    real_name varchar(60) null,
    verified tinyint(1) not null default 0,
    verify_status varchar(20) not null default 'unverified',
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp
) comment '个人中心展示资料与认证状态表';
