package com.campusmate.mapper;

import com.campusmate.domain.entity.AuthCenterPhoneCode;
import com.campusmate.domain.entity.AuthCenterRecord;
import com.campusmate.domain.entity.UserProfile;
import com.campusmate.domain.vo.AuthCenterVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AuthCenterMapper {

    UserProfile selectProfile(@Param("userId") Long userId);

    int countPendingCampusRecord(@Param("userId") Long userId);

    int insertRecord(AuthCenterRecord record);

    List<AuthCenterRecord> selectRecords(@Param("userId") Long userId);

    List<AuthCenterVO.BenefitVO> selectBenefits();

    List<AuthCenterVO.SampleVO> selectSamples();

    List<AuthCenterVO.RightVO> selectRights();

    int insertPhoneCode(AuthCenterPhoneCode code);

    AuthCenterPhoneCode selectLatestPhoneCode(@Param("userId") Long userId,
                                              @Param("phone") String phone,
                                              @Param("now") LocalDateTime now);

    int markPhoneCodeUsed(@Param("codeId") Long codeId);

    int updateAccountPhone(@Param("userId") Long userId, @Param("phone") String phone);
}
