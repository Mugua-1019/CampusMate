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
