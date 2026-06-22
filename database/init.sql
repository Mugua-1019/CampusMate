-- CampusMate one-shot database initialization script
-- Consolidated database schema and demo data.
-- Run with: mysql --default-character-set=utf8mb4 -u root -p < database/init.sql

create database if not exists campusmate
  default character set utf8mb4
  default collate utf8mb4_unicode_ci;

use campusmate;

set foreign_key_checks = 0;
drop table if exists home_post_reply;
drop table if exists home_notification;
drop table if exists home_post_report;
drop table if exists auth_center_phone_code;
drop table if exists auth_center_record;
drop table if exists auth_center_right;
drop table if exists auth_center_material_sample;
drop table if exists auth_center_benefit;
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
set foreign_key_checks = 1;

-- ============================================================
-- Section: homepage schema
-- ============================================================

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
    publisher_user_id bigint null,
    category varchar(40) not null,
    title varchar(80) not null,
    status varchar(20) not null default 'matching',
    tags varchar(255) null,
    description varchar(500) not null,
    expected_time varchar(80) not null,
    expected_location varchar(120) not null,
    publisher_name varchar(60) not null,
    publisher_status varchar(40) null,
    publisher_status_note varchar(160) null,
    avatar_text varchar(10) not null,
    anonymous tinyint(1) not null default 0,
    verified tinyint(1) not null default 0,
    current_count int not null default 0,
    max_count int not null default 1,
    current_state varchar(255) null,
    hope_you_can varchar(255) null,
    preferred_way varchar(255) null,
    gentle_replies varchar(1200) null,
    review_status varchar(20) not null default 'visible',
    sort_weight int not null default 0,
    deleted tinyint(1) not null default 0,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    index idx_home_post_plaza_category (plaza, category),
    index idx_home_post_review (review_status, deleted),
    index idx_home_post_sort (sort_weight, id)
) comment '首页广场便利贴需求测试表';

-- ============================================================
-- Section: auth schema
-- ============================================================

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

-- ============================================================
-- Section: profile center schema
-- ============================================================

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

-- ============================================================
-- Section: auth center schema
-- ============================================================

create table if not exists auth_center_benefit (
    benefit_id bigint primary key auto_increment,
    label varchar(40) not null,
    icon_key varchar(40) not null,
    sort_weight int not null default 0,
    enabled tinyint(1) not null default 1,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    unique key uk_auth_center_benefit_label (label)
) comment 'auth center top benefit config';

create table if not exists auth_center_material_sample (
    sample_id bigint primary key auto_increment,
    label varchar(40) not null,
    tone varchar(40) not null,
    sort_weight int not null default 0,
    enabled tinyint(1) not null default 1,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    unique key uk_auth_center_sample_label (label)
) comment 'auth center material sample config';

create table if not exists auth_center_right (
    right_id bigint primary key auto_increment,
    title varchar(40) not null,
    description varchar(160) not null,
    icon_key varchar(40) not null,
    sort_weight int not null default 0,
    enabled tinyint(1) not null default 1,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    unique key uk_auth_center_right_title (title)
) comment 'auth center right config';

create table if not exists auth_center_record (
    record_id bigint primary key auto_increment,
    user_id bigint not null,
    auth_type varchar(20) not null,
    school varchar(80) null,
    student_no varchar(40) null,
    real_name varchar(60) null,
    identity_type varchar(40) null,
    phone varchar(20) null,
    material_name varchar(120) null,
    material_url varchar(255) null,
    status varchar(20) not null,
    verified tinyint(1) not null default 0,
    feedback varchar(500) not null,
    action_text varchar(30) not null default '-',
    submitted_at datetime not null default current_timestamp,
    reviewed_at datetime null,
    index idx_auth_center_record_user (user_id, submitted_at),
    index idx_auth_center_record_status (auth_type, status, submitted_at)
) comment 'auth center submission and review record';

create table if not exists auth_center_phone_code (
    code_id bigint primary key auto_increment,
    user_id bigint not null,
    phone varchar(20) not null,
    code varchar(10) not null,
    used tinyint(1) not null default 0,
    expires_at datetime not null,
    created_at datetime not null default current_timestamp,
    index idx_auth_center_phone_code (user_id, phone, used, expires_at)
) comment 'auth center mock phone code';

