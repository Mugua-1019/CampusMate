package com.campusmate.service.impl;

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

    private String resolveNickname(String requestedNickname, String currentNickname) {
        String trimmed = trimToNull(requestedNickname);
        if (trimmed != null) {
            return trimmed;
        }
        return requiredText(currentNickname, "nickname");
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
