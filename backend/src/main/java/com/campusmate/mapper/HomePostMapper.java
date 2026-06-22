package com.campusmate.mapper;

import com.campusmate.domain.dto.HomePlazaQuery;
import com.campusmate.domain.entity.HomeNotification;
import com.campusmate.domain.entity.HomePost;
import com.campusmate.domain.entity.HomePostReport;
import com.campusmate.domain.entity.HomePostReply;
import com.campusmate.domain.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HomePostMapper {

    List<HomePost> selectVisiblePosts(@Param("query") HomePlazaQuery query);

    HomePost selectVisibleMatchPostById(@Param("id") Long id);

    HomePost selectVisibleVentPostById(@Param("id") Long id);

    List<HomePost> selectSimilarVisibleVentPosts(@Param("id") Long id, @Param("category") String category);

    List<HomePostReply> selectVisibleRepliesByPostId(@Param("postId") Long postId);

    List<HomeNotification> selectNotificationsByUserId(@Param("userId") Long userId);

    UserProfile selectVerifyStatus(@Param("userId") Long userId);

    int incrementVentPostComfort(@Param("postId") Long postId);

    int upsertLikeNotification(@Param("userId") Long userId, @Param("postId") Long postId);

    int markNotificationsRead(@Param("userId") Long userId);

    int insertReply(HomePostReply reply);

    int insertReport(HomePostReport report);
}
