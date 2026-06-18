package com.campusmate.service;

import com.campusmate.domain.dto.ProfileUpdateRequest;
import com.campusmate.domain.entity.UserProfile;
import com.campusmate.domain.vo.ProfileVO;
import com.campusmate.mapper.ProfileMapper;
import com.campusmate.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfileServiceImplTest {

    @Test
    void getProfileReturnsDisplayAndReadonlyVerifyInfo() {
        FakeProfileMapper mapper = new FakeProfileMapper(profile());
        ProfileService service = new ProfileServiceImpl(mapper);

        ProfileVO result = service.getProfile(1L);

        assertEquals("Ada", result.getNickname());
        assertEquals(Arrays.asList("reading", "running"), result.getInterestTags());
        assertEquals("Alice Chen", result.getRealName());
        assertTrue(result.isVerified());
        assertEquals("approved", result.getVerifyStatus());
        assertEquals(100, result.getCompletionPercent());
        assertEquals("学习搭子", result.getPreferences().get(0).getLabel());
        assertEquals(42, result.getActivity().getScore());
        assertEquals(List.of("无违规记录"), result.getSafety().getItems());
        assertEquals("Ada Chat", result.getChats().get(0).getName());
        assertEquals("Study post", result.getPosts().get(0).getTitle());
    }

    @Test
    void updateProfileChangesOnlyDisplayFields() {
        UserProfile original = profile();
        FakeProfileMapper mapper = new FakeProfileMapper(original);
        ProfileService service = new ProfileServiceImpl(mapper);
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUserId(1L);
        request.setAvatarUrl("  /avatars/new.png ");
        request.setNickname("  Star ");
        request.setGender(" female ");
        request.setGrade(" 2024 ");
        request.setCollege(" Computer School ");
        request.setMajor(" Software Engineering ");
        request.setInterestTags(Arrays.asList(" badminton ", "", "music"));
        request.setBio("  Keep moving. ");

        ProfileVO result = service.updateProfile(request);

        assertEquals("Star", result.getNickname());
        assertEquals(List.of("badminton", "music"), result.getInterestTags());
        assertEquals("Alice Chen", result.getRealName());
        assertTrue(result.isVerified());
        assertEquals("badminton,music", mapper.updatedProfile.getInterestTags());
    }

    @Test
    void updateProfileKeepsCurrentNicknameWhenRequestNicknameIsBlank() {
        FakeProfileMapper mapper = new FakeProfileMapper(profile());
        ProfileService service = new ProfileServiceImpl(mapper);
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUserId(1L);
        request.setAvatarUrl("/uploads/avatars/new.jpg");
        request.setNickname(" ");

        ProfileVO result = service.updateProfile(request);

        assertEquals("Ada", result.getNickname());
        assertEquals("/uploads/avatars/new.jpg", mapper.updatedProfile.getAvatarUrl());
    }

    @Test
    void getProfileCreatesDefaultProfileWhenAccountExistsWithoutProfile() {
        FakeProfileMapper mapper = new FakeProfileMapper(null);
        mapper.account = "student@example.com";
        ProfileService service = new ProfileServiceImpl(mapper);

        ProfileVO result = service.getProfile(2L);

        assertEquals(2L, result.getUserId());
        assertEquals("student", result.getNickname());
        assertEquals("student", mapper.insertedProfile.getNickname());
    }

    private UserProfile profile() {
        UserProfile profile = new UserProfile();
        profile.setUserId(1L);
        profile.setAvatarUrl("/avatars/default.png");
        profile.setNickname("Ada");
        profile.setGender("female");
        profile.setGrade("2023");
        profile.setCollege("Computer School");
        profile.setMajor("Software Engineering");
        profile.setInterestTags("reading, running");
        profile.setBio("Hello CampusMate.");
        profile.setRealName("Alice Chen");
        profile.setVerified(true);
        profile.setVerifyStatus("approved");
        return profile;
    }

    private static class FakeProfileMapper implements ProfileMapper {
        private UserProfile profile;
        private UserProfile updatedProfile;
        private UserProfile insertedProfile;
        private String account;

        private FakeProfileMapper(UserProfile profile) {
            this.profile = profile;
        }

        @Override
        public UserProfile selectByUserId(Long userId) {
            return profile;
        }

        @Override
        public String selectAccountByUserId(Long userId) {
            return account;
        }

        @Override
        public int insertDefaultProfile(UserProfile profile) {
            this.insertedProfile = profile;
            this.profile = profile;
            return 1;
        }

        @Override
        public int updateDisplayProfile(UserProfile profile) {
            this.updatedProfile = profile;
            this.profile = profile;
            return 1;
        }

        @Override
        public ProfileVO.CampusVerifyVO selectCampusVerify(Long userId) {
            ProfileVO.CampusVerifyVO verify = new ProfileVO.CampusVerifyVO();
            verify.setTitle("校园认证");
            verify.setDescription("完成校园认证。");
            verify.setActionText("去认证");
            return verify;
        }

        @Override
        public List<ProfileVO.PreferenceVO> selectPreferences(Long userId) {
            ProfileVO.PreferenceVO preference = new ProfileVO.PreferenceVO();
            preference.setLabel("学习搭子");
            preference.setIcon("reading");
            preference.setScore(6);
            preference.setTone("purple");
            return List.of(preference);
        }

        @Override
        public ProfileVO.ActivityVO selectActivitySummary(Long userId) {
            ProfileVO.ActivityVO activity = new ProfileVO.ActivityVO();
            activity.setScore(42);
            activity.setPercentile(68);
            return activity;
        }

        @Override
        public List<ProfileVO.ActivityBarVO> selectActivityBars(Long userId) {
            ProfileVO.ActivityBarVO bar = new ProfileVO.ActivityBarVO();
            bar.setDay("一");
            bar.setValue(32);
            return List.of(bar);
        }

        @Override
        public ProfileVO.SafetyVO selectSafety(Long userId) {
            ProfileVO.SafetyVO safety = new ProfileVO.SafetyVO();
            safety.setStatus("良好");
            safety.setCreditScore(92);
            safety.setSafetyLevel("高");
            return safety;
        }

        @Override
        public List<String> selectSafetyItems(Long userId) {
            return List.of("无违规记录");
        }

        @Override
        public List<ProfileVO.ChatVO> selectChats(Long userId) {
            ProfileVO.ChatVO chat = new ProfileVO.ChatVO();
            chat.setAvatar("A");
            chat.setName("Ada Chat");
            chat.setTag("学习搭子");
            chat.setMessage("hello");
            chat.setTime("10:24");
            chat.setUnread(1);
            chat.setTone("warm");
            return List.of(chat);
        }

        @Override
        public List<ProfileVO.PostVO> selectPosts(Long userId) {
            ProfileVO.PostVO post = new ProfileVO.PostVO();
            post.setShortLabel("学");
            post.setTitle("Study post");
            post.setTag("学习搭子");
            post.setPeriod("长期");
            post.setDescription("study together");
            post.setLocation("library");
            post.setTime("today");
            post.setMatched(3);
            post.setTone("study");
            return List.of(post);
        }
    }
}
