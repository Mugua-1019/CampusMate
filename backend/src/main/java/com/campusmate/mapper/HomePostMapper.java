package com.campusmate.mapper;

import com.campusmate.domain.dto.HomePlazaQuery;
import com.campusmate.domain.entity.HomeNotification;
import com.campusmate.domain.entity.HomeMatchRequest;
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

    int insertPost(HomePost post);

    int insertReply(HomePostReply reply);

    int insertReport(HomePostReport report);

    HomeMatchRequest selectMatchRequestByPostAndRequester(@Param("postId") Long postId, @Param("requesterUserId") Long requesterUserId);

    HomeMatchRequest selectMatchRequestDetailById(@Param("requestId") Long requestId);

    List<HomeMatchRequest> selectStartedMatchRequests(@Param("userId") Long userId);

    List<HomeMatchRequest> selectJoinedMatchRequests(@Param("userId") Long userId);

    List<HomeMatchRequest> selectRecentApprovedMatchRequests(@Param("userId") Long userId);

    int countStartedMatchPosts(@Param("userId") Long userId);

    int countMatchRequestsByStatus(@Param("userId") Long userId, @Param("status") String status);

    int insertMatchRequest(HomeMatchRequest request);

    int approveMatchRequest(@Param("requestId") Long requestId, @Param("publisherUserId") Long publisherUserId);

    int rejectMatchRequest(@Param("requestId") Long requestId, @Param("publisherUserId") Long publisherUserId);

    int incrementMatchPostCount(@Param("postId") Long postId);
}