-- ============================================================
-- Section: home post report schema
-- ============================================================

create table if not exists home_post_report (
    report_id bigint primary key auto_increment,
    post_id bigint not null,
    reporter_user_id bigint null,
    reason varchar(80) not null,
    detail varchar(1000) null,
    contact varchar(120) null,
    status varchar(20) not null default 'pending',
    admin_note varchar(500) null,
    created_at datetime not null default current_timestamp,
    handled_at datetime null,
    index idx_home_post_report_post (post_id, created_at),
    index idx_home_post_report_status (status, created_at)
) comment '匹配广场帖子举报记录表';

-- ============================================================
-- Section: home post vent fields migration
-- ============================================================

use campusmate;

drop procedure if exists add_home_post_column_if_missing;

delimiter //

create procedure add_home_post_column_if_missing(
    in column_name_value varchar(64),
    in column_definition_value varchar(512)
)
begin
    if not exists (
        select 1
        from information_schema.columns
        where table_schema = database()
          and table_name = 'home_post'
          and column_name = column_name_value
    ) then
        set @alter_sql = concat('alter table home_post add column ', column_definition_value);
        prepare stmt from @alter_sql;
        execute stmt;
        deallocate prepare stmt;
    end if;
end//

delimiter ;

call add_home_post_column_if_missing('publisher_status', 'publisher_status varchar(40) null after publisher_name');
call add_home_post_column_if_missing('publisher_status_note', 'publisher_status_note varchar(160) null after publisher_status');
call add_home_post_column_if_missing('current_state', 'current_state varchar(255) null after max_count');
call add_home_post_column_if_missing('hope_you_can', 'hope_you_can varchar(255) null after current_state');
call add_home_post_column_if_missing('preferred_way', 'preferred_way varchar(255) null after hope_you_can');
call add_home_post_column_if_missing('gentle_replies', 'gentle_replies varchar(1200) null after preferred_way');

drop procedure if exists add_home_post_column_if_missing;

update home_post
set publisher_status = '回复开放中',
    publisher_status_note = 'TA 目前希望收到回复',
    current_count = 24,
    max_count = 40,
    current_state = '有点累,想被理解,不需要建议,希望慢慢聊',
    hope_you_can = '耐心倾听,不评判,尊重隐私',
    preferred_way = '在线文字聊天,现在 - 23:30 之间都可以聊',
    gentle_replies = '我在，如果你愿意，可以慢慢说。我会认真听你说的每一句话。
你已经很勇敢了，愿意说出来就是很重要的一步。
不着急，我们可以慢慢聊，今晚你并不孤单。'
where plaza = 'vent'
  and title = '今天有点低落';

update home_post
set publisher_status = '等待倾听中',
    publisher_status_note = 'TA 想先收到一段温柔回应',
    current_count = 8,
    max_count = 20,
    current_state = '复习焦虑,节奏有点乱,想被鼓励',
    hope_you_can = '温柔鼓励,分享复习节奏,不催促',
    preferred_way = '在线文字聊天,今晚十分钟也可以',
    gentle_replies = '复习乱掉的时候会很难受，但这不代表你不够努力。
先把今晚能做的一小步做完，也已经很好了。'
where plaza = 'vent'
  and title = '复习焦虑想聊聊';

update home_post
set publisher_status = '轻松分享中',
    publisher_status_note = 'TA 想把开心的小事传递出去',
    current_count = 12,
    max_count = 30,
    current_state = '心情变好,想分享,轻松聊天',
    hope_you_can = '一起开心,简单回应,分享日常',
    preferred_way = '校园咖啡角或在线文字',
    gentle_replies = '听起来真好，这种小小的温暖很适合被记住。
谢谢你愿意把开心也分享出来。'
where plaza = 'vent'
  and title = '想分享一件小事';

update home_post
set gentle_replies = null
where plaza = 'vent';

-- ============================================================
-- Section: home post reply schema
-- ============================================================

