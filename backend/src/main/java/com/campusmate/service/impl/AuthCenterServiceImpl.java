package com.campusmate.service.impl;

import com.campusmate.domain.dto.AuthCenterCampusSubmitRequest;
import com.campusmate.domain.dto.AuthCenterPhoneCodeRequest;
import com.campusmate.domain.dto.AuthCenterPhoneSubmitRequest;
import com.campusmate.domain.entity.AuthCenterPhoneCode;
import com.campusmate.domain.entity.AuthCenterRecord;
import com.campusmate.domain.entity.UserProfile;
import com.campusmate.domain.vo.AuthCenterPhoneCodeVO;
import com.campusmate.domain.vo.AuthCenterVO;
import com.campusmate.mapper.AuthCenterMapper;
import com.campusmate.service.AuthCenterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AuthCenterServiceImpl implements AuthCenterService {

    private static final String TYPE_CAMPUS = "campus";
    private static final String TYPE_PHONE = "phone";
    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_PASSED = "passed";
    private static final String DEMO_PHONE_CODE = "123456";
    private static final int PHONE_CODE_EXPIRES_MINUTES = 5;
    private static final DateTimeFormatter RECORD_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AuthCenterMapper authCenterMapper;

    public AuthCenterServiceImpl(AuthCenterMapper authCenterMapper) {
        this.authCenterMapper = authCenterMapper;
    }

    @Override
    public AuthCenterVO getAuthCenter(Long userId) {
        Long validUserId = requireUserId(userId);
        AuthCenterVO vo = new AuthCenterVO();
        vo.setSummary(toSummary(requireProfile(validUserId)));
        vo.setBenefits(authCenterMapper.selectBenefits());
        vo.setSamples(authCenterMapper.selectSamples());
        vo.setRights(authCenterMapper.selectRights());
        vo.setRecords(listRecords(validUserId));
        return vo;
    }

    @Override
    public List<AuthCenterVO.RecordVO> listRecords(Long userId) {
        return authCenterMapper.selectRecords(requireUserId(userId)).stream()
                .map(this::toRecordVO)
                .toList();
    }

    @Override
    @Transactional
    public AuthCenterVO.RecordVO submitCampus(AuthCenterCampusSubmitRequest request) {
        Long userId = requireUserId(request == null ? null : request.getUserId());
        requireProfile(userId);
        if (authCenterMapper.countPendingCampusRecord(userId) > 0) {
            throw new IllegalArgumentException("campus authentication is already pending");
        }

        AuthCenterRecord record = new AuthCenterRecord();
        record.setUserId(userId);
        record.setAuthType(TYPE_CAMPUS);
        record.setSchool(requiredText(request.getSchool(), "school"));
        record.setStudentNo(requiredText(request.getStudentNo(), "studentNo"));
        record.setRealName(requiredText(request.getRealName(), "realName"));
        record.setIdentityType(requiredText(request.getIdentityType(), "identityType"));
        record.setMaterialName(requiredText(request.getMaterialName(), "materialName"));
        record.setMaterialUrl(requiredText(request.getMaterialUrl(), "materialUrl"));
        record.setStatus(STATUS_PENDING);
        record.setVerified(false);
        record.setFeedback("预计1-3个工作日内完成审核");
        record.setActionText("-");
        record.setSubmittedAt(LocalDateTime.now());
        requireOne(authCenterMapper.insertRecord(record), "campus auth record insert");
        return toRecordVO(record);
    }

    @Override
    @Transactional
    public AuthCenterPhoneCodeVO sendPhoneCode(AuthCenterPhoneCodeRequest request) {
        Long userId = requireUserId(request == null ? null : request.getUserId());
        requireProfile(userId);
        String phone = validPhone(request.getPhone());

        AuthCenterPhoneCode code = new AuthCenterPhoneCode();
        code.setUserId(userId);
        code.setPhone(phone);
        code.setCode(DEMO_PHONE_CODE);
        code.setUsed(false);
        code.setExpiresAt(LocalDateTime.now().plusMinutes(PHONE_CODE_EXPIRES_MINUTES));
        requireOne(authCenterMapper.insertPhoneCode(code), "phone code insert");

        AuthCenterPhoneCodeVO vo = new AuthCenterPhoneCodeVO();
        vo.setPhone(phone);
        vo.setCode(DEMO_PHONE_CODE);
        vo.setExpiresInSeconds(PHONE_CODE_EXPIRES_MINUTES * 60);
        return vo;
    }

    @Override
    @Transactional
    public AuthCenterVO.RecordVO submitPhone(AuthCenterPhoneSubmitRequest request) {
        Long userId = requireUserId(request == null ? null : request.getUserId());
        requireProfile(userId);
        String phone = validPhone(request.getPhone());
        String codeText = requiredText(request.getCode(), "code");
        AuthCenterPhoneCode code = authCenterMapper.selectLatestPhoneCode(userId, phone, LocalDateTime.now());
        if (code == null || !codeText.equals(code.getCode())) {
            throw new IllegalArgumentException("phone code is invalid or expired");
        }

        requireOne(authCenterMapper.markPhoneCodeUsed(code.getCodeId()), "phone code used update");
        requireOne(authCenterMapper.updateAccountPhone(userId, phone), "account phone update");

        AuthCenterRecord record = new AuthCenterRecord();
        record.setUserId(userId);
        record.setAuthType(TYPE_PHONE);
        record.setPhone(phone);
        record.setStatus(STATUS_PASSED);
        record.setVerified(true);
        record.setFeedback("手机号认证成功");
        record.setActionText("查看详情");
        record.setSubmittedAt(LocalDateTime.now());
        requireOne(authCenterMapper.insertRecord(record), "phone auth record insert");
        return toRecordVO(record);
    }

    private AuthCenterVO.SummaryVO toSummary(UserProfile profile) {
        AuthCenterVO.SummaryVO vo = new AuthCenterVO.SummaryVO();
        vo.setUserId(profile.getUserId());
        vo.setNickname(profile.getNickname());
        vo.setAvatarUrl(profile.getAvatarUrl());
        vo.setAvatarText(avatarText(profile.getNickname()));
        vo.setVerified(Boolean.TRUE.equals(profile.getVerified()));
        vo.setVerifyStatus(profile.getVerifyStatus());
        vo.setStatusText(Boolean.TRUE.equals(profile.getVerified()) ? "已认证" : "待认证");
        vo.setLevelTitle(Boolean.TRUE.equals(profile.getVerified()) ? "校园认证用户" : "未认证");
        vo.setLevelHint(Boolean.TRUE.equals(profile.getVerified()) ? "你已解锁发布、聊天和匹配功能" : "完成认证后可升级为校园认证用户");
        return vo;
    }

    private AuthCenterVO.RecordVO toRecordVO(AuthCenterRecord record) {
        AuthCenterVO.RecordVO vo = new AuthCenterVO.RecordVO();
        vo.setRecordId(record.getRecordId());
        vo.setTime(record.getSubmittedAt() == null ? null : RECORD_TIME_FORMAT.format(record.getSubmittedAt()));
        vo.setType(TYPE_PHONE.equals(record.getAuthType()) ? "手机号认证" : "校园认证");
        vo.setContent(TYPE_PHONE.equals(record.getAuthType()) ? maskPhone(record.getPhone()) : campusContent(record));
        vo.setStatus(statusText(record.getStatus()));
        vo.setStatusClass(record.getStatus());
        vo.setFeedback(record.getFeedback());
        vo.setAction(record.getActionText());
        return vo;
    }

    private String campusContent(AuthCenterRecord record) {
        return record.getSchool() + " · 学号：" + record.getStudentNo();
    }

    private String statusText(String status) {
        if (STATUS_PASSED.equals(status)) {
            return "已通过";
        }
        if (STATUS_PENDING.equals(status)) {
            return "审核中";
        }
        return "已驳回";
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    private String avatarText(String nickname) {
        String trimmed = trimToNull(nickname);
        return trimmed == null ? "星" : trimmed.substring(0, 1);
    }

    private UserProfile requireProfile(Long userId) {
        UserProfile profile = authCenterMapper.selectProfile(userId);
        if (profile == null) {
            throw new IllegalArgumentException("user not found");
        }
        return profile;
    }

    private Long requireUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("userId is required");
        }
        return userId;
    }

    private String validPhone(String value) {
        String phone = requiredText(value, "phone");
        if (!phone.matches("\\d{11}")) {
            throw new IllegalArgumentException("phone must be 11 digits");
        }
        return phone;
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

    private void requireOne(int affectedRows, String action) {
        if (affectedRows != 1) {
            throw new IllegalStateException(action + " affected " + affectedRows + " rows");
        }
    }
}
