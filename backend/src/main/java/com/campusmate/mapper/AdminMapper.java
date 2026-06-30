package com.campusmate.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {

    Map<String, Object> selectAdminByUsername(@Param("username") String username);

    Map<String, Object> selectAdminById(@Param("adminId") Long adminId);

    int updateAdminLastLogin(@Param("adminId") Long adminId);

    int countUsers(@Param("keyword") String keyword, @Param("status") String status);

    List<Map<String, Object>> selectUsers(@Param("keyword") String keyword, @Param("status") String status,
                                          @Param("offset") int offset, @Param("size") int size);

    Map<String, Object> selectUserDetail(@Param("userId") Long userId);

    int updateUserEnabled(@Param("userId") Long userId, @Param("enabled") boolean enabled);

    int countPosts(@Param("keyword") String keyword, @Param("plaza") String plaza, @Param("reviewStatus") String reviewStatus);

    List<Map<String, Object>> selectPosts(@Param("keyword") String keyword, @Param("plaza") String plaza,
                                          @Param("reviewStatus") String reviewStatus, @Param("offset") int offset,
                                          @Param("size") int size);

    Map<String, Object> selectPostDetail(@Param("postId") Long postId);

    int updatePostReviewStatus(@Param("postId") Long postId, @Param("reviewStatus") String reviewStatus);

    int updatePostDeleted(@Param("postId") Long postId, @Param("deleted") boolean deleted);

    int countReports(@Param("status") String status);

    List<Map<String, Object>> selectReports(@Param("status") String status, @Param("offset") int offset, @Param("size") int size);

    int updateReportStatus(@Param("reportId") Long reportId, @Param("status") String status);

    int countAuthRecords(@Param("status") String status);

    List<Map<String, Object>> selectAuthRecords(@Param("status") String status, @Param("offset") int offset, @Param("size") int size);

    Map<String, Object> selectAuthRecordById(@Param("recordId") Long recordId);

    int approveAuthRecord(@Param("recordId") Long recordId, @Param("feedback") String feedback);

    int rejectAuthRecord(@Param("recordId") Long recordId, @Param("feedback") String feedback);

    int updateProfileVerified(@Param("userId") Long userId, @Param("realName") String realName,
                              @Param("verified") boolean verified, @Param("verifyStatus") String verifyStatus);

    int updateHomeUserSummaryVerified(@Param("userId") Long userId, @Param("verified") boolean verified);

    List<Map<String, Object>> selectAnnouncements();

    Map<String, Object> selectAnnouncementById(@Param("announcementId") Long announcementId);

    int insertAnnouncement(@Param("title") String title, @Param("content") String content,
                           @Param("status") String status, @Param("adminId") Long adminId);

    int updateAnnouncement(@Param("announcementId") Long announcementId, @Param("title") String title,
                           @Param("content") String content, @Param("status") String status);

    int deleteAnnouncement(@Param("announcementId") Long announcementId);

    List<Map<String, Object>> selectCategories();

    Map<String, Object> selectCategoryById(@Param("categoryId") Long categoryId);

    int insertCategory(@Param("plazaKey") String plazaKey, @Param("categoryName") String categoryName,
                       @Param("sortWeight") int sortWeight);

    int updateCategory(@Param("categoryId") Long categoryId, @Param("plazaKey") String plazaKey,
                       @Param("categoryName") String categoryName, @Param("sortWeight") int sortWeight);

    int updateCategoryEnabled(@Param("categoryId") Long categoryId, @Param("enabled") boolean enabled);

    int insertSystemMessage(@Param("targetType") String targetType, @Param("targetUserId") Long targetUserId,
                            @Param("title") String title, @Param("content") String content,
                            @Param("adminId") Long adminId);

    int insertOperationLog(@Param("adminId") Long adminId, @Param("action") String action,
                           @Param("targetType") String targetType, @Param("targetId") Long targetId,
                           @Param("detail") String detail);

    int countOperationLogs();

    List<Map<String, Object>> selectOperationLogs(@Param("offset") int offset, @Param("size") int size);

    int countTodayUsers();

    int countPendingPosts();

    int countPendingReports();

    int countPendingAuthRecords();

    int countTotalUsers();

    int countTotalPosts();
}