create table if not exists home_post_reply (
    id bigint primary key auto_increment,
    post_id bigint not null,
    user_id bigint null,
    author_name varchar(80) not null,
    avatar_text varchar(8) null,
    content varchar(1000) not null,
    like_count int not null default 0,
    review_status varchar(20) not null default 'visible',
    deleted tinyint(1) not null default 0,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    index idx_home_post_reply_post (post_id, created_at, id),
    index idx_home_post_reply_status (review_status, deleted),
    constraint fk_home_post_reply_post
        foreign key (post_id) references home_post(id)
        on delete cascade
) comment '倾诉广场温柔回应表';

-- ============================================================
-- Section: home notification schema
-- ============================================================

drop procedure if exists add_home_post_column_if_missing;

delimiter //
create procedure add_home_post_column_if_missing(
    in column_name_value varchar(64),
    in column_definition_value varchar(255)
)
begin
    if not exists (
        select 1
        from information_schema.columns
        where table_schema = database()
          and table_name = 'home_post'
          and column_name = column_name_value
    ) then
        set @alter_sql = concat('alter table home_post add column ', column_definition_value);
        prepare stmt from @alter_sql;
        execute stmt;
        deallocate prepare stmt;
    end if;
end //
delimiter ;

call add_home_post_column_if_missing('publisher_user_id', 'publisher_user_id bigint null after plaza');

drop procedure if exists add_home_post_column_if_missing;

create table if not exists home_notification (
    id bigint primary key auto_increment,
    user_id bigint not null,
    notice_type varchar(30) not null,
    source_type varchar(30) not null,
    source_id bigint not null,
    aggregate_count int not null default 0,
    read_status varchar(20) not null default 'unread',
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    unique key uk_home_notification_source (user_id, notice_type, source_type, source_id),
    index idx_home_notification_user (user_id, read_status, updated_at)
) comment '首页通用提醒表';

-- ============================================================
-- Section: homepage demo data
-- ============================================================

delete from home_post_reply;
delete from home_post;
delete from home_pending_meet;
delete from home_user_stat;
delete from home_user_summary;
delete from profile_publish;
delete from profile_recent_chat;
delete from profile_safety_item;
delete from profile_safety;
delete from profile_activity_bar;
delete from profile_activity_summary;
delete from profile_preference;
delete from profile_campus_verify;
delete from user_profile;
delete from user_account;
delete from home_plaza_category;
delete from home_plaza_tab;

insert into home_plaza_tab
(plaza_key, label, description, sort_weight, enabled)
values
('match', '匹配广场', '学习、吃饭、运动、比赛和活动同行', 10, 1),
('vent', '倾诉广场', '找人聊聊、情绪陪伴和日常分享', 20, 1);

insert into home_plaza_category
(plaza_key, category_name, sort_weight, enabled)
values
('match', '全部', 0, 1),
('match', '学习搭子', 10, 1),
('match', '饭搭子', 20, 1),
('match', '运动搭子', 30, 1),
('match', '比赛组队', 40, 1),
('match', '活动同行', 50, 1),
('match', '闲聊陪伴', 60, 1),
('match', '其他', 70, 1),
('vent', '全部', 0, 1),
('vent', '想找人聊聊', 10, 1),
('vent', '心情不好', 20, 1),
('vent', '学业压力', 30, 1),
('vent', '考试焦虑', 40, 1),
('vent', '生活分享', 50, 1),
('vent', '只想被听见', 60, 1);

insert into home_user_summary
(user_id, verified, nickname)
values
(1, 0, '小星同学');

insert into user_profile
(user_id, avatar_url, nickname, gender, grade, college, major, interest_tags, bio, real_name, verified, verify_status)
values
(1, '/testimage/moren.png', '小星同学', '女', '2023级', '计算机学院', '软件工程', '自习,跑步,音乐', '想找到靠谱的校园搭子。', '陈小星', 0, 'pending');

insert into user_account
(user_id, account, phone, email, password_salt, password_hash, enabled)
values
(1, 'demo', '13800000000', 'demo@campusmate.local', 'campusmate-demo-salt', '83031fa8dc640bad4a8cea76b59a709c261dd02d818268d4bf8382c2d50fa338', 1);

