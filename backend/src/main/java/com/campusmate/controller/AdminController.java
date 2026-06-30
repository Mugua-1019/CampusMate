package com.campusmate.controller;

import com.campusmate.common.result.Result;
import com.campusmate.service.AdminService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/auth/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, Object> request) {
        return Result.ok(adminService.login(request));
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard(@RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        return Result.ok(adminService.dashboard(adminId));
    }

    @GetMapping("/users")
    public Result<Map<String, Object>> users(@RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) String status,
                                             @RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "20") Integer size,
                                             @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        return Result.ok(adminService.users(adminId, keyword, status, page, size));
    }

    @GetMapping("/users/{id}")
    public Result<Map<String, Object>> userDetail(@PathVariable Long id,
                                                  @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        return Result.ok(adminService.userDetail(adminId, id));
    }

    @PostMapping("/users/{id}/disable")
    public Result<Void> disableUser(@PathVariable Long id,
                                    @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        adminService.setUserEnabled(adminId, id, false);
        return Result.ok(null);
    }

    @PostMapping("/users/{id}/enable")
    public Result<Void> enableUser(@PathVariable Long id,
                                   @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        adminService.setUserEnabled(adminId, id, true);
        return Result.ok(null);
    }

    @GetMapping("/posts")
    public Result<Map<String, Object>> posts(@RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) String plaza,
                                             @RequestParam(required = false) String reviewStatus,
                                             @RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "20") Integer size,
                                             @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        return Result.ok(adminService.posts(adminId, keyword, plaza, reviewStatus, page, size));
    }

    @GetMapping("/posts/{id}")
    public Result<Map<String, Object>> postDetail(@PathVariable Long id,
                                                  @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        return Result.ok(adminService.postDetail(adminId, id));
    }

    @PostMapping("/posts/{id}/approve")
    public Result<Void> approvePost(@PathVariable Long id,
                                    @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        adminService.setPostReviewStatus(adminId, id, "visible", null);
        return Result.ok(null);
    }

    @PostMapping("/posts/{id}/reject")
    public Result<Void> rejectPost(@PathVariable Long id,
                                   @RequestBody(required = false) Map<String, Object> request,
                                   @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        adminService.setPostReviewStatus(adminId, id, "rejected", text(request, "reason"));
        return Result.ok(null);
    }

    @PostMapping("/posts/{id}/delete")
    public Result<Void> deletePost(@PathVariable Long id,
                                   @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        adminService.setPostDeleted(adminId, id, true);
        return Result.ok(null);
    }

    @PostMapping("/posts/{id}/restore")
    public Result<Void> restorePost(@PathVariable Long id,
                                    @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        adminService.setPostDeleted(adminId, id, false);
        return Result.ok(null);
    }

    @GetMapping("/reports")
    public Result<Map<String, Object>> reports(@RequestParam(required = false) String status,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "20") Integer size,
                                               @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        return Result.ok(adminService.reports(adminId, status, page, size));
    }

    @PostMapping("/reports/{id}/handle")
    public Result<Void> handleReport(@PathVariable Long id,
                                     @RequestBody(required = false) Map<String, Object> request,
                                     @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        adminService.handleReport(adminId, id, text(request, "status"), text(request, "handlingNote"));
        return Result.ok(null);
    }

    @GetMapping("/auth-records")
    public Result<Map<String, Object>> authRecords(@RequestParam(required = false) String status,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "20") Integer size,
                                                   @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        return Result.ok(adminService.authRecords(adminId, status, page, size));
    }

    @PostMapping("/auth-records/{id}/approve")
    public Result<Void> approveAuthRecord(@PathVariable Long id,
                                          @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        adminService.approveAuthRecord(adminId, id);
        return Result.ok(null);
    }

    @PostMapping("/auth-records/{id}/reject")
    public Result<Void> rejectAuthRecord(@PathVariable Long id,
                                         @RequestBody(required = false) Map<String, Object> request,
                                         @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        adminService.rejectAuthRecord(adminId, id, text(request, "reason"));
        return Result.ok(null);
    }

    @GetMapping("/announcements")
    public Result<List<Map<String, Object>>> announcements(@RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        return Result.ok(adminService.announcements(adminId));
    }

    @PostMapping("/announcements")
    public Result<Map<String, Object>> createAnnouncement(@RequestBody Map<String, Object> request,
                                                          @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        return Result.ok(adminService.createAnnouncement(adminId, request));
    }

    @PutMapping("/announcements/{id}")
    public Result<Map<String, Object>> updateAnnouncement(@PathVariable Long id,
                                                          @RequestBody Map<String, Object> request,
                                                          @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        return Result.ok(adminService.updateAnnouncement(adminId, id, request));
    }

    @DeleteMapping("/announcements/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id,
                                           @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        adminService.deleteAnnouncement(adminId, id);
        return Result.ok(null);
    }

    @GetMapping("/categories")
    public Result<List<Map<String, Object>>> categories(@RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        return Result.ok(adminService.categories(adminId));
    }

    @PostMapping("/categories")
    public Result<Map<String, Object>> createCategory(@RequestBody Map<String, Object> request,
                                                      @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        return Result.ok(adminService.createCategory(adminId, request));
    }

    @PutMapping("/categories/{id}")
    public Result<Map<String, Object>> updateCategory(@PathVariable Long id,
                                                      @RequestBody Map<String, Object> request,
                                                      @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        return Result.ok(adminService.updateCategory(adminId, id, request));
    }

    @PostMapping("/categories/{id}/enable")
    public Result<Void> enableCategory(@PathVariable Long id,
                                       @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        adminService.setCategoryEnabled(adminId, id, true);
        return Result.ok(null);
    }

    @PostMapping("/categories/{id}/disable")
    public Result<Void> disableCategory(@PathVariable Long id,
                                        @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        adminService.setCategoryEnabled(adminId, id, false);
        return Result.ok(null);
    }

    @PostMapping("/system-messages")
    public Result<Map<String, Object>> createSystemMessage(@RequestBody Map<String, Object> request,
                                                           @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        return Result.ok(adminService.createSystemMessage(adminId, request));
    }

    @GetMapping("/logs")
    public Result<Map<String, Object>> logs(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "20") Integer size,
                                            @RequestHeader(value = "X-CampusMate-Admin-Id", required = false) Long adminId) {
        return Result.ok(adminService.logs(adminId, page, size));
    }

    private String text(Map<String, Object> request, String key) {
        if (request == null || request.get(key) == null) {
            return null;
        }
        String value = String.valueOf(request.get(key)).trim();
        return value.isEmpty() ? null : value;
    }
}
