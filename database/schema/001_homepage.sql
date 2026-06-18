create table if not exists home_plaza_tab (
    id bigint primary key auto_increment,
    plaza_key varchar(20) not null unique,
    label varchar(40) not null,
    description varchar(120) not null,
    sort_weight int not null default 0,
    enabled tinyint(1) not null default 1,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp
) comment '首页广场 Tab 测试配置表';

create table if not exists home_plaza_category (
    id bigint primary key auto_increment,
    plaza_key varchar(20) not null,
    category_name varchar(40) not null,
    sort_weight int not null default 0,
    enabled tinyint(1) not null default 1,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    unique key uk_home_plaza_category (plaza_key, category_name),
    index idx_home_plaza_category_key (plaza_key, sort_weight)
) comment '首页广场分类测试配置表';

create table if not exists home_user_summary (
    user_id bigint primary key,
    verified tinyint(1) not null default 0,
    nickname varchar(60) not null,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp
) comment '首页右侧用户摘要测试表';

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

create table if not exists home_user_stat (
    id bigint primary key auto_increment,
    user_id bigint not null,
    stat_label varchar(40) not null,
    stat_value int not null default 0,
    sort_weight int not null default 0,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    index idx_home_user_stat_user (user_id, sort_weight)
) comment '首页用户状态统计测试表';

create table if not exists home_pending_meet (
    id bigint primary key auto_increment,
    user_id bigint not null,
    title varchar(80) not null,
    partner varchar(60) not null,
    category varchar(40) not null,
    meet_time varchar(80) not null,
    location varchar(120) not null,
    status varchar(40) not null,
    active tinyint(1) not null default 1,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    index idx_home_pending_meet_user (user_id, active, id)
) comment '首页待确认见面信息测试表';

create table if not exists home_post (
    id bigint primary key auto_increment,
    plaza varchar(20) not null comment 'match or vent',
    category varchar(40) not null,
    title varchar(80) not null,
    status varchar(20) not null default 'matching',
    tags varchar(255) null,
    description varchar(500) not null,
    expected_time varchar(80) not null,
    expected_location varchar(120) not null,
    publisher_name varchar(60) not null,
    avatar_text varchar(10) not null,
    anonymous tinyint(1) not null default 0,
    verified tinyint(1) not null default 0,
    current_count int not null default 0,
    max_count int not null default 1,
    review_status varchar(20) not null default 'visible',
    sort_weight int not null default 0,
    deleted tinyint(1) not null default 0,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    index idx_home_post_plaza_category (plaza, category),
    index idx_home_post_review (review_status, deleted),
    index idx_home_post_sort (sort_weight, id)
) comment '首页广场便利贴需求测试表';