insert into user_account
(user_id, account, phone, email, password_salt, password_hash, enabled)
values
(21, 'reply01', '13800000021', 'reply01@campusmate.local', 'reply-salt-1', '0c354c9ac417aea55f6f6c5dbe21ac4d6e716a498ad34e3164fed9b873c470a5', 1),
(22, 'reply02', '13800000022', 'reply02@campusmate.local', 'reply-salt-2', '7491bf95530a8f0b09435f84f0d417abcfbabc602ec88a5eeec00d94b50c583d', 1),
(23, 'reply03', '13800000023', 'reply03@campusmate.local', 'reply-salt-3', 'c9860d11c9f0e4aeca3c11aac1cdc357c33dec3eda4902dd2fd66f7c817ea5cc', 1);

insert into user_profile
(user_id, avatar_url, nickname, gender, grade, college, major, interest_tags, bio, real_name, verified, verify_status)
values
(21, '/testimage/moren.png', '微风不燥', '女', '2022级', '心理学院', '应用心理学', '倾听,阅读,散步', '愿意认真听完每一句话。', '林晓风', 1, 'approved'),
(22, '/testimage/moren.png', '星辰大海', '男', '2021级', '计算机学院', '软件工程', '自习,音乐,陪伴', '希望把一点稳定和温暖传出去。', '周星野', 1, 'approved'),
(23, '/testimage/moren.png', '云朵软软', '女', '2023级', '文学院', '汉语言文学', '写作,咖啡,聊天', '擅长慢慢聊，也尊重沉默。', '许云舒', 1, 'approved');

insert into home_user_summary
(user_id, verified, nickname)
values
(21, 1, '微风不燥'),
(22, 1, '星辰大海'),
(23, 1, '云朵软软');

insert into home_user_stat
(user_id, stat_label, stat_value, sort_weight)
values
(1, '今日可匹配', 3, 10),
(1, '进行中聊天', 2, 20),
(1, '待确认匹配', 1, 30),
(1, '已完成匹配', 8, 40);

insert into profile_campus_verify
(user_id, title, description, action_text)
values
(1, '校园认证', '完成校园认证后，解锁更多功能和信任标识。', '去认证');

insert into profile_preference
(user_id, label, icon, score, tone, sort_weight)
values
(1, '学习搭子', 'reading', 6, 'purple', 10),
(1, '运动搭子', 'promotion', 5, 'coral', 20),
(1, '倾听搭子', 'headset', 4, 'pink', 30);

insert into profile_activity_summary
(user_id, activity_score, percentile)
values
(1, 42, 68);

insert into profile_activity_bar
(user_id, day_label, activity_value, sort_weight)
values
(1, '一', 32, 10),
(1, '二', 44, 20),
(1, '三', 52, 30),
(1, '四', 40, 40),
(1, '五', 26, 50),
(1, '六', 38, 60),
(1, '日', 42, 70);

insert into profile_safety
(user_id, status_label, credit_score, safety_level)
values
(1, '良好', 92, '高');

insert into profile_safety_item
(user_id, item_text, sort_weight)
values
(1, '无违规记录', 10),
(1, '资料已保护', 20),
(1, '实名认证待完成', 30);

insert into profile_recent_chat
(user_id, avatar_text, display_name, tag, message, display_time, unread_count, tone, sort_weight)
values
(1, '风', '风来轻扬晚步', '学习搭子', '你好呀！我们可以一起复习高数。', '10:24', 2, 'warm', 10),
(1, '桐', '桐子淌水', '运动搭子', '周末有羽毛球局，要一起吗？', '昨天', 1, 'pink', 20),
(1, '书', '书山有路', '学习搭子', '分享了一份资料给你。', '05-18', null, 'green', 30);

insert into profile_publish
(user_id, short_label, title, tag, period, description, location, display_time, matched_count, tone, sort_weight)
values
(1, '学', '求高数期末复习搭子', '学习搭子', '长期', '求一起复习高数的小伙伴，互相监督，共同进步。', '图书馆附近', '2024-05-20 12:30', 3, 'study', 10),
(1, '运', '周末羽毛球运动搭子', '运动搭子', '周末', '每周六下午，有一起打羽毛球的小伙伴吗？', '体育馆', '2024-05-19 09:48', 2, 'sport', 20);

