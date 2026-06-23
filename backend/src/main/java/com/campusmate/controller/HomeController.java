package com.campusmate.controller;

import com.campusmate.common.result.Result;
import com.campusmate.domain.dto.HomePlazaQuery;
import com.campusmate.domain.dto.HomePostCreateRequest;
import com.campusmate.domain.dto.HomePostReportRequest;
import com.campusmate.domain.dto.HomePostReplyRequest;
import com.campusmate.domain.entity.HomePostReport;
import com.campusmate.domain.vo.HomeMatchVO;
import com.campusmate.domain.vo.HomePlazaVO;
import com.campusmate.service.HomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/plaza")
    public Result<HomePlazaVO> getHomePlaza(
            @ModelAttribute HomePlazaQuery query,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        if (currentUserId != null) {
            query.setUserId(currentUserId);
        }
        return Result.ok(homeService.getHomePlaza(query));
    }

    @GetMapping("/match-posts/{id}")
    public Result<HomePlazaVO.HomePostVO> getMatchPostDetail(
            @PathVariable Long id,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        return Result.ok(homeService.getMatchPostDetail(id, currentUserId));
    }

    @GetMapping("/vent-posts/{id}")
    public Result<HomePlazaVO.HomePostVO> getVentPostDetail(
            @PathVariable Long id,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        return Result.ok(homeService.getVentPostDetail(id, currentUserId));
    }

    @PostMapping("/posts")
    public Result<HomePlazaVO.HomePostVO> createPost(
            @RequestBody HomePostCreateRequest request,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        if (request != null && currentUserId != null) {
            request.setUserId(currentUserId);
        }
        return Result.ok(homeService.createPost(request));
    }

    @PutMapping("/match-posts/{id}")
    public Result<HomePlazaVO.HomePostVO> updateMatchPost(
            @PathVariable Long id,
            @RequestBody HomePostCreateRequest request,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        if (request != null && currentUserId != null) {
            request.setUserId(currentUserId);
        }
        return Result.ok(homeService.updatePost(id, request));
    }

    @PostMapping("/vent-posts/{id}/comforts")
    public Result<HomePlazaVO.HomePostComfortVO> submitVentPostComfort(
            @PathVariable Long id,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        return Result.ok(homeService.submitVentPostComfort(id, currentUserId));
    }

    @PostMapping("/vent-posts/{id}/replies")
    public Result<HomePlazaVO.HomePostReplyVO> submitVentPostReply(
            @PathVariable Long id,
            @RequestBody HomePostReplyRequest request,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        if (request != null && currentUserId != null) {
            request.setUserId(currentUserId);
        }
        return Result.ok(homeService.submitVentPostReply(id, request));
    }

    @GetMapping("/notifications")
    public Result<java.util.List<HomePlazaVO.HomeNotificationVO>> getNotifications(
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        return Result.ok(homeService.getNotifications(currentUserId));
    }

    @PostMapping("/notifications/read")
    public Result<Void> markNotificationsRead(
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        homeService.markNotificationsRead(currentUserId);
        return Result.ok(null);
    }

    @PostMapping("/match-posts/{id}/reports")
    public Result<HomePostReport> submitMatchPostReport(
            @PathVariable Long id,
            @RequestBody HomePostReportRequest request,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        if (request != null && currentUserId != null) {
            request.setUserId(currentUserId);
        }
        return Result.ok(homeService.submitMatchPostReport(id, request));
    }

    @GetMapping("/my-matches")
    public Result<HomeMatchVO> getMyMatches(
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        return Result.ok(homeService.getMyMatches(currentUserId));
    }

    @PostMapping("/match-posts/{id}/requests")
    public Result<HomeMatchVO.MatchCardVO> submitMatchPostRequest(
            @PathVariable Long id,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        return Result.ok(homeService.submitMatchPostRequest(id, currentUserId));
    }

    @PostMapping("/match-requests/{id}/approve")
    public Result<HomeMatchVO.MatchCardVO> approveMatchRequest(
            @PathVariable Long id,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        return Result.ok(homeService.approveMatchRequest(id, currentUserId));
    }

    @PostMapping("/match-requests/{id}/reject")
    public Result<HomeMatchVO.MatchCardVO> rejectMatchRequest(
            @PathVariable Long id,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        return Result.ok(homeService.rejectMatchRequest(id, currentUserId));
    }
}
