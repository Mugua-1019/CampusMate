package com.campusmate.service.impl;

import com.campusmate.domain.dto.HomePlazaQuery;
import com.campusmate.domain.dto.HomePostCreateRequest;
import com.campusmate.domain.dto.HomePostReportRequest;
import com.campusmate.domain.dto.HomePostReplyRequest;
import com.campusmate.domain.entity.HomeNotification;
import com.campusmate.domain.entity.HomeMatchRequest;
import com.campusmate.domain.entity.HomePendingMeet;
import com.campusmate.domain.entity.HomePlazaCategory;
import com.campusmate.domain.entity.HomePlazaTab;
import com.campusmate.domain.entity.HomePost;
import com.campusmate.domain.entity.HomePostReport;
import com.campusmate.domain.entity.HomePostReply;
import com.campusmate.domain.entity.HomeUserSummary;
import com.campusmate.domain.entity.UserProfile;
import com.campusmate.domain.vo.HomeMatchVO;
import com.campusmate.domain.vo.HomePlazaVO;
import com.campusmate.mapper.HomeConfigMapper;
import com.campusmate.mapper.HomePostMapper;
import com.campusmate.service.HomeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    private static final String VENT_PLAZA = "vent";
    private static final Set<String> VERIFY_STATUS_APPROVED = Set.of("passed", "approved");
    private static final String REPORT_STATUS_PENDING = "pending";
    private static final String REPLY_STATUS_VISIBLE = "visible";
    private static final String NOTIFICATION_STATUS_UNREAD = "unread";
    private static final String MATCH_REQUEST_PENDING = "pending";
    private static final String MATCH_REQUEST_APPROVED = "approved";

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

    @Override
    public HomePlazaVO.HomePostVO createPost(HomePostCreateRequest request) {
        Long userId = request == null ? null : request.getUserId();
        if (userId == null || userId <= 0) {
            throw new SecurityException("please login first");
        }
        requireVerifiedUser(userId);

        String plaza = requiredText(request.getPlaza(), "plaza");
        if (!MATCH_PLAZA.equals(plaza) && !VENT_PLAZA.equals(plaza)) {
            throw new IllegalArgumentException("plaza must be match or vent");
        }

        HomeUserSummary userSummary = homeConfigMapper.selectUserSummary(userId);
        String nickname = trimToNull(userSummary == null ? null : userSummary.getNickname());
        boolean verified = userSummary != null
                && Boolean.TRUE.equals(userSummary.getVerified())
                && VERIFY_STATUS_APPROVED.contains(userSummary.getVerifyStatus());
        boolean anonymous = Boolean.TRUE.equals(request.getAnonymous());

        HomePost post = new HomePost();
        post.setPlaza(plaza);
        post.setPublisherUserId(userId);
        post.setCategory(limitText(requiredText(request.getCategory(), "category"), 40));
        post.setTitle(limitText(requiredText(request.getTitle(), "title"), 80));
        post.setStatus("matching");
        post.setTags(limitText(joinTextList(request.getTags()), 255));
        post.setDescription(limitText(requiredText(request.getDescription(), "description"), 500));
        post.setExpectedTime(limitText(defaultText(request.getTime(), VENT_PLAZA.equals(plaza) ? "随时" : "时间待定"), 80));
        post.setExpectedLocation(limitText(defaultText(request.getLocation(), VENT_PLAZA.equals(plaza) ? "在线文字" : "地点待定"), 120));
        post.setAaFee(limitText(defaultText(request.getAaFee(), MATCH_PLAZA.equals(plaza) ? "AA制，费用待定" : "无需费用"), 80));
        post.setPublisherName(nickname == null ? "CampusMate 同学" : nickname);
        post.setAvatarText(anonymous ? "匿" : buildAvatarText(nickname));
        post.setAnonymous(anonymous);
        post.setVerified(verified);
        post.setCurrentCount(0);
        post.setMaxCount(normalizeMaxCount(request.getMaxCount(), VENT_PLAZA.equals(plaza) ? 40 : 2));

        if (VENT_PLAZA.equals(plaza)) {
            post.setPublisherStatus("等待倾听中");
            post.setPublisherStatusNote("TA 想收到一段温柔回应");
            post.setCurrentState(limitText(joinTextList(request.getCurrentState()), 255));
            post.setHopeYouCan(limitText(joinTextList(request.getHopeYouCan()), 255));
            post.setPreferredWay(limitText(joinTextList(request.getPreferredWay()), 255));
            post.setGentleReplies("");
        }

        requireOne(homePostMapper.insertPost(post), "home post insert");
        return toPostVO(post);
    }

    @Override
    public HomePlazaVO.HomePostVO updatePost(Long postId, HomePostCreateRequest request) {
        Long userId = request == null ? null : request.getUserId();
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("post id is required");
        }
        if (userId == null || userId <= 0) {
            throw new SecurityException("please login first");
        }

        HomePost current = homePostMapper.selectVisibleMatchPostById(postId);
        if (current == null) {
            throw new IllegalArgumentException("match post not found");
        }
        if (!userId.equals(current.getPublisherUserId())) {
            throw new SecurityException("only publisher can edit this post");
        }

        HomePost post = new HomePost();
        post.setId(postId);
        post.setPublisherUserId(userId);
        post.setCategory(limitText(requiredText(request.getCategory(), "category"), 40));
        post.setTitle(limitText(requiredText(request.getTitle(), "title"), 80));
        post.setTags(limitText(joinTextList(request.getTags()), 255));
        post.setDescription(limitText(requiredText(request.getDescription(), "description"), 500));
        post.setExpectedTime(limitText(defaultText(request.getTime(), "时间待定"), 80));
        post.setExpectedLocation(limitText(defaultText(request.getLocation(), "地点待定"), 120));
        post.setAaFee(limitText(defaultText(request.getAaFee(), "AA制，费用待定"), 80));
        post.setMaxCount(normalizeMaxCount(request.getMaxCount(), 2));
        post.setAnonymous(Boolean.TRUE.equals(request.getAnonymous()));

        requireOne(homePostMapper.updateMatchPost(post), "home post update");
        HomePost updated = homePostMapper.selectVisibleMatchPostById(postId);
        return toPostVO(updated == null ? post : updated);
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

    @Override
    public HomeMatchVO getMyMatches(Long userId) {
        requireVerifiedUser(userId);
        HomeMatchVO vo = new HomeMatchVO();
        List<HomeMatchVO.MatchCardVO> startedPosts = homePostMapper.selectStartedMatchRequests(userId).stream()
                .map(request -> toMatchCardVO(request, true))
                .collect(Collectors.toList());
        List<HomeMatchVO.MatchCardVO> joinedPosts = homePostMapper.selectJoinedMatchRequests(userId).stream()
                .map(request -> toMatchCardVO(request, false))
                .collect(Collectors.toList());
        int pendingCount = homePostMapper.countMatchRequestsByStatus(userId, MATCH_REQUEST_PENDING);
        int approvedCount = homePostMapper.countMatchRequestsByStatus(userId, MATCH_REQUEST_APPROVED);
        int totalCount = startedPosts.size() + joinedPosts.size();

        vo.setStartedPosts(startedPosts);
        vo.setJoinedPosts(joinedPosts);
        vo.setRecentMatches(homePostMapper.selectRecentApprovedMatchRequests(userId).stream()
                .map(request -> toRecentMatchVO(request, userId))
                .collect(Collectors.toList()));
        vo.setMetrics(List.of(
                new HomeMatchVO.MetricVO("总匹配数", String.valueOf(totalCount), "我发起和我参与的申请", "pink"),
                new HomeMatchVO.MetricVO("待处理", String.valueOf(pendingCount), "有新请求待同意", "purple"),
                new HomeMatchVO.MetricVO("已完成", String.valueOf(approvedCount), "同意后才算成功匹配", "blue"),
                new HomeMatchVO.MetricVO("互动好感", "98%", "缘分指数很高", "yellow"),
                new HomeMatchVO.MetricVO("本周新增", String.valueOf(homePostMapper.countStartedMatchPosts(userId)), "我发起的帖子", "green")
        ));
        return vo;
    }

    @Override
    @Transactional
    public HomeMatchVO.MatchCardVO submitMatchPostRequest(Long postId, Long userId) {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("post id is required");
        }
        requireVerifiedUser(userId);
        HomePost post = homePostMapper.selectVisibleMatchPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("match post not found");
        }
        if (post.getPublisherUserId() == null) {
            throw new IllegalStateException("match post publisher is missing");
        }
        if (post.getPublisherUserId().equals(userId)) {
            throw new IllegalArgumentException("cannot request your own match post");
        }
        if (safeInt(post.getCurrentCount()) >= safeInt(post.getMaxCount()) || "full".equals(post.getStatus())) {
            throw new IllegalStateException("match post is full");
        }

        HomeMatchRequest existed = homePostMapper.selectMatchRequestByPostAndRequester(postId, userId);
        if (existed == null) {
            HomeMatchRequest request = new HomeMatchRequest();
            request.setPostId(postId);
            request.setRequesterUserId(userId);
            request.setStatus(MATCH_REQUEST_PENDING);
            request.setCreatedAt(LocalDateTime.now());
            request.setUpdatedAt(request.getCreatedAt());
            requireOne(homePostMapper.insertMatchRequest(request), "match request insert");
            existed = request;
        }
        return toMatchCardVO(homePostMapper.selectMatchRequestDetailById(existed.getRequestId()), false);
    }

    @Override
    @Transactional
    public HomeMatchVO.MatchCardVO approveMatchRequest(Long requestId, Long userId) {
        if (requestId == null || requestId <= 0) {
            throw new IllegalArgumentException("request id is required");
        }
        requireVerifiedUser(userId);
        HomeMatchRequest request = homePostMapper.selectMatchRequestDetailById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("match request not found");
        }
        if (!userId.equals(request.getPublisherUserId())) {
            throw new SecurityException("only publisher can approve match request");
        }
        if (!MATCH_REQUEST_PENDING.equals(request.getStatus())) {
            throw new IllegalStateException("only pending match request can be approved");
        }
        requireOne(homePostMapper.approveMatchRequest(requestId, userId), "match request approve");
        requireOne(homePostMapper.incrementMatchPostCount(request.getPostId()), "match post count update");
        return toMatchCardVO(homePostMapper.selectMatchRequestDetailById(requestId), true);
    }

    @Override
    @Transactional
    public HomeMatchVO.MatchCardVO rejectMatchRequest(Long requestId, Long userId) {
        if (requestId == null || requestId <= 0) {
            throw new IllegalArgumentException("request id is required");
        }
        requireVerifiedUser(userId);
        HomeMatchRequest request = homePostMapper.selectMatchRequestDetailById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("match request not found");
        }
        if (!userId.equals(request.getPublisherUserId())) {
            throw new SecurityException("only publisher can reject match request");
        }
        if (!MATCH_REQUEST_PENDING.equals(request.getStatus())) {
            throw new IllegalStateException("only pending match request can be rejected");
        }
        requireOne(homePostMapper.rejectMatchRequest(requestId, userId), "match request reject");
        return toMatchCardVO(homePostMapper.selectMatchRequestDetailById(requestId), true);
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
        vo.setAaFee(post.getAaFee());
        vo.setPublisherUserId(post.getPublisherUserId());
        vo.setPublisherName(Boolean.TRUE.equals(post.getAnonymous()) ? "匿名同学" : post.getPublisherName());
        vo.setPublisherAvatarUrl(post.getPublisherAvatarUrl());
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

    private HomeMatchVO.MatchCardVO toMatchCardVO(HomeMatchRequest request, boolean asPublisher) {
        HomeMatchVO.MatchCardVO vo = new HomeMatchVO.MatchCardVO();
        vo.setRequestId(request.getRequestId());
        vo.setPostId(request.getPostId());
        vo.setPeerUserId(asPublisher ? request.getRequesterUserId() : request.getPublisherUserId());
        vo.setTitle(request.getTitle());
        vo.setCategory(request.getCategory());
        vo.setDescription(request.getDescription());
        vo.setLocation(request.getExpectedLocation());
        vo.setTime(request.getExpectedTime());
        vo.setPartner(asPublisher ? request.getRequesterName() : request.getPublisherName());
        vo.setAvatarUrl(asPublisher ? request.getRequesterAvatarUrl() : request.getPublisherAvatarUrl());
        vo.setRequestCount(safeInt(request.getRequestCount()));
        vo.setStatus(request.getStatus());
        vo.setStateText(buildMatchStateText(request.getStatus(), asPublisher));
        vo.setTags(List.of(request.getCategory(), vo.getStateText()));
        vo.setElapsed(buildElapsed(request.getCreatedAt()));
        return vo;
    }

    private HomeMatchVO.RecentMatchVO toRecentMatchVO(HomeMatchRequest request, Long userId) {
        HomeMatchVO.RecentMatchVO vo = new HomeMatchVO.RecentMatchVO();
        vo.setTitle(request.getTitle());
        vo.setCategory(request.getCategory());
        vo.setAvatarUrl(userId.equals(request.getRequesterUserId()) ? request.getPublisherAvatarUrl() : request.getRequesterAvatarUrl());
        vo.setTime(buildElapsed(request.getUpdatedAt()));
        return vo;
    }

    private String buildMatchStateText(String status, boolean asPublisher) {
        if (MATCH_REQUEST_APPROVED.equals(status)) {
            return "已成功匹配";
        }
        if (MATCH_REQUEST_PENDING.equals(status)) {
            return asPublisher ? "待我同意" : "等待发起者同意";
        }
        if ("rejected".equals(status)) {
            return "已拒绝";
        }
        return "已结束";
    }

    private String buildElapsed(LocalDateTime time) {
        if (time == null) {
            return "刚刚";
        }
        long days = ChronoUnit.DAYS.between(time.toLocalDate(), LocalDate.now());
        if (days <= 0) {
            return "今天";
        }
        if (days == 1) {
            return "昨天";
        }
        if (days < 7) {
            return days + " 天前";
        }
        long weeks = days / 7;
        if (weeks < 4) {
            return weeks + " 周前";
        }
        return time.toLocalDate().toString();
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

    private String defaultText(String value, String fallback) {
        String trimmed = trimToNull(value);
        return trimmed == null ? fallback : trimmed;
    }

    private String joinTextList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return values.stream()
                .map(this::trimToNull)
                .filter(value -> value != null)
                .collect(Collectors.joining(","));
    }

    private int normalizeMaxCount(Integer value, int fallback) {
        if (value == null || value <= 0) {
            return fallback;
        }
        return Math.min(value, 99);
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
