use campusmate;

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

insert into profile_campus_verify
(user_id, title, description, action_text)
select
    p.user_id,
    '校园认证',
    case
        when p.verified = 1 then '你已完成校园认证，功能权限已解锁。'
        else '完成校园认证后，解锁更多功能和信任标识。'
    end,
    case
        when p.verified = 1 then '查看认证'
        else '去认证'
    end
from user_profile p
where not exists (
    select 1 from profile_campus_verify v where v.user_id = p.user_id
);

insert into profile_activity_summary
(user_id, activity_score, percentile)
select
    p.user_id,
    case when p.verified = 1 then 42 else 18 end,
    case when p.verified = 1 then 68 else 25 end
from user_profile p
where not exists (
    select 1 from profile_activity_summary s where s.user_id = p.user_id
);

insert into profile_safety
(user_id, status_label, credit_score, safety_level)
select
    p.user_id,
    case when p.verified = 1 then '状态良好' else '待认证' end,
    case when p.verified = 1 then 96 else 60 end,
    case when p.verified = 1 then '高' else '基础' end
from user_profile p
where not exists (
    select 1 from profile_safety s where s.user_id = p.user_id
);

insert into profile_preference
(user_id, label, icon, score, tone, sort_weight)
select p.user_id, d.label, d.icon, d.score, d.tone, d.sort_weight
from user_profile p
join (
    select '学习搭子' as label, 'reading' as icon, 6 as score, 'purple' as tone, 10 as sort_weight
    union all select '运动搭子', 'promotion', 5, 'coral', 20
    union all select '倾听搭子', 'headset', 4, 'pink', 30
) d
where not exists (
    select 1 from profile_preference x where x.user_id = p.user_id
);

insert into profile_activity_bar
(user_id, day_label, activity_value, sort_weight)
select p.user_id, d.day_label, d.activity_value, d.sort_weight
from user_profile p
join (
    select '一' as day_label, 32 as activity_value, 10 as sort_weight
    union all select '二', 44, 20
    union all select '三', 52, 30
    union all select '四', 40, 40
    union all select '五', 26, 50
    union all select '六', 38, 60
    union all select '日', 42, 70
) d
where not exists (
    select 1 from profile_activity_bar x where x.user_id = p.user_id
);

insert into profile_safety_item
(user_id, item_text, sort_weight)
select p.user_id, d.item_text, d.sort_weight
from user_profile p
join (
    select '已同步账号资料与认证状态' as item_text, 10 as sort_weight
    union all select '异常行为会影响信用分', 20
    union all select '完善资料可提升匹配可信度', 30
) d
where not exists (
    select 1 from profile_safety_item x where x.user_id = p.user_id
);
