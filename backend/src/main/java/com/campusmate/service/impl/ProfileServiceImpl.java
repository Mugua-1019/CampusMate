package com.campusmate.service.impl;

import com.campusmate.domain.dto.ProfilePreferenceUpdateRequest;
import com.campusmate.domain.dto.ProfileUpdateRequest;
import com.campusmate.domain.entity.UserProfile;
import com.campusmate.domain.vo.ProfileVO;
import com.campusmate.mapper.ProfileMapper;
import com.campusmate.service.ProfileService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements ProfileService {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final String EMPTY_ACHIEVED_AT = "----------";

    private final ProfileMapper profileMapper;

    public ProfileServiceImpl(ProfileMapper profileMapper) {
        this.profileMapper = profileMapper;
    }

    @Override
    public ProfileVO getProfile(Long userId) {
        UserProfile profile = requireProfile(resolveUserId(userId));
        return toVO(profile);
    }

    @Override
    public ProfileVO updateProfile(ProfileUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("profile request is required");
        }
        UserProfile profile = requireProfile(resolveUserId(request.getUserId()));
        profile.setAvatarUrl(trimToNull(request.getAvatarUrl()));
        profile.setNickname(resolveNickname(request.getNickname(), profile.getNickname()));
        profile.setGender(trimToNull(request.getGender()));
        profile.setGrade(trimToNull(request.getGrade()));
        profile.setCollege(trimToNull(request.getCollege()));
        profile.setMajor(trimToNull(request.getMajor()));
        profile.setInterestTags(joinTags(request.getInterestTags()));
        profile.setBio(trimToNull(request.getBio()));

        int updated = profileMapper.updateDisplayProfile(profile);
        if (updated != 1) {
            throw new IllegalStateException("profile update affected " + updated + " rows");
        }
        return toVO(requireProfile(profile.getUserId()));
    }

    @Override
    public ProfileVO updatePreferences(ProfilePreferenceUpdateRequest request) {
        if (request == null || request.getPreferences() == null) {
            throw new IllegalArgumentException("preference request is required");
        }
        Long userId = resolveUserId(request.getUserId());
        requireProfile(userId);
        int attempted = 0;
        int updated = 0;
        for (ProfilePreferenceUpdateRequest.Item item : request.getPreferences()) {
            String label = trimToNull(item == null ? null : item.getLabel());
            if (label == null || item.getScore() == null) {
                continue;
            }
            attempted++;
            updated += profileMapper.updatePreferenceScore(userId, label, clampScore(item.getScore()));
        }
        if (attempted == 0) {
            throw new IllegalArgumentException("at least one preference score is required");
        }
        if (updated != attempted) {
            throw new IllegalStateException("preference update affected " + updated + " of " + attempted + " rows");
        }
        return toVO(requireProfile(userId));
    }

    private Long resolveUserId(Long userId) {
        return userId == null ? DEFAULT_USER_ID : userId;
    }

    private UserProfile requireProfile(Long userId) {
        UserProfile profile = profileMapper.selectByUserId(userId);
        if (profile != null) {
            return profile;
        }
        String account = profileMapper.selectAccountByUserId(userId);
        if (account == null) {
            throw new IllegalStateException("profile not found for userId " + userId);
        }
        UserProfile defaultProfile = new UserProfile();
        defaultProfile.setUserId(userId);
        defaultProfile.setNickname(defaultNickname(account));
        defaultProfile.setVerified(false);
        defaultProfile.setVerifyStatus("unverified");
        int inserted = profileMapper.insertDefaultProfile(defaultProfile);
        if (inserted != 1) {
            throw new IllegalStateException("profile create affected " + inserted + " rows");
        }
        return profileMapper.selectByUserId(userId);
    }

    private String defaultNickname(String account) {
        String trimmed = trimToNull(account);
        if (trimmed == null) {
            return "未命名用户";
        }
        int atIndex = trimmed.indexOf('@');
        String nickname = atIndex > 0 ? trimmed.substring(0, atIndex) : trimmed;
        return nickname.length() > 60 ? nickname.substring(0, 60) : nickname;
    }

    private ProfileVO toVO(UserProfile profile) {
        ProfileVO vo = new ProfileVO();
        vo.setUserId(profile.getUserId());
        vo.setAvatarUrl(profile.getAvatarUrl());
        vo.setNickname(profile.getNickname());
        vo.setGender(profile.getGender());
        vo.setGrade(profile.getGrade());
        vo.setCollege(profile.getCollege());
        vo.setMajor(profile.getMajor());
        vo.setInterestTags(splitTags(profile.getInterestTags()));
        vo.setBio(profile.getBio());
        vo.setRealName(profile.getRealName());
        vo.setVerified(Boolean.TRUE.equals(profile.getVerified()));
        vo.setVerifyStatus(profile.getVerifyStatus());
        vo.setCompletionPercent(calculateCompletion(profile));
        vo.setCompletionHint("完善资料，获得更多匹配机会。");
        vo.setCampusVerify(profileMapper.selectCampusVerify(profile.getUserId()));
        vo.setPreferences(profileMapper.selectPreferences(profile.getUserId()));
        ProfileVO.ActivityVO activity = profileMapper.selectActivitySummary(profile.getUserId());
        if (activity != null) {
            activity.setWeekBars(profileMapper.selectActivityBars(profile.getUserId()));
        }
        vo.setActivity(activity);
        ProfileVO.SafetyVO safety = profileMapper.selectSafety(profile.getUserId());
        if (safety != null) {
            safety.setItems(profileMapper.selectSafetyItems(profile.getUserId()));
        }
        vo.setSafety(safety);
        vo.setChats(profileMapper.selectChats(profile.getUserId()));
        vo.setPosts(profileMapper.selectPosts(profile.getUserId()));
        vo.setAchievements(buildAchievements(profile, vo.getCompletionPercent()));
        return vo;
    }

    private List<ProfileVO.AchievementVO> buildAchievements(UserProfile profile, int completionPercent) {
        Long userId = profile.getUserId();
        String campusVerifiedDate = profileMapper.selectLatestCampusVerifiedDate(userId);
        int approvedMatches = profileMapper.countApprovedMatches(userId);
        String approvedMatchDate = profileMapper.selectLatestApprovedMatchDate(userId);
        int studyMatches = profileMapper.countApprovedMatchesByCategory(userId, "学习");
        String studyMatchDate = profileMapper.selectLatestApprovedMatchDateByCategory(userId, "学习");
        int sportMatches = profileMapper.countApprovedMatchesByCategory(userId, "运动");
        String sportMatchDate = profileMapper.selectLatestApprovedMatchDateByCategory(userId, "运动");
        int ventReplies = profileMapper.countVisibleVentReplies(userId);
        String ventReplyDate = profileMapper.selectLatestVisibleVentReplyDate(userId);
        boolean verified = Boolean.TRUE.equals(profile.getVerified());
        return List.of(
                achievement("anquanweishi", "安全卫士", "完成校园认证", verified, campusVerifiedDate, "green"),
                achievement("pipeidaren", "匹配达人", "累计完成五次匹配", approvedMatches >= 5, approvedMatchDate, "purple"),
                achievement("xiaoyuanhuoditu", "校园活地图", "完成校园认证且个人资料完善度达到100%", verified && completionPercent >= 100, campusVerifiedDate, "blue"),
                achievement("xuexidaren", "学习达人", "该用户完成三次学习搭子匹配", studyMatches >= 3, studyMatchDate, "indigo"),
                achievement("yundongdaren", "运动达人", "该用户完成三次运动搭子匹配", sportMatches >= 3, sportMatchDate, "green"),
                achievement("zuijiatingzhong", "最佳听众", "该用户累计评论或安慰倾诉广场帖子达十次", ventReplies >= 10, ventReplyDate, "orange")
        );
    }

    private ProfileVO.AchievementVO achievement(String key,
                                                String title,
                                                String condition,
                                                boolean achieved,
                                                String achievedAt,
                                                String tone) {
        ProfileVO.AchievementVO vo = new ProfileVO.AchievementVO();
        vo.setKey(key);
        vo.setTitle(title);
        vo.setCondition(condition);
        vo.setAchieved(achieved);
        vo.setAchievedAt(achieved ? defaultText(achievedAt, "已获得") : EMPTY_ACHIEVED_AT);
        vo.setTone(tone);
        return vo;
    }

    private int calculateCompletion(UserProfile profile) {
        int total = 8;
        int filled = 0;
        if (trimToNull(profile.getAvatarUrl()) != null) {
            filled++;
        }
        if (trimToNull(profile.getNickname()) != null) {
            filled++;
        }
        if (trimToNull(profile.getGender()) != null) {
            filled++;
        }
        if (trimToNull(profile.getGrade()) != null) {
            filled++;
        }
        if (trimToNull(profile.getCollege()) != null) {
            filled++;
        }
        if (trimToNull(profile.getMajor()) != null) {
            filled++;
        }
        if (trimToNull(profile.getInterestTags()) != null) {
            filled++;
        }
        if (trimToNull(profile.getBio()) != null) {
            filled++;
        }
        return Math.round(filled * 100.0f / total);
    }

    private List<String> splitTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());
    }

    private String joinTags(List<String> tags) {
        if (tags == null) {
            return null;
        }
        return tags.stream()
                .map(this::trimToNull)
                .filter(tag -> tag != null)
                .collect(Collectors.joining(","));
    }

    private String requiredText(String value, String field) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException(field + " is required");
        }
        return trimmed;
    }

    private String defaultText(String value, String fallback) {
        String trimmed = trimToNull(value);
        return trimmed == null ? fallback : trimmed;
    }

    private String resolveNickname(String requestedNickname, String currentNickname) {
        String trimmed = trimToNull(requestedNickname);
        if (trimmed != null) {
            return trimmed;
        }
        return requiredText(currentNickname, "nickname");
    }

    private int clampScore(int score) {
        return Math.max(1, Math.min(6, score));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
