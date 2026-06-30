package com.campusmate.service;

import java.util.List;
import java.util.Map;

public interface AdminService {

    Map<String, Object> login(Map<String, Object> request);

    Map<String, Object> dashboard(Long adminId);

    Map<String, Object> users(Long adminId, String keyword, String status, Integer page, Integer size);

    Map<String, Object> userDetail(Long adminId, Long userId);

    void setUserEnabled(Long adminId, Long userId, boolean enabled);

    Map<String, Object> posts(Long adminId, String keyword, String plaza, String reviewStatus, Integer page, Integer size);

    Map<String, Object> postDetail(Long adminId, Long postId);

    void setPostReviewStatus(Long adminId, Long postId, String reviewStatus, String reason);

    void setPostDeleted(Long adminId, Long postId, boolean deleted);

    Map<String, Object> reports(Long adminId, String status, Integer page, Integer size);

    void handleReport(Long adminId, Long reportId, String status, String handlingNote);

    Map<String, Object> authRecords(Long adminId, String status, Integer page, Integer size);

    void approveAuthRecord(Long adminId, Long recordId);

    void rejectAuthRecord(Long adminId, Long recordId, String reason);

    List<Map<String, Object>> announcements(Long adminId);

    Map<String, Object> createAnnouncement(Long adminId, Map<String, Object> request);

    Map<String, Object> updateAnnouncement(Long adminId, Long announcementId, Map<String, Object> request);

    void deleteAnnouncement(Long adminId, Long announcementId);

    List<Map<String, Object>> categories(Long adminId);

    Map<String, Object> createCategory(Long adminId, Map<String, Object> request);

    Map<String, Object> updateCategory(Long adminId, Long categoryId, Map<String, Object> request);

    void setCategoryEnabled(Long adminId, Long categoryId, boolean enabled);

    Map<String, Object> createSystemMessage(Long adminId, Map<String, Object> request);

    Map<String, Object> logs(Long adminId, Integer page, Integer size);
}
