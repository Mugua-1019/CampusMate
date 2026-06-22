package com.campusmate.service.impl;

import com.campusmate.domain.dto.HomePlazaQuery;
import com.campusmate.domain.dto.HomePostReportRequest;
import com.campusmate.domain.dto.HomePostReplyRequest;
import com.campusmate.domain.entity.HomeNotification;
import com.campusmate.domain.entity.HomePendingMeet;
import com.campusmate.domain.entity.HomePlazaCategory;
import com.campusmate.domain.entity.HomePlazaTab;
import com.campusmate.domain.entity.HomePost;
import com.campusmate.domain.entity.HomePostReport;
import com.campusmate.domain.entity.HomePostReply;
import com.campusmate.domain.entity.HomeUserSummary;
import com.campusmate.domain.entity.UserProfile;
import com.campusmate.domain.vo.HomePlazaVO;
import com.campusmate.mapper.HomeConfigMapper;
import com.campusmate.mapper.HomePostMapper;
import com.campusmate.service.HomeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HomeServiceImpl implements HomeService {

    private static final String ALL_CATEGORY = "全部";
    private static final String MATCH_PLAZA = "match";
    private static final Set<String> VERIFY_STATUS_APPROVED = Set.of("passed", "approved");
    private static final String REPORT_STATUS_PENDING = "pending";
    private static final String REPLY_STATUS_VISIBLE = "visible";
    private static final String NOTIFICATION_STATUS_UNREAD = "unread";

    private final HomePostMapper homePostMapper;
    private final HomeConfigMapper homeConfigMapper;

    public HomeServiceImpl(HomePostMapper homePostMapper, HomeConfigMapper homeConfigMapper) {
        this.homePostMapper = homePostMapper;
        this.homeConfigMapper = homeConfigMapper;
    }

    @Override
    public HomePlazaVO getHomePlaza(HomePlazaQuery query) {
        List<HomePlazaTab> tabs = homeConfigMapper.selectTabs();
        List<HomePlazaCategory> categories = homeConfigMapper.selectCategories();
        normalizeQuery(query, tabs, categories);

        HomePlazaVO vo = new HomePlazaVO();
        vo.setActivePlaza(query.getPlaza());
        vo.setSelectedCategory(query.getCategory());
        vo.setTabs(buildTabs(tabs, categories));
        vo.setUserSummary(buildUserSummary(query.getUserId()));
        vo.setPosts(homePostMapper.selectVisiblePosts(query).stream()
                .map(this::toPostVO)
                .collect(Collectors.toList()));
        return vo;
    }

    @Override
    public HomePlazaVO.HomePostVO getMatchPostDetail(Long id, Long userId) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("post id is required");
        }
        requireVerifiedUser(userId);
        HomePost post = homePostMapper.selectVisibleMatchPostById(id);
        if (post == null) {
            throw new IllegalArgumentException("match post not found");
        }
        return toPostVO(post);
    }

    @Override
    public HomePlazaVO.HomePostVO getVentPostDetail(Long id, Long userId) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("post id is required");
        }
        requireVerifiedUser(userId);
        HomePost post = homePostMapper.selectVisibleVentPostById(id);
        if (post == null) {
            throw new IllegalArgumentException("vent post not found");
        }
        HomePlazaVO.HomePostVO vo = toPostVO(post);
        vo.setReplies(homePostMapper.selectVisibleRepliesByPostId(id).stream()
                .map(this::toReplyVO)
                .collect(Collectors.toList()));
        vo.setSimilarPosts(homePostMapper.selectSimilarVisibleVentPosts(id, post.getCategory()).stream()
                .map(this::toPostVO)
                .collect(Collectors.toList()));
        return vo;
    }

    private void requireVerifiedUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new SecurityException("请先登录并完成校园认证后查看帖子");
        }
        UserProfile profile = homePostMapper.selectVerifyStatus(userId);
        if (profile == null
                || !Boolean.TRUE.equals(profile.getVerified())
                || !VERIFY_STATUS_APPROVED.contains(profile.getVerifyStatus())) {
            throw new SecurityException("请先完成校园认证后查看帖子");
        }
    }

    @Override
    public HomePlazaVO.HomePostComfortVO submitVentPostComfort(Long postId, Long userId) {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("post id is required");
        }
        requireVerifiedUser(userId);
        HomePost post = homePostMapper.selectVisibleVentPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("vent post not found");
        }
        requireOne(homePostMapper.incrementVentPostComfort(postId), "post comfort update");
        if (post.getPublisherUserId() != null && !post.getPublisherUserId().equals(userId)) {
            requireOne(homePostMapper.upsertLikeNotification(post.getPublisherUserId(), postId), "like notification upsert");
        }
        HomePost updatedPost = homePostMapper.selectVisibleVentPostById(postId);
        HomePlazaVO.HomePostComfortVO vo = new HomePlazaVO.HomePostComfortVO();
        vo.setPostId(postId);
        vo.setCurrentCount(safeInt(updatedPost == null ? null : updatedPost.getCurrentCount()));
        return vo;
    }

    @Override
    public HomePlazaVO.HomePostReplyVO submitVentPostReply(Long postId, HomePostReplyRequest request) {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("post id is required");
        }
        Long userId = request == null ? null : request.getUserId();
        requireVerifiedUser(userId);
        if (homePostMapper.selectVisibleVentPostById(postId) == null) {
            throw new IllegalArgumentException("vent post not found");
        }

        HomeUserSummary userSummary = homeConfigMapper.selectUserSummary(userId);
        String authorName = trimToNull(userSummary == null ? null : userSummary.getNickname());
        HomePostReply reply = new HomePostReply();
        reply.setPostId(postId);
        reply.setUserId(userId);
        reply.setAuthorName(authorName == null ? "CampusMate 同学" : authorName);
        reply.setAvatarText(buildAvatarText(authorName));
        reply.setContent(limitText(requiredText(request == null ? null : request.getContent(), "content"), 1000));
        reply.setLikeCount(0);
        reply.setReviewStatus(REPLY_STATUS_VISIBLE);
        reply.setDeleted(false);
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUpdatedAt(reply.getCreatedAt());
        requireOne(homePostMapper.insertReply(reply), "post reply insert");
        return toReplyVO(reply);
    }

    @Override
    public List<HomePlazaVO.HomeNotificationVO> getNotifications(Long userId) {
        if (userId == null || userId <= 0) {
            throw new SecurityException("please login first");
        }
        return homePostMapper.selectNotificationsByUserId(userId).stream()
                .map(this::toNotificationVO)
                .collect(Collectors.toList());
    }

    @Override
    public void markNotificationsRead(Long userId) {
        if (userId == null || userId <= 0) {
            throw new SecurityException("please login first");
        }
        homePostMapper.markNotificationsRead(userId);
    }

    @Override
    public HomePostReport submitMatchPostReport(Long postId, HomePostReportRequest request) {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("post id is required");
        }
        if (homePostMapper.selectVisibleMatchPostById(postId) == null) {
            throw new IllegalArgumentException("match post not found");
        }

        HomePostReport report = new HomePostReport();
        report.setPostId(postId);
        report.setReporterUserId(request == null ? null : request.getUserId());
        report.setReason(requiredText(request == null ? null : request.getReason(), "reason"));
        report.setDetail(limitText(trimToNull(request == null ? null : request.getDetail()), 1000));
        report.setContact(limitText(trimToNull(request == null ? null : request.getContact()), 120));
        report.setStatus(REPORT_STATUS_PENDING);
        report.setCreatedAt(LocalDateTime.now());
        requireOne(homePostMapper.insertReport(report), "post report insert");
        return report;
    }

    private void normalizeQuery(HomePlazaQuery query, List<HomePlazaTab> tabs, List<HomePlazaCategory> categories) {
        List<String> tabKeys = tabs.stream()
                .map(HomePlazaTab::getPlazaKey)
                .collect(Collectors.toList());
        if (!tabKeys.contains(query.getPlaza())) {
            query.setPlaza(tabKeys.isEmpty() ? MATCH_PLAZA : tabKeys.get(0));
        }

        List<String> activeCategories = categories.stream()
                .filter(category -> query.getPlaza().equals(category.getPlazaKey()))
                .map(HomePlazaCategory::getCategoryName)
                .collect(Collectors.toList());
        if (!activeCategories.contains(query.getCategory())) {
            query.setCategory(ALL_CATEGORY);
        }
    }

    private List<HomePlazaVO.PlazaTabVO> buildTabs(List<HomePlazaTab> tabs, List<HomePlazaCategory> categories) {
        Map<String, List<String>> categoriesByPlaza = categories.stream()
                .collect(Collectors.groupingBy(
                        HomePlazaCategory::getPlazaKey,
                        Collectors.mapping(HomePlazaCategory::getCategoryName, Collectors.toList())
                ));
        return tabs.stream()
                .map(tab -> new HomePlazaVO.PlazaTabVO(
                        tab.getPlazaKey(),
                        tab.getLabel(),
                        tab.getDescription(),
                        categoriesByPlaza.getOrDefault(tab.getPlazaKey(), Collections.singletonList(ALL_CATEGORY))
                ))
                .collect(Collectors.toList());
    }

    private HomePlazaVO.UserSummaryVO buildUserSummary(Long userId) {
        HomePlazaVO.UserSummaryVO summary = new HomePlazaVO.UserSummaryVO();
        if (userId == null) {
            summary.setVerified(false);
            summary.setNickname("游客同学");
            summary.setAvatarUrl(null);
            summary.setStats(Collections.emptyList());
            summary.setPendingMeet(new HomePlazaVO.PendingMeetVO());
            return summary;
        }

        HomeUserSummary userSummary = homeConfigMapper.selectUserSummary(userId);
        summary.setVerified(userSummary != null
                && Boolean.TRUE.equals(userSummary.getVerified())
                && VERIFY_STATUS_APPROVED.contains(userSummary.getVerifyStatus()));
        summary.setNickname(userSummary == null ? "游客同学" : userSummary.getNickname());
        summary.setAvatarUrl(userSummary == null ? null : userSummary.getAvatarUrl());
        summary.setStats(homeConfigMapper.selectUserStats(userId).stream()
                .map(stat -> new HomePlazaVO.StatVO(stat.getStatLabel(), safeInt(stat.getStatValue())))
                .collect(Collectors.toList()));
        summary.setPendingMeet(toPendingMeetVO(homeConfigMapper.selectPendingMeet(userId)));
        return summary;
    }

    private HomePlazaVO.PendingMeetVO toPendingMeetVO(HomePendingMeet pendingMeet) {
        HomePlazaVO.PendingMeetVO vo = new HomePlazaVO.PendingMeetVO();
        if (pendingMeet == null) {
            return vo;
        }
        vo.setTitle(pendingMeet.getTitle());
        vo.setPartner(pendingMeet.getPartner());
        vo.setCategory(pendingMeet.getCategory());
        vo.setTime(pendingMeet.getMeetTime());
        vo.setLocation(pendingMeet.getLocation());
        vo.setStatus(pendingMeet.getStatus());
        return vo;
    }

    private HomePlazaVO.HomePostVO toPostVO(HomePost post) {
        HomePlazaVO.HomePostVO vo = new HomePlazaVO.HomePostVO();
        int currentCount = safeInt(post.getCurrentCount());
        int maxCount = safeInt(post.getMaxCount());

        vo.setId(post.getId());
        vo.setPlaza(post.getPlaza());
        vo.setCategory(post.getCategory());
        vo.setTitle(post.getTitle());
        vo.setStatus(post.getStatus());
        vo.setTags(splitTags(post.getTags()));
        vo.setDescription(post.getDescription());
        vo.setTime(post.getExpectedTime());
        vo.setLocation(post.getExpectedLocation());
        vo.setPublisherName(Boolean.TRUE.equals(post.getAnonymous()) ? "匿名同学" : post.getPublisherName());
        vo.setPublisherStatus(post.getPublisherStatus());
        vo.setPublisherStatusNote(post.getPublisherStatusNote());
        vo.setAvatarText(post.getAvatarText());
        vo.setAnonymous(Boolean.TRUE.equals(post.getAnonymous()));
        vo.setVerified(Boolean.TRUE.equals(post.getVerified()));
        vo.setCurrentCount(currentCount);
        vo.setMaxCount(maxCount);
        vo.setFull(currentCount >= maxCount || "full".equals(post.getStatus()));
        vo.setCurrentState(splitTextList(post.getCurrentState()));
        vo.setHopeYouCan(splitTextList(post.getHopeYouCan()));
        vo.setPreferredWay(splitTextList(post.getPreferredWay()));
        vo.setGentleReplies(splitReplyList(post.getGentleReplies()));
        vo.setReplies(Collections.emptyList());
        vo.setSimilarPosts(Collections.emptyList());
        return vo;
    }

    private HomePlazaVO.HomePostReplyVO toReplyVO(HomePostReply reply) {
        HomePlazaVO.HomePostReplyVO vo = new HomePlazaVO.HomePostReplyVO();
        vo.setId(reply.getId());
        vo.setPostId(reply.getPostId());
        vo.setUserId(reply.getUserId());
        vo.setAuthorName(reply.getAuthorName());
        vo.setAvatarText(reply.getAvatarText());
        vo.setContent(reply.getContent());
        vo.setLikeCount(safeInt(reply.getLikeCount()));
        vo.setCreatedAt(reply.getCreatedAt() == null ? null : reply.getCreatedAt().toString());
        return vo;
    }

    private HomePlazaVO.HomeNotificationVO toNotificationVO(HomeNotification notification) {
        HomePlazaVO.HomeNotificationVO vo = new HomePlazaVO.HomeNotificationVO();
        int count = safeInt(notification.getAggregateCount());
        vo.setId(notification.getId());
        vo.setCount(count);
        vo.setUnread(NOTIFICATION_STATUS_UNREAD.equals(notification.getReadStatus()));
        vo.setMessage("有 " + count + " 人为你点赞");
        vo.setUpdatedAt(notification.getUpdatedAt() == null ? null : notification.getUpdatedAt().toString());
        return vo;
    }

    private List<String> splitTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());
    }

    private List<String> splitTextList(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split("[,，\\n;；]"))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    private List<String> splitReplyList(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split("\\\\n|\\r?\\n"))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
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

    private String limitText(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private String buildAvatarText(String authorName) {
        String trimmed = trimToNull(authorName);
        if (trimmed == null) {
            return "同";
        }
        return trimmed.substring(0, Math.min(1, trimmed.length()));
    }

    private void requireOne(int affectedRows, String action) {
        if (affectedRows != 1) {
            throw new IllegalStateException(action + " affected " + affectedRows + " rows");
        }
    }
}
