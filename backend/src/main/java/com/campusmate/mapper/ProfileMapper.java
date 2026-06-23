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

    int updatePreferenceScore(@Param("userId") Long userId,
                              @Param("label") String label,
                              @Param("score") int score);

    ProfileVO.CampusVerifyVO selectCampusVerify(@Param("userId") Long userId);

    List<ProfileVO.PreferenceVO> selectPreferences(@Param("userId") Long userId);

    ProfileVO.ActivityVO selectActivitySummary(@Param("userId") Long userId);

    List<ProfileVO.ActivityBarVO> selectActivityBars(@Param("userId") Long userId);

    ProfileVO.SafetyVO selectSafety(@Param("userId") Long userId);

    List<String> selectSafetyItems(@Param("userId") Long userId);

    List<ProfileVO.ChatVO> selectChats(@Param("userId") Long userId);

    List<ProfileVO.PostVO> selectPosts(@Param("userId") Long userId);

    String selectLatestCampusVerifiedDate(@Param("userId") Long userId);

    int countApprovedMatches(@Param("userId") Long userId);

    String selectLatestApprovedMatchDate(@Param("userId") Long userId);

    int countApprovedMatchesByCategory(@Param("userId") Long userId, @Param("categoryKeyword") String categoryKeyword);

    String selectLatestApprovedMatchDateByCategory(@Param("userId") Long userId, @Param("categoryKeyword") String categoryKeyword);

    int countVisibleVentReplies(@Param("userId") Long userId);

    String selectLatestVisibleVentReplyDate(@Param("userId") Long userId);
}
