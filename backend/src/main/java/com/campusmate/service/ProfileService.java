package com.campusmate.service;

import com.campusmate.domain.dto.ProfileUpdateRequest;
import com.campusmate.domain.dto.ProfilePreferenceUpdateRequest;
import com.campusmate.domain.vo.ProfileVO;

public interface ProfileService {

    ProfileVO getProfile(Long userId);

    ProfileVO updateProfile(ProfileUpdateRequest request);

    ProfileVO updatePreferences(ProfilePreferenceUpdateRequest request);
}
