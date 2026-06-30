package com.campusmate.service.impl;

import com.campusmate.mapper.AdminMapper;
import com.campusmate.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final AdminMapper adminMapper;

    public AdminServiceImpl(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    @Override
    public Map<String, Object> login(Map<String, Object> request) {
        String username = requiredText(value(request, "username"), "username");
        String password = requiredText(value(request, "password"), "password");
        Map<String, Object> admin = adminMapper.selectAdminByUsername(username);
        if (admin == null || !truthy(admin.get("enabled"))) {
            throw new IllegalArgumentException("username or password is incorrect");
        }
        String salt = String.valueOf(admin.get("passwordSalt"));
        String passwordHash = String.valueOf(admin.get("passwordHash"));
        if (!hashPassword(salt, password).equals(passwordHash)) {
            throw new IllegalArgumentException("username or password is incorrect");
        }
        Long adminId = number(admin.get("adminId"), "adminId");
        requireOne(adminMapper.updateAdminLastLogin(adminId), "admin login update");
        log(adminId, "login", "admin_account", adminId, "管理员登录");

        Map<String, Object> result = new HashMap<>();
        result.put("adminId", adminId);
        result.put("username", admin.get("username"));
        result.put("displayName", admin.get("displayName"));
        result.put("role", admin.get("role"));
        result.put("token", "admin-" + adminId);
        return result;
    }

    @Override
    public Map<String, Object> dashboard(Long adminId) {
        requireAdmin(adminId);
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalUsers", adminMapper.countTotalUsers());
        dashboard.put("todayUsers", adminMapper.countTodayUsers());
        dashboard.put("totalPosts", adminMapper.countTotalPosts());
        dashboard.put("pendingPosts", adminMapper.countPendingPosts());
        dashboard.put("pendingReports", adminMapper.countPendingReports());
        dashboard.put("pendingAuthRecords", adminMapper.countPendingAuthRecords());
        return dashboard;
    }

    @Override
    public Map<String, Object> users(Long adminId, String keyword, String status, Integer page, Integer size) {
        requireAdmin(adminId);
        Page pageValue = page(page, size);
        String safeKeyword = trimToNull(keyword);
        String safeStatus = trimToNull(status);
        return pageResult(
                adminMapper.countUsers(safeKeyword, safeStatus),
                adminMapper.selectUsers(safeKeyword, safeStatus, pageValue.offset(), pageValue.size()),
                pageValue
        );
    }

    @Override
    public Map<String, Object> userDetail(Long adminId, Long userId) {
        requireAdmin(adminId);
        Long safeUserId = positiveId(userId, "userId");
        Map<String, Object> user = adminMapper.selectUserDetail(safeUserId);
        if (user == null) {
            throw new IllegalArgumentException("user not found");
        }
        return user;
    }

    @Override
    public void setUserEnabled(Long adminId, Long userId, boolean enabled) {
        requireAdmin(adminId);
        Long safeUserId = positiveId(userId, "userId");
        requireOne(adminMapper.updateUserEnabled(safeUserId, enabled), "user status update");
        log(adminId, enabled ? "enable_user" : "disable_user", "user_account", safeUserId, null);
    }

    @Override
    public Map<String, Object> posts(Long adminId, String keyword, String plaza, String reviewStatus, Integer page, Integer size) {
        requireAdmin(adminId);
        Page pageValue = page(page, size);
        String safeKeyword = trimToNull(keyword);
        String safePlaza = trimToNull(plaza);
        String safeReviewStatus = trimToNull(reviewStatus);
        return pageResult(
                adminMapper.countPosts(safeKeyword, safePlaza, safeReviewStatus),
                adminMapper.selectPosts(safeKeyword, safePlaza, safeReviewStatus, pageValue.offset(), pageValue.size()),
                pageValue
        );
    }

    @Override
    public Map<String, Object> postDetail(Long adminId, Long postId) {
        requireAdmin(adminId);
        Long safePostId = positiveId(postId, "postId");
        Map<String, Object> post = adminMapper.selectPostDetail(safePostId);
        if (post == null) {
            throw new IllegalArgumentException("post not found");
        }
        return post;
    }

    @Override
    public void setPostReviewStatus(Long adminId, Long postId, String reviewStatus, String reason) {
        requireAdmin(adminId);
        Long safePostId = positiveId(postId, "postId");
        String safeStatus = requiredText(reviewStatus, "reviewStatus");
        if (!"visible".equals(safeStatus) && !"rejected".equals(safeStatus) && !"pending".equals(safeStatus)) {
            throw new IllegalArgumentException("reviewStatus is invalid");
        }
        requireOne(adminMapper.updatePostReviewStatus(safePostId, safeStatus), "post review update");
        log(adminId, "review_post", "home_post", safePostId, reason == null ? safeStatus : safeStatus + ": " + reason);
    }

    @Override
    public void setPostDeleted(Long adminId, Long postId, boolean deleted) {
        requireAdmin(adminId);
        Long safePostId = positiveId(postId, "postId");
        requireOne(adminMapper.updatePostDeleted(safePostId, deleted), "post delete update");
        log(adminId, deleted ? "delete_post" : "restore_post", "home_post", safePostId, null);
    }

    @Override
    public Map<String, Object> reports(Long adminId, String status, Integer page, Integer size) {
        requireAdmin(adminId);
        Page pageValue = page(page, size);
        String safeStatus = trimToNull(status);
        return pageResult(
                adminMapper.countReports(safeStatus),
                adminMapper.selectReports(safeStatus, pageValue.offset(), pageValue.size()),
                pageValue
        );
    }

    @Override
    public void handleReport(Long adminId, Long reportId, String status, String handlingNote) {
        requireAdmin(adminId);
        Long safeReportId = positiveId(reportId, "reportId");
        String safeStatus = requiredText(status, "status");
        if (!"ignored".equals(safeStatus) && !"resolved".equals(safeStatus) && !"pending".equals(safeStatus)) {
            throw new IllegalArgumentException("status is invalid");
        }
        requireOne(adminMapper.updateReportStatus(safeReportId, safeStatus), "report status update");
        log(adminId, "handle_report", "home_post_report", safeReportId, handlingNote == null ? safeStatus : safeStatus + ": " + handlingNote);
    }

    @Override
    public Map<String, Object> authRecords(Long adminId, String status, Integer page, Integer size) {
        requireAdmin(adminId);
        Page pageValue = page(page, size);
        String safeStatus = trimToNull(status);
        return pageResult(
                adminMapper.countAuthRecords(safeStatus),
                adminMapper.selectAuthRecords(safeStatus, pageValue.offset(), pageValue.size()),
                pageValue
        );
    }

    @Override
    @Transactional
    public void approveAuthRecord(Long adminId, Long recordId) {
        requireAdmin(adminId);
        Long safeRecordId = positiveId(recordId, "recordId");
        Map<String, Object> record = requireAuthRecord(safeRecordId);
        Long userId = number(record.get("userId"), "userId");
        Object realNameValue = record.get("realName");
        String realName = realNameValue == null ? null : trimToNull(String.valueOf(realNameValue));
        requireOne(adminMapper.approveAuthRecord(safeRecordId, "校园认证已通过"), "auth record approve");
        requireOne(adminMapper.updateProfileVerified(userId, realName, true, "passed"), "profile verify update");
        adminMapper.updateHomeUserSummaryVerified(userId, true);
        log(adminId, "approve_auth_record", "auth_center_record", safeRecordId, "userId=" + userId);
    }

    @Override
    @Transactional
    public void rejectAuthRecord(Long adminId, Long recordId, String reason) {
        requireAdmin(adminId);
        Long safeRecordId = positiveId(recordId, "recordId");
        Map<String, Object> record = requireAuthRecord(safeRecordId);
        Long userId = number(record.get("userId"), "userId");
        String feedback = trimToNull(reason);
        if (feedback == null) {
            feedback = "认证材料不符合要求，请重新提交";
        }
        requireOne(adminMapper.rejectAuthRecord(safeRecordId, feedback), "auth record reject");
        requireOne(adminMapper.updateProfileVerified(userId, null, false, "rejected"), "profile verify update");
        adminMapper.updateHomeUserSummaryVerified(userId, false);
        log(adminId, "reject_auth_record", "auth_center_record", safeRecordId, feedback);
    }

    @Override
    public List<Map<String, Object>> announcements(Long adminId) {
        requireAdmin(adminId);
        return adminMapper.selectAnnouncements();
    }

    @Override
    @Transactional
    public Map<String, Object> createAnnouncement(Long adminId, Map<String, Object> request) {
        requireAdmin(adminId);
        String title = requiredText(value(request, "title"), "title");
        String content = requiredText(value(request, "content"), "content");
        String status = announcementStatus(value(request, "status"));
        requireOne(adminMapper.insertAnnouncement(title, content, status, adminId), "announcement insert");
        Map<String, Object> announcement = firstAnnouncement();
        log(adminId, "create_announcement", "admin_announcement", number(announcement.get("announcementId"), "announcementId"), title);
        return announcement;
    }

    @Override
    @Transactional
    public Map<String, Object> updateAnnouncement(Long adminId, Long announcementId, Map<String, Object> request) {
        requireAdmin(adminId);
        Long safeAnnouncementId = positiveId(announcementId, "announcementId");
        String title = requiredText(value(request, "title"), "title");
        String content = requiredText(value(request, "content"), "content");
        String status = announcementStatus(value(request, "status"));
        requireOne(adminMapper.updateAnnouncement(safeAnnouncementId, title, content, status), "announcement update");
        log(adminId, "update_announcement", "admin_announcement", safeAnnouncementId, title);
        return requireAnnouncement(safeAnnouncementId);
    }

    @Override
    public void deleteAnnouncement(Long adminId, Long announcementId) {
        requireAdmin(adminId);
        Long safeAnnouncementId = positiveId(announcementId, "announcementId");
        requireOne(adminMapper.deleteAnnouncement(safeAnnouncementId), "announcement delete");
        log(adminId, "delete_announcement", "admin_announcement", safeAnnouncementId, null);
    }

    @Override
    public List<Map<String, Object>> categories(Long adminId) {
        requireAdmin(adminId);
        return adminMapper.selectCategories();
    }

    @Override
    @Transactional
    public Map<String, Object> createCategory(Long adminId, Map<String, Object> request) {
        requireAdmin(adminId);
        String plazaKey = requiredText(value(request, "plazaKey"), "plazaKey");
        String categoryName = requiredText(value(request, "categoryName"), "categoryName");
        int sortWeight = intValue(request, "sortWeight", 100);
        requireOne(adminMapper.insertCategory(plazaKey, categoryName, sortWeight), "category insert");
        Map<String, Object> category = firstCategory();
        log(adminId, "create_category", "home_plaza_category", number(category.get("id"), "id"), plazaKey + "/" + categoryName);
        return category;
    }

    @Override
    @Transactional
    public Map<String, Object> updateCategory(Long adminId, Long categoryId, Map<String, Object> request) {
        requireAdmin(adminId);
        Long safeCategoryId = positiveId(categoryId, "categoryId");
        String plazaKey = requiredText(value(request, "plazaKey"), "plazaKey");
        String categoryName = requiredText(value(request, "categoryName"), "categoryName");
        int sortWeight = intValue(request, "sortWeight", 100);
        requireOne(adminMapper.updateCategory(safeCategoryId, plazaKey, categoryName, sortWeight), "category update");
        log(adminId, "update_category", "home_plaza_category", safeCategoryId, plazaKey + "/" + categoryName);
        return requireCategory(safeCategoryId);
    }

    @Override
    public void setCategoryEnabled(Long adminId, Long categoryId, boolean enabled) {
        requireAdmin(adminId);
        Long safeCategoryId = positiveId(categoryId, "categoryId");
        requireOne(adminMapper.updateCategoryEnabled(safeCategoryId, enabled), "category status update");
        log(adminId, enabled ? "enable_category" : "disable_category", "home_plaza_category", safeCategoryId, null);
    }

    @Override
    @Transactional
    public Map<String, Object> createSystemMessage(Long adminId, Map<String, Object> request) {
        requireAdmin(adminId);
        String targetType = requiredText(value(request, "targetType"), "targetType");
        Long targetUserId = optionalId(value(request, "targetUserId"));
        String title = requiredText(value(request, "title"), "title");
        String content = requiredText(value(request, "content"), "content");
        if (!"all".equals(targetType) && !"user".equals(targetType)) {
            throw new IllegalArgumentException("targetType is invalid");
        }
        if ("user".equals(targetType) && targetUserId == null) {
            throw new IllegalArgumentException("targetUserId is required");
        }
        requireOne(adminMapper.insertSystemMessage(targetType, targetUserId, title, content, adminId), "system message insert");
        log(adminId, "create_system_message", "admin_system_message", targetUserId, title);
        Map<String, Object> result = new HashMap<>();
        result.put("targetType", targetType);
        result.put("targetUserId", targetUserId);
        result.put("title", title);
        result.put("content", content);
        return result;
    }

    @Override
    public Map<String, Object> logs(Long adminId, Integer page, Integer size) {
        requireAdmin(adminId);
        Page pageValue = page(page, size);
        return pageResult(
                adminMapper.countOperationLogs(),
                adminMapper.selectOperationLogs(pageValue.offset(), pageValue.size()),
                pageValue
        );
    }

    private void requireAdmin(Long adminId) {
        Long safeAdminId = positiveId(adminId, "adminId");
        Map<String, Object> admin = adminMapper.selectAdminById(safeAdminId);
        if (admin == null || !truthy(admin.get("enabled"))) {
            throw new SecurityException("admin is required");
        }
    }

    private void log(Long adminId, String action, String targetType, Long targetId, String detail) {
        int affected = adminMapper.insertOperationLog(adminId, action, targetType, targetId, detail);
        if (affected != 1) {
            throw new IllegalStateException("operation log insert affected " + affected + " rows");
        }
    }

    private Map<String, Object> pageResult(int total, List<Map<String, Object>> records, Page page) {
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("page", page.page());
        result.put("size", page.size());
        result.put("records", records);
        return result;
    }

    private Page page(Integer page, Integer size) {
        int safePage = page == null || page < 1 ? DEFAULT_PAGE : page;
        int safeSize = size == null || size < 1 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        return new Page(safePage, safeSize);
    }

    private Map<String, Object> firstAnnouncement() {
        List<Map<String, Object>> announcements = adminMapper.selectAnnouncements();
        if (announcements.isEmpty()) {
            throw new IllegalStateException("announcement insert cannot be confirmed");
        }
        return announcements.get(0);
    }

    private Map<String, Object> firstCategory() {
        List<Map<String, Object>> categories = adminMapper.selectCategories();
        if (categories.isEmpty()) {
            throw new IllegalStateException("category insert cannot be confirmed");
        }
        return categories.get(0);
    }

    private Map<String, Object> requireAnnouncement(Long announcementId) {
        Map<String, Object> announcement = adminMapper.selectAnnouncementById(announcementId);
        if (announcement == null) {
            throw new IllegalArgumentException("announcement not found");
        }
        return announcement;
    }

    private Map<String, Object> requireCategory(Long categoryId) {
        Map<String, Object> category = adminMapper.selectCategoryById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("category not found");
        }
        return category;
    }

    private Map<String, Object> requireAuthRecord(Long recordId) {
        Map<String, Object> record = adminMapper.selectAuthRecordById(recordId);
        if (record == null) {
            throw new IllegalArgumentException("auth record not found");
        }
        String status = String.valueOf(record.get("status"));
        if (!"pending".equals(status)) {
            throw new IllegalArgumentException("auth record is already reviewed");
        }
        return record;
    }

    private String announcementStatus(String status) {
        String safeStatus = trimToNull(status);
        if (safeStatus == null) {
            return "draft";
        }
        if (!"draft".equals(safeStatus) && !"published".equals(safeStatus) && !"offline".equals(safeStatus)) {
            throw new IllegalArgumentException("status is invalid");
        }
        return safeStatus;
    }

    private String value(Map<String, Object> request, String key) {
        if (request == null || request.get(key) == null) {
            return null;
        }
        return String.valueOf(request.get(key));
    }

    private int intValue(Map<String, Object> request, String key, int defaultValue) {
        String value = value(request, key);
        if (trimToNull(value) == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(key + " must be a number");
        }
    }

    private Long optionalId(String value) {
        if (trimToNull(value) == null) {
            return null;
        }
        try {
            long id = Long.parseLong(value);
            return id > 0 ? id : null;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("targetUserId must be a number");
        }
    }

    private Long positiveId(Long id, String field) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(field + " is required");
        }
        return id;
    }

    private Long number(Object value, String field) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value != null) {
            try {
                return Long.parseLong(String.valueOf(value));
            } catch (NumberFormatException ignored) {
                throw new IllegalStateException(field + " is not a number");
            }
        }
        throw new IllegalStateException(field + " is missing");
    }

    private String requiredText(String value, String field) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException(field + " is required");
        }
        return trimmed;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean truthy(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return "true".equalsIgnoreCase(String.valueOf(value)) || "1".equals(String.valueOf(value));
    }

    private void requireOne(int affectedRows, String action) {
        if (affectedRows != 1) {
            throw new IllegalStateException(action + " affected " + affectedRows + " rows");
        }
    }

    private String hashPassword(String salt, String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest((salt + ":" + password).getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private record Page(int page, int size) {
        int offset() {
            return (page - 1) * size;
        }
    }
}
