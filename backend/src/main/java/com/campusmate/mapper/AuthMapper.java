package com.campusmate.mapper;

import com.campusmate.domain.entity.UserAccount;
import com.campusmate.domain.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthMapper {

    UserAccount selectByLoginAccount(@Param("account") String account);

    UserAccount selectByUserId(@Param("userId") Long userId);

    int countByLoginAccount(@Param("account") String account);

    int insertAccount(UserAccount account);

    int insertProfile(UserProfile profile);

    int insertHomeUserSummary(@Param("userId") Long userId, @Param("nickname") String nickname);

    int updatePassword(@Param("userId") Long userId,
                       @Param("passwordSalt") String passwordSalt,
                       @Param("passwordHash") String passwordHash);
}
