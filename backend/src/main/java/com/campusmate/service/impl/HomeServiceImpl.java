package com.campusmate.service.impl;

import com.campusmate.domain.dto.HomePlazaQuery;
import com.campusmate.domain.entity.HomePendingMeet;
import com.campusmate.domain.entity.HomePlazaCategory;
import com.campusmate.domain.entity.HomePlazaTab;
import com.campusmate.domain.entity.HomePost;
import com.campusmate.domain.entity.HomeUserSummary;
import com.campusmate.domain.vo.HomePlazaVO;
import com.campusmate.mapper.HomeConfigMapper;
import com.campusmate.mapper.HomePostMapper;
import com.campusmate.service.HomeService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HomeServiceImpl implements HomeService {

    private static final String ALL_CATEGORY = "全部";
    private static final String MATCH_PLAZA = "match";

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
        summary.setVerified(userSummary != null && Boolean.TRUE.equals(userSummary.getVerified()));
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
        vo.setAvatarText(post.getAvatarText());
        vo.setAnonymous(Boolean.TRUE.equals(post.getAnonymous()));
        vo.setVerified(Boolean.TRUE.equals(post.getVerified()));
        vo.setCurrentCount(currentCount);
        vo.setMaxCount(maxCount);
        vo.setFull(currentCount >= maxCount || "full".equals(post.getStatus()));
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

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }
}
