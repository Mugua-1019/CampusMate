package com.campusmate.mapper;

import com.campusmate.domain.dto.HomePlazaQuery;
import com.campusmate.domain.entity.HomePost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HomePostMapper {

    List<HomePost> selectVisiblePosts(@Param("query") HomePlazaQuery query);
}
