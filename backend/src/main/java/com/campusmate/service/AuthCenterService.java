package com.campusmate.service;

import com.campusmate.domain.dto.AuthCenterCampusSubmitRequest;
import com.campusmate.domain.dto.AuthCenterPhoneCodeRequest;
import com.campusmate.domain.dto.AuthCenterPhoneSubmitRequest;
import com.campusmate.domain.vo.AuthCenterPhoneCodeVO;
import com.campusmate.domain.vo.AuthCenterVO;

import java.util.List;

public interface AuthCenterService {

    AuthCenterVO getAuthCenter(Long userId);

    List<AuthCenterVO.RecordVO> listRecords(Long userId);

    AuthCenterVO.RecordVO submitCampus(AuthCenterCampusSubmitRequest request);

    AuthCenterPhoneCodeVO sendPhoneCode(AuthCenterPhoneCodeRequest request);

    AuthCenterVO.RecordVO submitPhone(AuthCenterPhoneSubmitRequest request);
}
