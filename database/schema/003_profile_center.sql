create table if not exists profile_campus_verify (
    user_id bigint primary key,
    title varchar(40) not null,
    description varchar(200) not null,
    action_text varchar(30) not null,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp
) comment '个人中心校园认证提示表';

create table if not exists profile_preference (
    id bigint primary key auto_increment,
    user_id bigint not null,
    label varchar(40) not null,
    icon varchar(30) not null,
    score int not null default 0,
    tone varchar(20) not null,
    sort_weight int not null default 0,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    index idx_profile_preference_user (user_id, sort_weight)
) comment '个人中心偏好搭子展示表';

create table if not exists profile_activity_summary (
    user_id bigint primary key,
    activity_score int not null default 0,
    percentile int not null default 0,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp
) comment '个人中心活跃度摘要表';

create table if not exists profile_activity_bar (
    id bigint primary key auto_increment,
    user_id bigint not null,
    day_label varchar(10) not null,
    activity_value int not null default 0,
    sort_weight int not null default 0,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    index idx_profile_activity_bar_user (user_id, sort_weight)
) comment '个人中心周活跃柱状图表';

create table if not exists profile_safety (
    user_id bigint primary key,
    status_label varchar(30) not null,
    credit_score int not null default 0,
    safety_level varchar(30) not null,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp
) comment '个人中心信用安全状态表';

create table if not exists profile_safety_item (
    id bigint primary key auto_increment,
    user_id bigint not null,
    item_text varchar(80) not null,
    sort_weight int not null default 0,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    index idx_profile_safety_item_user (user_id, sort_weight)
) comment '个人中心信用安全说明表';

create table if not exists profile_recent_chat (
    id bigint primary key auto_increment,
    user_id bigint not null,
    avatar_text varchar(10) not null,
    display_name varchar(60) not null,
    tag varchar(40) not null,
    message varchar(200) not null,
    display_time varchar(30) not null,
    unread_count int null,
    tone varchar(20) not null,
    sort_weight int not null default 0,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    index idx_profile_recent_chat_user (user_id, sort_weight)
) comment '个人中心最近聊天与匹配表';

create table if not exists profile_publish (
    id bigint primary key auto_increment,
    user_id bigint not null,
    short_label varchar(10) not null,
    title varchar(80) not null,
    tag varchar(40) not null,
    period varchar(30) not null,
    description varchar(300) not null,
    location varchar(120) not null,
    display_time varchar(40) not null,
    matched_count int not null default 0,
    tone varchar(20) not null,
    sort_weight int not null default 0,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    index idx_profile_publish_user (user_id, sort_weight)
) comment '个人中心我的发布展示表';
