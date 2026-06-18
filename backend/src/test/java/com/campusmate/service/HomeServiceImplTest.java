package com.campusmate.service;

import com.campusmate.domain.dto.HomePlazaQuery;
import com.campusmate.domain.entity.HomePendingMeet;
import com.campusmate.domain.entity.HomePlazaCategory;
import com.campusmate.domain.entity.HomePlazaTab;
import com.campusmate.domain.entity.HomePost;
import com.campusmate.domain.entity.HomeUserStat;
import com.campusmate.domain.entity.HomeUserSummary;
import com.campusmate.domain.vo.HomePlazaVO;
import com.campusmate.mapper.HomeConfigMapper;
import com.campusmate.mapper.HomePostMapper;
import com.campusmate.service.impl.HomeServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    private static class CapturingHomePostMapper implements HomePostMapper {
        private final List<HomePost> posts;
        private HomePlazaQuery capturedQuery;

        private CapturingHomePostMapper(List<HomePost> posts) {
            this.posts = posts;
        }

        @Override
        public List<HomePost> selectVisiblePosts(HomePlazaQuery query) {
            this.capturedQuery = query;
            return posts;
        }
    }

    private static class FakeHomeConfigMapper implements HomeConfigMapper {

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
            summary.setAvatarUrl("/avatars/default.png");
            summary.setVerified(false);
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
