package com.campusmate.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Configuration
public class AdminBootstrapConfig {

    private static final String DEFAULT_SALT = "campusmate-admin-default-salt";

    @Bean
    public ApplicationRunner adminBootstrap(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute("""
                    create table if not exists admin_account (
                        admin_id bigint primary key auto_increment,
                        username varchar(64) not null unique,
                        display_name varchar(64) not null,
                        role varchar(32) not null,
                        password_salt varchar(128) not null,
                        password_hash varchar(128) not null,
                        enabled tinyint(1) not null default 1,
                        last_login_at datetime null,
                        created_at datetime not null default current_timestamp,
                        updated_at datetime not null default current_timestamp on update current_timestamp
                    )
                    """);
            jdbcTemplate.execute("""
                    create table if not exists admin_operation_log (
                        log_id bigint primary key auto_increment,
                        admin_id bigint not null,
                        action varchar(64) not null,
                        target_type varchar(64) not null,
                        target_id bigint null,
                        detail varchar(500) null,
                        created_at datetime not null default current_timestamp
                    )
                    """);
            jdbcTemplate.execute("""
                    create table if not exists admin_announcement (
                        announcement_id bigint primary key auto_increment,
                        title varchar(120) not null,
                        content text not null,
                        status varchar(32) not null,
                        created_by bigint not null,
                        created_at datetime not null default current_timestamp,
                        updated_at datetime not null default current_timestamp on update current_timestamp
                    )
                    """);
            jdbcTemplate.execute("""
                    create table if not exists admin_system_message (
                        message_id bigint primary key auto_increment,
                        target_type varchar(32) not null,
                        target_user_id bigint null,
                        title varchar(120) not null,
                        content text not null,
                        created_by bigint not null,
                        created_at datetime not null default current_timestamp
                    )
                    """);
            Integer count = jdbcTemplate.queryForObject(
                    "select count(1) from admin_account where username = ?",
                    Integer.class,
                    "admin"
            );
            if (count == null || count == 0) {
                jdbcTemplate.update("""
                                insert into admin_account
                                (username, display_name, role, password_salt, password_hash, enabled)
                                values (?, ?, ?, ?, ?, 1)
                                """,
                        "admin", "系统管理员", "super_admin", DEFAULT_SALT, hash(DEFAULT_SALT, "123456"));
            }
        };
    }

    private String hash(String salt, String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest((salt + ":" + password).getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