insert into home_pending_meet
(user_id, title, partner, category, meet_time, location, status, active)
values
(1, '周末操场跑步', '风一同学', '运动搭子', '明天 08:00', '操场西侧入口', '等待你确认', 1);

insert into home_post
(plaza, category, title, status, tags, description, expected_time, expected_location, publisher_name, avatar_text, anonymous, verified, current_count, max_count, review_status, sort_weight)
values
('match', '学习搭子', '今晚图书馆自习', 'matching', '自习,专注互勉,已认证', '一起到图书馆安静学习，互相监督，效率加倍。', '今晚 19:00', '图书馆三楼自习区', '小星星不熬夜', '星', 0, 1, 3, 4, 'visible', 90),
('match', '饭搭子', '二食堂午饭搭子', 'matching', '饭搭子,不赶时间', '中午一起吃饭，想试试新开的窗口，顺便聊聊天。', '今天 12:10', '第二食堂二楼', '可可不甜', '可', 0, 1, 2, 4, 'visible', 80),
('match', '运动搭子', '周末操场跑步', 'matching', '跑步,打卡', '慢跑三公里，新手友好，结束后一起拉伸。', '周六 08:00', '操场西侧入口', '风一同学', '风', 0, 1, 4, 5, 'visible', 70),
('match', '比赛组队', '数学建模缺队友', 'matching', '建模,写作,已认证', '已有两人，想找一位擅长论文排版和表达的伙伴。', '本周内', '线上 + 创客空间', '模型小队长', '模', 0, 1, 2, 3, 'visible', 60),
('match', '活动同行', '讲座一起占座', 'matching', '讲座,活动同行', '人工智能公开讲座，想找同学一起去，结束后可交流笔记。', '明晚 18:30', '大学生活动中心', '小鹿同学', '鹿', 0, 1, 1, 5, 'visible', 50),
('match', '运动搭子', '周五夜骑校园', 'full', '夜骑,放松', '微风正好，绕校园慢骑一圈，当前名额已约满。', '周五 20:00', '校门口集合', '骑行少年', '骑', 0, 1, 5, 5, 'visible', 40),
('match', '闲聊陪伴', '晚饭后湖边散步', 'matching', '闲聊,散步', '不赶路，不输出大道理，就轻松聊聊今天发生的事。', '今晚 21:00', '明湖东侧步道', '月亮邮差', '月', 0, 1, 1, 2, 'visible', 30),
('vent', '考试焦虑', '复习焦虑想聊聊', 'matching', '倾听,考试焦虑', '最近复习节奏有点乱，想找人互相鼓励十分钟。', '今晚', '线上文字', '匿名同学', '匿', 1, 0, 0, 1, 'visible', 90),
('vent', '心情不好', '今天有点低落', 'matching', '只想被听见,匿名', '不需要建议，只想有人听我把今天说完。', '随时', '线上语音前先文字', '匿名同学', '匿', 1, 0, 0, 1, 'visible', 80),
('vent', '生活分享', '想分享一件小事', 'matching', '生活分享,轻松聊', '今天遇到一件很暖的小事，想找人一起开心一下。', '今天下午', '校园咖啡角', '橘子汽水', '橘', 0, 1, 1, 2, 'visible', 70);

update home_post
set publisher_status = '回复开放中',
    publisher_status_note = 'TA 目前希望收到回复',
    current_count = 24,
    max_count = 40,
    current_state = '有点累,想被理解,不需要建议,希望慢慢聊',
    hope_you_can = '耐心倾听,不评判,尊重隐私',
    preferred_way = '在线文字聊天,现在 - 23:30 之间都可以聊',
    gentle_replies = '我在，如果你愿意，可以慢慢说。我会认真听你说的每一句话。
你已经很勇敢了，愿意说出来就是很重要的一步。
不着急，我们可以慢慢聊，今晚你并不孤单。'
where plaza = 'vent'
  and title = '今天有点低落';

