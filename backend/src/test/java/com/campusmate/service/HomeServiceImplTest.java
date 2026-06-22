package com.campusmate.service;

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
import com.campusmate.domain.entity.HomeUserStat;
import com.campusmate.domain.entity.HomeUserSummary;
import com.campusmate.domain.entity.UserProfile;
import com.campusmate.domain.vo.HomeMatchVO;
import com.campusmate.domain.vo.HomePlazaVO;
import com.campusmate.mapper.HomeConfigMapper;
import com.campusmate.mapper.HomePostMapper;
import com.campusmate.service.impl.HomeServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HomeServiceImplTest {

    @Test
    void getHomePlazaNormalizesInvalidCategoryAndCalculatesFullState() {
        CapturingHomePostMapper postMapper = new CapturingHomePostMapper(Arrays.asList(
                post(1L, "match", "学习搭子", "matching", false, true, 3, 4),
                post(2L, "match", "运动搭子", "full", false, true, 5, 5)
        ));
        HomeService service = new HomeServiceImpl(postMapper, new FakeHomeConfigMapper());
        HomePlazaQuery query = new HomePlazaQuery();
        query.setPlaza("match");
        query.setCategory("不存在的分类");

        HomePlazaVO result = service.getHomePlaza(query);

        assertEquals("全部", postMapper.capturedQuery.getCategory());
        assertEquals(2, result.getPosts().size());
        assertFalse(result.getPosts().get(0).isFull());
        assertTrue(result.getPosts().get(1).isFull());
    }

    @Test
    void getHomePlazaMasksAnonymousPublisher() {
        CapturingHomePostMapper postMapper = new CapturingHomePostMapper(List.of(
                post(3L, "vent", "心情不好", "matching", true, false, 0, 1)
        ));
        HomeService service = new HomeServiceImpl(postMapper, new FakeHomeConfigMapper());
        HomePlazaQuery query = new HomePlazaQuery();
        query.setPlaza("vent");
        query.setCategory("心情不好");

        HomePlazaVO result = service.getHomePlaza(query);

        assertEquals("匿名同学", result.getPosts().get(0).getPublisherName());
        assertTrue(result.getPosts().get(0).isAnonymous());
        assertEquals("vent", result.getActivePlaza());
        assertEquals("游客同学", result.getUserSummary().getNickname());
        assertFalse(result.getUserSummary().isVerified());
        assertEquals(0, result.getUserSummary().getStats().size());
        assertEquals(null, result.getUserSummary().getPendingMeet().getTitle());
    }

    @Test
    void getMatchPostDetailOnlyReturnsVisibleMatchPost() {
        CapturingHomePostMapper postMapper = new CapturingHomePostMapper(List.of(
                post(8L, "match", "杩愬姩鎼瓙", "matching", false, true, 2, 5)
        ));
        HomeService service = new HomeServiceImpl(postMapper, new FakeHomeConfigMapper());

        HomePlazaVO.HomePostVO result = service.getMatchPostDetail(8L, 1L);

        assertEquals(8L, result.getId());
        assertEquals("match", result.getPlaza());
        assertEquals(2, result.getCurrentCount());
        assertFalse(result.isFull());
    }

    @Test
    void getMatchPostDetailAllowsApprovedCampusVerifyStatus() {
        CapturingHomePostMapper postMapper = new CapturingHomePostMapper(List.of(
                post(12L, "match", "approved-status-post", "matching", false, true, 1, 4)
        ));
        UserProfile profile = new UserProfile();
        profile.setVerified(true);
        profile.setVerifyStatus("approved");
        postMapper.verifyProfile = profile;
        HomeService service = new HomeServiceImpl(postMapper, new FakeHomeConfigMapper());

        HomePlazaVO.HomePostVO result = service.getMatchPostDetail(12L, 1L);

        assertEquals(12L, result.getId());
    }

    @Test
    void getVentPostDetailReturnsComfortFieldsAndSimilarPosts() {
        HomePost ventPost = post(20L, "vent", "心情不好", "matching", true, false, 24, 40);
        ventPost.setPublisherStatus("回复开放中");
        ventPost.setPublisherStatusNote("TA 目前希望收到回复");
        ventPost.setCurrentState("有点累,想被理解");
        ventPost.setHopeYouCan("耐心倾听,不评判");
        ventPost.setPreferredWay("在线文字聊天,今晚有空");
        ventPost.setGentleReplies("我在，会认真听你说\\n你已经很勇敢了");
        HomePost similarPost = post(21L, "vent", "心情不好", "matching", true, false, 3, 10);
        HomeService service = new HomeServiceImpl(
                new CapturingHomePostMapper(Arrays.asList(ventPost, similarPost)),
                new FakeHomeConfigMapper()
        );

        HomePlazaVO.HomePostVO result = service.getVentPostDetail(20L, 1L);

        assertEquals(20L, result.getId());
        assertEquals("vent", result.getPlaza());
        assertEquals("回复开放中", result.getPublisherStatus());
        assertEquals(24, result.getCurrentCount());
        assertEquals(Arrays.asList("有点累", "想被理解"), result.getCurrentState());
        assertEquals(Arrays.asList("耐心倾听", "不评判"), result.getHopeYouCan());
        assertEquals(Arrays.asList("在线文字聊天", "今晚有空"), result.getPreferredWay());
        assertEquals(2, result.getGentleReplies().size());
        assertEquals(1, result.getSimilarPosts().size());
        assertEquals(21L, result.getSimilarPosts().get(0).getId());
    }

    @Test
    void submitVentPostReplyStoresVisibleFlatReply() {
        CapturingHomePostMapper postMapper = new CapturingHomePostMapper(List.of(
                post(30L, "vent", "reply-test", "matching", true, false, 1, 3)
        ));
        HomeService service = new HomeServiceImpl(postMapper, new FakeHomeConfigMapper());
        HomePostReplyRequest request = new HomePostReplyRequest();
        request.setUserId(6L);
        request.setContent("谢谢你愿意说出来，我会认真听。");

        HomePlazaVO.HomePostReplyVO result = service.submitVentPostReply(30L, request);

        assertEquals(1L, result.getId());
        assertEquals(30L, result.getPostId());
        assertEquals(6L, result.getUserId());
        assertEquals("谢谢你愿意说出来，我会认真听。", result.getContent());
        assertEquals("visible", postMapper.insertedReply.getReviewStatus());
        assertFalse(postMapper.insertedReply.getDeleted());
    }

    @Test
    void createMatchPostStoresFieldsUsedByMatchPlaza() {
        CapturingHomePostMapper postMapper = new CapturingHomePostMapper(List.of());
        HomeService service = new HomeServiceImpl(postMapper, new FakeHomeConfigMapper());
        HomePostCreateRequest request = new HomePostCreateRequest();
        request.setUserId(6L);
        request.setPlaza("match");
        request.setCategory("运动搭子");
        request.setTitle("周五晚找羽毛球搭子");
        request.setDescription("新手友好，地点可以商量。");
        request.setTime("周五 19:00");
        request.setLocation("体育馆 2 号场");
        request.setAaFee("AA制，约 15 元/人");
        request.setMaxCount(4);
        request.setAnonymous(false);
        request.setTags(List.of("新手友好", "校内"));

        HomePlazaVO.HomePostVO result = service.createPost(request);

        assertEquals(1L, result.getId());
        assertEquals("match", result.getPlaza());
        assertEquals("运动搭子", result.getCategory());
        assertEquals("周五 19:00", result.getTime());
        assertEquals("体育馆 2 号场", result.getLocation());
        assertEquals("AA制，约 15 元/人", result.getAaFee());
        assertEquals(4, result.getMaxCount());
        assertEquals("新手友好,校内", postMapper.insertedPost.getTags());
        assertEquals("AA制，约 15 元/人", postMapper.insertedPost.getAaFee());
        assertFalse(result.isAnonymous());
    }

    @Test
    void createVentPostStoresFieldsUsedByVentDetail() {
        CapturingHomePostMapper postMapper = new CapturingHomePostMapper(List.of());
        HomeService service = new HomeServiceImpl(postMapper, new FakeHomeConfigMapper());
        HomePostCreateRequest request = new HomePostCreateRequest();
        request.setUserId(6L);
        request.setPlaza("vent");
        request.setCategory("考试焦虑");
        request.setTitle("考试压力有点大");
        request.setDescription("想找个人听我说说。");
        request.setAnonymous(true);
        request.setTags(List.of("考试焦虑", "在线文字"));
        request.setCurrentState(List.of("考试焦虑"));
        request.setHopeYouCan(List.of("只倾听", "不需要建议"));
        request.setPreferredWay(List.of("在线文字", "今晚可聊"));

        HomePlazaVO.HomePostVO result = service.createPost(request);

        assertEquals("vent", result.getPlaza());
        assertEquals("匿名同学", result.getPublisherName());
        assertEquals("随时", result.getTime());
        assertEquals("在线文字", result.getLocation());
        assertEquals(List.of("考试焦虑"), result.getCurrentState());
        assertEquals(List.of("只倾听", "不需要建议"), result.getHopeYouCan());
        assertEquals(List.of("在线文字", "今晚可聊"), result.getPreferredWay());
        assertEquals("匿", postMapper.insertedPost.getAvatarText());
    }

    @Test
    void submitVentPostComfortIncrementsCountAndCreatesOwnerNotification() {
        HomePost post = post(31L, "vent", "comfort-test", "matching", true, false, 4, 20);
        post.setPublisherUserId(22L);
        CapturingHomePostMapper postMapper = new CapturingHomePostMapper(List.of(post));
        HomeService service = new HomeServiceImpl(postMapper, new FakeHomeConfigMapper());

        HomePlazaVO.HomePostComfortVO result = service.submitVentPostComfort(31L, 21L);

        assertEquals(31L, result.getPostId());
        assertEquals(5, result.getCurrentCount());
        assertEquals(1, postMapper.insertedNotificationCount);
    }

    @Test
    void homePlazaTreatsApprovedVerifyStatusAsVerified() {
        FakeHomeConfigMapper configMapper = new FakeHomeConfigMapper();
        configMapper.verified = true;
        configMapper.verifyStatus = "approved";
        HomeService service = new HomeServiceImpl(new CapturingHomePostMapper(List.of()), configMapper);
        HomePlazaQuery query = new HomePlazaQuery();
        query.setUserId(2L);
        query.setPlaza("match");
        query.setCategory("鍏ㄩ儴");

        HomePlazaVO result = service.getHomePlaza(query);

        assertTrue(result.getUserSummary().isVerified());
    }

    @Test
    void getMatchPostDetailFailsLoudlyWhenMissing() {
        HomeService service = new HomeServiceImpl(new CapturingHomePostMapper(List.of()), new FakeHomeConfigMapper());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.getMatchPostDetail(404L, 1L));

        assertEquals("match post not found", exception.getMessage());
    }

    @Test
    void getMatchPostDetailRequiresPassedCampusVerify() {
        CapturingHomePostMapper postMapper = new CapturingHomePostMapper(List.of(
                post(11L, "match", "杩愬姩鎼瓙", "matching", false, true, 1, 3)
        ));
        UserProfile profile = new UserProfile();
        profile.setVerified(false);
        profile.setVerifyStatus("pending");
        postMapper.verifyProfile = profile;
        HomeService service = new HomeServiceImpl(postMapper, new FakeHomeConfigMapper());

        SecurityException exception = assertThrows(SecurityException.class,
                () -> service.getMatchPostDetail(11L, 1L));

        assertEquals("请先完成校园认证后查看帖子", exception.getMessage());
    }

    @Test
    void submitMatchPostReportStoresPendingReport() {
        CapturingHomePostMapper postMapper = new CapturingHomePostMapper(List.of(
                post(9L, "match", "杩愬姩鎼瓙", "matching", false, true, 1, 3)
        ));
        HomeService service = new HomeServiceImpl(postMapper, new FakeHomeConfigMapper());
        HomePostReportRequest request = new HomePostReportRequest();
        request.setUserId(7L);
        request.setReason("虚假信息");
        request.setDetail("地点和描述不一致");
        request.setContact("13800000000");

        HomePostReport result = service.submitMatchPostReport(9L, request);

        assertEquals(1L, result.getReportId());
        assertEquals(9L, result.getPostId());
        assertEquals(7L, result.getReporterUserId());
        assertEquals("虚假信息", result.getReason());
        assertEquals("pending", result.getStatus());
        assertEquals("地点和描述不一致", postMapper.insertedReport.getDetail());
    }

    @Test
    void submitMatchPostReportRequiresReason() {
        HomeService service = new HomeServiceImpl(new CapturingHomePostMapper(List.of(
                post(10L, "match", "杩愬姩鎼瓙", "matching", false, true, 1, 3)
        )), new FakeHomeConfigMapper());
        HomePostReportRequest request = new HomePostReportRequest();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.submitMatchPostReport(10L, request));

        assertEquals("reason is required", exception.getMessage());
    }

    @Test
    void submitMatchPostRequestCreatesPendingRequestWithoutCountingSuccess() {
        HomePost post = post(41L, "match", "学习搭子", "matching", false, true, 1, 3);
        post.setPublisherUserId(31L);
        CapturingHomePostMapper postMapper = new CapturingHomePostMapper(List.of(post));
        HomeService service = new HomeServiceImpl(postMapper, new FakeHomeConfigMapper());

        HomeMatchVO.MatchCardVO result = service.submitMatchPostRequest(41L, 50L);

        assertEquals(41L, result.getPostId());
        assertEquals("pending", result.getStatus());
        assertEquals("等待发起者同意", result.getStateText());
        assertEquals(1, post.getCurrentCount());
    }

    @Test
    void approveMatchRequestRequiresPublisherAndIncrementsCount() {
        HomePost post = post(42L, "match", "运动搭子", "matching", false, true, 1, 3);
        post.setPublisherUserId(31L);
        CapturingHomePostMapper postMapper = new CapturingHomePostMapper(List.of(post));
        HomeMatchRequest request = matchRequest(1L, 42L, 50L, 31L, "pending");
        postMapper.matchRequests.add(request);
        HomeService service = new HomeServiceImpl(postMapper, new FakeHomeConfigMapper());

        HomeMatchVO.MatchCardVO result = service.approveMatchRequest(1L, 31L);

        assertEquals("approved", result.getStatus());
        assertEquals("已成功匹配", result.getStateText());
        assertEquals(2, post.getCurrentCount());
    }

    private HomePost post(Long id, String plaza, String category, String status, boolean anonymous,
                          boolean verified, int currentCount, int maxCount) {
        HomePost post = new HomePost();
        post.setId(id);
        post.setPlaza(plaza);
        post.setCategory(category);
        post.setTitle("测试需求");
        post.setStatus(status);
        post.setTags("测试,标签");
        post.setDescription("测试描述");
        post.setExpectedTime("今晚");
        post.setExpectedLocation("图书馆");
        post.setPublisherName("真实昵称");
        post.setAvatarText("测");
        post.setAnonymous(anonymous);
        post.setVerified(verified);
        post.setCurrentCount(currentCount);
        post.setMaxCount(maxCount);
        return post;
    }

    private HomeMatchRequest matchRequest(Long requestId, Long postId, Long requesterUserId, Long publisherUserId, String status) {
        HomeMatchRequest request = new HomeMatchRequest();
        request.setRequestId(requestId);
        request.setPostId(postId);
        request.setRequesterUserId(requesterUserId);
        request.setPublisherUserId(publisherUserId);
        request.setStatus(status);
        request.setTitle("测试需求");
        request.setCategory("学习搭子");
        request.setDescription("测试描述");
        request.setExpectedTime("今晚");
        request.setExpectedLocation("图书馆");
        request.setRequesterName("申请同学");
        request.setPublisherName("发布同学");
        request.setRequestCount(1);
        return request;
    }

    private static class CapturingHomePostMapper implements HomePostMapper {
        private final List<HomePost> posts;
        private HomePlazaQuery capturedQuery;
        private HomePostReport insertedReport;
        private HomePostReply insertedReply;
        private HomePost insertedPost;
        private int insertedNotificationCount;
        private UserProfile verifyProfile;
        private final java.util.List<HomeMatchRequest> matchRequests = new java.util.ArrayList<>();

        private CapturingHomePostMapper(List<HomePost> posts) {
            this.posts = posts;
            this.verifyProfile = verifiedProfile();
        }

        @Override
        public List<HomePost> selectVisiblePosts(HomePlazaQuery query) {
            this.capturedQuery = query;
            return posts;
        }

        @Override
        public HomePost selectVisibleMatchPostById(Long id) {
            return posts.stream()
                    .filter(post -> id.equals(post.getId()) && "match".equals(post.getPlaza()))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public HomePost selectVisibleVentPostById(Long id) {
            return posts.stream()
                    .filter(post -> id.equals(post.getId()) && "vent".equals(post.getPlaza()))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<HomePost> selectSimilarVisibleVentPosts(Long id, String category) {
            return posts.stream()
                    .filter(post -> !id.equals(post.getId()))
                    .filter(post -> "vent".equals(post.getPlaza()))
                    .filter(post -> category == null || category.equals(post.getCategory()))
                    .toList();
        }

        @Override
        public List<HomePostReply> selectVisibleRepliesByPostId(Long postId) {
            return List.of();
        }

        @Override
        public List<HomeNotification> selectNotificationsByUserId(Long userId) {
            return List.of();
        }

        @Override
        public UserProfile selectVerifyStatus(Long userId) {
            return verifyProfile;
        }

        @Override
        public int incrementVentPostComfort(Long postId) {
            posts.stream()
                    .filter(post -> postId.equals(post.getId()))
                    .findFirst()
                    .ifPresent(post -> post.setCurrentCount(post.getCurrentCount() + 1));
            return 1;
        }

        @Override
        public int upsertLikeNotification(Long userId, Long postId) {
            insertedNotificationCount++;
            return 1;
        }

        @Override
        public int markNotificationsRead(Long userId) {
            return 1;
        }

        @Override
        public int insertPost(HomePost post) {
            post.setId(1L);
            this.insertedPost = post;
            return 1;
        }

        @Override
        public int insertReply(HomePostReply reply) {
            reply.setId(1L);
            this.insertedReply = reply;
            return 1;
        }

        @Override
        public int insertReport(HomePostReport report) {
            report.setReportId(1L);
            this.insertedReport = report;
            return 1;
        }

        @Override
        public HomeMatchRequest selectMatchRequestByPostAndRequester(Long postId, Long requesterUserId) {
            return matchRequests.stream()
                    .filter(request -> postId.equals(request.getPostId()) && requesterUserId.equals(request.getRequesterUserId()))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public HomeMatchRequest selectMatchRequestDetailById(Long requestId) {
            return matchRequests.stream()
                    .filter(request -> requestId.equals(request.getRequestId()))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<HomeMatchRequest> selectStartedMatchRequests(Long userId) {
            return matchRequests.stream()
                    .filter(request -> userId.equals(request.getPublisherUserId()))
                    .toList();
        }

        @Override
        public List<HomeMatchRequest> selectJoinedMatchRequests(Long userId) {
            return matchRequests.stream()
                    .filter(request -> userId.equals(request.getRequesterUserId()))
                    .toList();
        }

        @Override
        public List<HomeMatchRequest> selectRecentApprovedMatchRequests(Long userId) {
            return matchRequests.stream()
                    .filter(request -> "approved".equals(request.getStatus()))
                    .filter(request -> userId.equals(request.getPublisherUserId()) || userId.equals(request.getRequesterUserId()))
                    .toList();
        }

        @Override
        public int countStartedMatchPosts(Long userId) {
            return (int) posts.stream()
                    .filter(post -> userId.equals(post.getPublisherUserId()))
                    .filter(post -> "match".equals(post.getPlaza()))
                    .count();
        }

        @Override
        public int countMatchRequestsByStatus(Long userId, String status) {
            return (int) matchRequests.stream()
                    .filter(request -> status.equals(request.getStatus()))
                    .filter(request -> userId.equals(request.getPublisherUserId()) || userId.equals(request.getRequesterUserId()))
                    .count();
        }

        @Override
        public int insertMatchRequest(HomeMatchRequest request) {
            request.setRequestId(1L);
            HomePost post = selectVisibleMatchPostById(request.getPostId());
            request.setPublisherUserId(post == null ? null : post.getPublisherUserId());
            request.setTitle(post == null ? "测试需求" : post.getTitle());
            request.setCategory(post == null ? "学习搭子" : post.getCategory());
            request.setDescription(post == null ? "测试描述" : post.getDescription());
            request.setExpectedTime(post == null ? "今晚" : post.getExpectedTime());
            request.setExpectedLocation(post == null ? "图书馆" : post.getExpectedLocation());
            request.setRequesterName("申请同学");
            request.setPublisherName("发布同学");
            request.setRequestCount(1);
            matchRequests.add(request);
            return 1;
        }

        @Override
        public int approveMatchRequest(Long requestId, Long publisherUserId) {
            HomeMatchRequest request = selectMatchRequestDetailById(requestId);
            if (request == null || !publisherUserId.equals(request.getPublisherUserId()) || !"pending".equals(request.getStatus())) {
                return 0;
            }
            request.setStatus("approved");
            return 1;
        }

        @Override
        public int rejectMatchRequest(Long requestId, Long publisherUserId) {
            HomeMatchRequest request = selectMatchRequestDetailById(requestId);
            if (request == null || !publisherUserId.equals(request.getPublisherUserId()) || !"pending".equals(request.getStatus())) {
                return 0;
            }
            request.setStatus("rejected");
            return 1;
        }

        @Override
        public int incrementMatchPostCount(Long postId) {
            HomePost post = selectVisibleMatchPostById(postId);
            if (post == null || post.getCurrentCount() >= post.getMaxCount()) {
                return 0;
            }
            post.setCurrentCount(post.getCurrentCount() + 1);
            if (post.getCurrentCount() >= post.getMaxCount()) {
                post.setStatus("full");
            }
            return 1;
        }

        private UserProfile verifiedProfile() {
            UserProfile profile = new UserProfile();
            profile.setVerified(true);
            profile.setVerifyStatus("passed");
            return profile;
        }
    }

    private static class FakeHomeConfigMapper implements HomeConfigMapper {
        private boolean verified;
        private String verifyStatus = "pending";

        @Override
        public List<HomePlazaTab> selectTabs() {
            return Arrays.asList(tab("match", "匹配广场"), tab("vent", "倾诉广场"));
        }

        @Override
        public List<HomePlazaCategory> selectCategories() {
            return Arrays.asList(
                    category("match", "全部"),
                    category("match", "学习搭子"),
                    category("match", "运动搭子"),
                    category("vent", "全部"),
                    category("vent", "心情不好")
            );
        }

        @Override
        public HomeUserSummary selectUserSummary(Long userId) {
            HomeUserSummary summary = new HomeUserSummary();
            summary.setUserId(userId);
            summary.setNickname("小星同学");
            summary.setAvatarUrl("/testimage/moren.png");
            summary.setVerified(verified);
            summary.setVerifyStatus(verifyStatus);
            return summary;
        }

        @Override
        public List<HomeUserStat> selectUserStats(Long userId) {
            HomeUserStat stat = new HomeUserStat();
            stat.setUserId(userId);
            stat.setStatLabel("今日可匹配");
            stat.setStatValue(3);
            return List.of(stat);
        }

        @Override
        public HomePendingMeet selectPendingMeet(Long userId) {
            HomePendingMeet meet = new HomePendingMeet();
            meet.setUserId(userId);
            meet.setTitle("周末操场跑步");
            meet.setPartner("风一同学");
            meet.setCategory("运动搭子");
            meet.setMeetTime("明天 08:00");
            meet.setLocation("操场西侧入口");
            meet.setStatus("等待你确认");
            return meet;
        }

        private HomePlazaTab tab(String key, String label) {
            HomePlazaTab tab = new HomePlazaTab();
            tab.setPlazaKey(key);
            tab.setLabel(label);
            tab.setDescription(label + "描述");
            return tab;
        }

        private HomePlazaCategory category(String plaza, String name) {
            HomePlazaCategory category = new HomePlazaCategory();
            category.setPlazaKey(plaza);
            category.setCategoryName(name);
            return category;
        }
    }
}
