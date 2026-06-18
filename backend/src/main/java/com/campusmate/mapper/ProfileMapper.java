package com.campusmate.mapper;

import com.campusmate.domain.entity.UserProfile;
import com.campusmate.domain.vo.ProfileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProfileMapper {

    UserProfile selectByUserId(@Param("userId") Long userId);

    String selectAccountByUserId(@Param("userId") Long userId);

    int insertDefaultProfile(UserProfile profile);

    int updateDisplayProfile(UserProfile profile);

    ProfileVO.CampusVerifyVO selectCampusVerify(@Param("userId") Long userId);

    List<ProfileVO.PreferenceVO> selectPreferences(@Param("userId") Long userId);

    ProfileVO.ActivityVO selectActivitySummary(@Param("userId") Long userId);

    List<ProfileVO.ActivityBarVO> selectActivityBars(@Param("userId") Long userId);

    ProfileVO.SafetyVO selectSafety(@Param("userId") Long userId);

    List<String> selectSafetyItems(@Param("userId") Long userId);

    List<ProfileVO.ChatVO> selectChats(@Param("userId") Long userId);

    List<ProfileVO.PostVO> selectPosts(@Param("userId") Long userId);
}