update home_post
set publisher_status = '等待倾听中',
    publisher_status_note = 'TA 想先收到一段温柔回应',
    current_count = 8,
    max_count = 20,
    current_state = '复习焦虑,节奏有点乱,想被鼓励',
    hope_you_can = '温柔鼓励,分享复习节奏,不催促',
    preferred_way = '在线文字聊天,今晚十分钟也可以',
    gentle_replies = '复习乱掉的时候会很难受，但这不代表你不够努力。
先把今晚能做的一小步做完，也已经很好了。'
where plaza = 'vent'
  and title = '复习焦虑想聊聊';

update home_post
set publisher_status = '轻松分享中',
    publisher_status_note = 'TA 想把开心的小事传递出去',
    current_count = 12,
    max_count = 30,
    current_state = '心情变好,想分享,轻松聊天',
    hope_you_can = '一起开心,简单回应,分享日常',
    preferred_way = '校园咖啡角或在线文字',
    gentle_replies = '听起来真好，这种小小的温暖很适合被记住。
谢谢你愿意把开心也分享出来。'
where plaza = 'vent'
  and title = '想分享一件小事';

update home_post
set gentle_replies = null
where plaza = 'vent';

update home_post
set publisher_user_id = case title
    when '复习焦虑想聊聊' then 23
    when '今天有点低落' then 22
    when '想分享一件小事' then 21
    else publisher_user_id
end
where plaza = 'vent';

insert into home_post_reply
(post_id, user_id, author_name, avatar_text, content, like_count, review_status, deleted, created_at, updated_at)
select id, 21, '微风不燥', '风', '我在，如果你愿意，可以慢慢说。我会认真听你说的每一句话。', 2, 'visible', 0, date_sub(now(), interval 18 minute), date_sub(now(), interval 18 minute)
from home_post
where plaza = 'vent' and title = '今天有点低落'
union all
select id, 22, '星辰大海', '星', '你已经很勇敢了，愿意说出来就是很重要的一步。', 1, 'visible', 0, date_sub(now(), interval 11 minute), date_sub(now(), interval 11 minute)
from home_post
where plaza = 'vent' and title = '今天有点低落'
union all
select id, 23, '云朵软软', '云', '不着急，我们可以慢慢聊，今晚你并不孤单。', 1, 'visible', 0, date_sub(now(), interval 7 minute), date_sub(now(), interval 7 minute)
from home_post
where plaza = 'vent' and title = '今天有点低落'
union all
select id, 21, '微风不燥', '风', '复习乱掉的时候会很难受，但这不代表你不够努力。先把今晚能做的一小步做完，也已经很好了。', 3, 'visible', 0, date_sub(now(), interval 15 minute), date_sub(now(), interval 15 minute)
from home_post
where plaza = 'vent' and title = '复习焦虑想聊聊'
union all
select id, 22, '星辰大海', '星', '听起来真好，这种小小的温暖很适合被记住。谢谢你愿意把开心也分享出来。', 2, 'visible', 0, date_sub(now(), interval 9 minute), date_sub(now(), interval 9 minute)
from home_post
where plaza = 'vent' and title = '想分享一件小事';

-- ============================================================
-- Section: auth center demo data
-- ============================================================

insert ignore into auth_center_benefit
(label, icon_key, sort_weight)
values
('发布需求', 'Document', 10),
('发起聊天', 'ChatDotRound', 20),
('参与匹配', 'Select', 30),
('提升可信度', 'Lock', 40),
('更多推荐', 'DataLine', 50);

insert ignore into auth_center_material_sample
(label, tone, sort_weight)
values
('学生证', 'sample-id', 10),
('校园卡', 'sample-card', 20),
('在校证明', 'sample-paper', 30),
('课表截图', 'sample-table', 40);

insert ignore into auth_center_right
(title, description, icon_key, sort_weight)
values
('发布需求', '发布学习、生活、活动等各类需求', 'Document', 10),
('发起聊天', '与更多同学发起聊天，拓展校园连接', 'ChatDotRound', 20),
('参与匹配', '解锁更多匹配机会，找到合适伙伴', 'Select', 30),
('提升可信度', '认证标识展示，增强他人信任感', 'Lock', 40);

