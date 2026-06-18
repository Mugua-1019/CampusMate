package com.campusmate.service.impl;

import com.campusmate.domain.dto.AuthLoginRequest;
import com.campusmate.domain.dto.AuthRegisterRequest;
import com.campusmate.domain.dto.AuthResetPasswordRequest;
import com.campusmate.domain.entity.UserAccount;
import com.campusmate.domain.entity.UserProfile;
import com.campusmate.domain.vo.AuthUserVO;
import com.campusmate.mapper.AuthMapper;
import com.campusmate.mapper.ProfileMapper;
import com.campusmate.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

@Service
public class AuthServiceImpl implements AuthService {

    private static final int MIN_PASSWORD_LENGTH = 6;

    private final AuthMapper authMapper;
    private final ProfileMapper profileMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthServiceImpl(AuthMapper authMapper, ProfileMapper profileMapper) {
        this.authMapper = authMapper;
        this.profileMapper = profileMapper;
    }

    @Override
    public AuthUserVO login(AuthLoginRequest request) {
        String loginAccount = requiredText(request == null ? null : request.getAccount(), "account");
        String password = requiredText(request == null ? null : request.getPassword(), "password");
        UserAccount account = authMapper.selectByLoginAccount(loginAccount);
        if (account == null || !Boolean.TRUE.equals(account.getEnabled())) {
            throw new IllegalArgumentException("account or password is incorrect");
        }
        if (!hashPassword(account.getPasswordSalt(), password).equals(account.getPasswordHash())) {
            throw new IllegalArgumentException("account or password is incorrect");
        }
        return toVO(account);
    }

    @Override
    @Transactional
    public AuthUserVO register(AuthRegisterRequest request) {
        String loginAccount = requiredText(request == null ? null : request.getAccount(), "account");
        String password = validPassword(request == null ? null : request.getPassword());
        if (authMapper.countByLoginAccount(loginAccount) > 0) {
            throw new IllegalArgumentException("account already exists");
        }

        UserAccount account = new UserAccount();
        account.setAccount(loginAccount);
        if (loginAccount.contains("@")) {
            account.setEmail(loginAccount);
        } else if (loginAccount.matches("\\d{11}")) {
            account.setPhone(loginAccount);
        }
        account.setPasswordSalt(newSalt());
        account.setPasswordHash(hashPassword(account.getPasswordSalt(), password));
        account.setEnabled(true);

        int inserted = authMapper.insertAccount(account);
        if (inserted != 1 || account.getUserId() == null) {
            throw new IllegalStateException("account insert affected " + inserted + " rows");
        }

        String nickname = trimToNull(request.getNickname());
        if (nickname == null) {
            nickname = defaultNickname(loginAccount);
        }
        UserProfile profile = new UserProfile();
        profile.setUserId(account.getUserId());
        profile.setAvatarUrl("/avatars/default.png");
        profile.setNickname(nickname);
        profile.setVerified(false);
        profile.setVerifyStatus("unverified");
        requireOne(authMapper.insertProfile(profile), "profile insert");
        requireOne(authMapper.insertHomeUserSummary(account.getUserId(), nickname), "home summary insert");
        return toVO(account);
    }

    @Override
    public void resetPassword(AuthResetPasswordRequest request) {
        String loginAccount = requiredText(request == null ? null : request.getAccount(), "account");
        String password = validPassword(request == null ? null : request.getPassword());
        UserAccount account = authMapper.selectByLoginAccount(loginAccount);
        if (account == null || !Boolean.TRUE.equals(account.getEnabled())) {
            throw new IllegalArgumentException("account not found");
        }
        String salt = newSalt();
        requireOne(authMapper.updatePassword(account.getUserId(), salt, hashPassword(salt, password)), "password reset");
    }

    private AuthUserVO toVO(UserAccount account) {
        UserProfile profile = profileMapper.selectByUserId(account.getUserId());
        AuthUserVO vo = new AuthUserVO();
        vo.setUserId(account.getUserId());
        vo.setAccount(account.getAccount());
        if (profile != null) {
            vo.setNickname(profile.getNickname());
            vo.setAvatarUrl(profile.getAvatarUrl());
        }
        return vo;
    }

    private String requiredText(String value, String field) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException(field + " is required");
        }
        return trimmed;
    }

    private String validPassword(String value) {
        String password = requiredText(value, "password");
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("password must be at least 6 characters");
        }
        return password;
    }

    private String defaultNickname(String account) {
        if (account.contains("@")) {
            return account.substring(0, account.indexOf('@'));
        }
        return account.length() > 6 ? "用户" + account.substring(account.length() - 4) : account;
    }

    private void requireOne(int affectedRows, String action) {
        if (affectedRows != 1) {
            throw new IllegalStateException(action + " affected " + affectedRows + " rows");
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String newSalt() {
        byte[] bytes = new byte[16];
        secureRandom.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private String hashPassword(String salt, String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest((salt + ":" + password).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
