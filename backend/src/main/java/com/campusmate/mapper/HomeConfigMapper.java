package com.campusmate.mapper;

import com.campusmate.domain.entity.HomePendingMeet;
import com.campusmate.domain.entity.HomePlazaCategory;
import com.campusmate.domain.entity.HomePlazaTab;
import com.campusmate.domain.entity.HomeUserStat;
import com.campusmate.domain.entity.HomeUserSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HomeConfigMapper {

    List<HomePlazaTab> selectTabs();

    List<HomePlazaCategory> selectCategories();

    HomeUserSummary selectUserSummary(@Param("userId") Long userId);

    List<HomeUserStat> selectUserStats(@Param("userId") Long userId);

    HomePendingMeet selectPendingMeet(@Param("userId") Long userId);
}
