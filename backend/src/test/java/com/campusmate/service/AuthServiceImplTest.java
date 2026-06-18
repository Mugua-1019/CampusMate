package com.campusmate.service;

import com.campusmate.domain.dto.AuthLoginRequest;
import com.campusmate.domain.dto.AuthRegisterRequest;
import com.campusmate.domain.dto.AuthResetPasswordRequest;
import com.campusmate.domain.entity.UserAccount;
import com.campusmate.domain.entity.UserProfile;
import com.campusmate.domain.vo.ProfileVO;
import com.campusmate.mapper.AuthMapper;
import com.campusmate.mapper.ProfileMapper;
import com.campusmate.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthServiceImplTest {

    @Test
    void loginReturnsUserWhenPasswordMatchesStoredHash() {
        FakeAuthMapper authMapper = new FakeAuthMapper(account("demo", "salt", hash("salt", "123456")));
        AuthService service = new AuthServiceImpl(authMapper, new FakeProfileMapper(profile()));
        AuthLoginRequest request = new AuthLoginRequest();
        request.setAccount("demo");
        request.setPassword("123456");

        assertEquals("Demo User", service.login(request).getNickname());
    }

    @Test
    void loginRejectsWrongPassword() {
        AuthService service = new AuthServiceImpl(
                new FakeAuthMapper(account("demo", "salt", hash("salt", "123456"))),
                new FakeProfileMapper(profile())
        );
        AuthLoginRequest request = new AuthLoginRequest();
        request.setAccount("demo");
        request.setPassword("wrong-password");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.login(request));

        assertEquals("account or password is incorrect", exception.getMessage());
    }

    @Test
    void registerCreatesAccountProfileAndHomeSummary() {
        FakeAuthMapper authMapper = new FakeAuthMapper(null);
        AuthService service = new AuthServiceImpl(authMapper, new FakeProfileMapper(null));
        AuthRegisterRequest request = new AuthRegisterRequest();
        request.setAccount("student@example.com");
        request.setPassword("123456");
        request.setNickname("Campus Student");

        assertEquals("student@example.com", service.register(request).getAccount());
        assertNotNull(authMapper.insertedAccount.getUserId());
        assertEquals("Campus Student", authMapper.insertedProfile.getNickname());
        assertEquals("Campus Student", authMapper.homeSummaryNickname);
        assertNotEquals("123456", authMapper.insertedAccount.getPasswordHash());
    }

    @Test
    void registerUsesDefaultNicknameWhenNicknameIsBlank() {
        FakeAuthMapper authMapper = new FakeAuthMapper(null);
        AuthService service = new AuthServiceImpl(authMapper, new FakeProfileMapper(null));
        AuthRegisterRequest request = new AuthRegisterRequest();
        request.setAccount("student@example.com");
        request.setPassword("123456");
        request.setNickname(" ");

        service.register(request);

        assertEquals("student", authMapper.insertedProfile.getNickname());
    }

    @Test
    void resetPasswordUpdatesSaltAndHash() {
        UserAccount account = account("demo", "old-salt", hash("old-salt", "123456"));
        FakeAuthMapper authMapper = new FakeAuthMapper(account);
        AuthService service = new AuthServiceImpl(authMapper, new FakeProfileMapper(profile()));
        AuthResetPasswordRequest request = new AuthResetPasswordRequest();
        request.setAccount("demo");
        request.setPassword("654321");

        service.resetPassword(request);

        assertNotEquals("old-salt", authMapper.updatedSalt);
        assertEquals(authMapper.updatedHash, hash(authMapper.updatedSalt, "654321"));
    }

    private UserAccount account(String loginAccount, String salt, String hash) {
        UserAccount account = new UserAccount();
        account.setUserId(1L);
        account.setAccount(loginAccount);
        account.setPasswordSalt(salt);
        account.setPasswordHash(hash);
        account.setEnabled(true);
        return account;
    }

    private UserProfile profile() {
        UserProfile profile = new UserProfile();
        profile.setUserId(1L);
        profile.setNickname("Demo User");
        profile.setAvatarUrl("/avatars/default.png");
        return profile;
    }

    private static String hash(String salt, String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest((salt + ":" + password).getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static class FakeAuthMapper implements AuthMapper {
        private UserAccount account;
        private UserAccount insertedAccount;
        private UserProfile insertedProfile;
        private String homeSummaryNickname;
        private String updatedSalt;
        private String updatedHash;

        private FakeAuthMapper(UserAccount account) {
            this.account = account;
        }

        @Override
        public UserAccount selectByLoginAccount(String account) {
            return this.account;
        }

        @Override
        public int countByLoginAccount(String account) {
            return this.account == null ? 0 : 1;
        }

        @Override
        public int insertAccount(UserAccount account) {
            account.setUserId(2L);
            this.insertedAccount = account;
            this.account = account;
            return 1;
        }

        @Override
        public int insertProfile(UserProfile profile) {
            this.insertedProfile = profile;
            return 1;
        }

        @Override
        public int insertHomeUserSummary(Long userId, String nickname) {
            this.homeSummaryNickname = nickname;
            return 1;
        }

        @Override
        public int updatePassword(Long userId, String passwordSalt, String passwordHash) {
            this.updatedSalt = passwordSalt;
            this.updatedHash = passwordHash;
            return 1;
        }
    }

    private static class FakeProfileMapper implements ProfileMapper {
        private final UserProfile profile;

        private FakeProfileMapper(UserProfile profile) {
            this.profile = profile;
        }

        @Override
        public UserProfile selectByUserId(Long userId) {
            return profile;
        }

        @Override
        public String selectAccountByUserId(Long userId) {
            return null;
        }

        @Override
        public int insertDefaultProfile(UserProfile profile) {
            return 1;
        }

        @Override
        public int updateDisplayProfile(UserProfile profile) {
            return 1;
        }

        @Override
        public ProfileVO.CampusVerifyVO selectCampusVerify(Long userId) {
            return null;
        }

        @Override
        public List<ProfileVO.PreferenceVO> selectPreferences(Long userId) {
            return List.of();
        }

        @Override
        public ProfileVO.ActivityVO selectActivitySummary(Long userId) {
            return null;
        }

        @Override
        public List<ProfileVO.ActivityBarVO> selectActivityBars(Long userId) {
            return List.of();
        }

        @Override
        public ProfileVO.SafetyVO selectSafety(Long userId) {
            return null;
        }

        @Override
        public List<String> selectSafetyItems(Long userId) {
            return List.of();
        }

        @Override
        public List<ProfileVO.ChatVO> selectChats(Long userId) {
            return List.of();
        }

        @Override
        public List<ProfileVO.PostVO> selectPosts(Long userId) {
            return List.of();
        }
    }
}
