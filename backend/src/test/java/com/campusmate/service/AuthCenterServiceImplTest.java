package com.campusmate.service;

import com.campusmate.domain.dto.AuthCenterCampusSubmitRequest;
import com.campusmate.domain.dto.AuthCenterPhoneCodeRequest;
import com.campusmate.domain.dto.AuthCenterPhoneSubmitRequest;
import com.campusmate.domain.entity.AuthCenterPhoneCode;
import com.campusmate.domain.entity.AuthCenterRecord;
import com.campusmate.domain.entity.UserProfile;
import com.campusmate.domain.vo.AuthCenterPhoneCodeVO;
import com.campusmate.domain.vo.AuthCenterVO;
import com.campusmate.mapper.AuthCenterMapper;
import com.campusmate.service.impl.AuthCenterServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthCenterServiceImplTest {

    @Test
    void getAuthCenterReadsSummaryConfigAndRecordsFromMapper() {
        FakeAuthCenterMapper mapper = new FakeAuthCenterMapper();
        mapper.profile.setVerified(true);
        mapper.profile.setVerifyStatus("approved");
        mapper.records.add(campusRecord());
        AuthCenterService service = new AuthCenterServiceImpl(mapper);

        AuthCenterVO vo = service.getAuthCenter(1L);

        assertEquals("校园认证用户", vo.getSummary().getLevelTitle());
        assertEquals("发布需求", vo.getBenefits().get(0).getLabel());
        assertEquals("学生证", vo.getSamples().get(0).getLabel());
        assertEquals("提升可信度", vo.getRights().get(0).getTitle());
        assertEquals("华东师范大学 · 学号：20240001", vo.getRecords().get(0).getContent());
    }

    @Test
    void submitCampusCreatesPendingRecordFromCurrentFormFields() {
        FakeAuthCenterMapper mapper = new FakeAuthCenterMapper();
        AuthCenterService service = new AuthCenterServiceImpl(mapper);

        AuthCenterVO.RecordVO record = service.submitCampus(campusRequest());

        assertEquals("审核中", record.getStatus());
        assertEquals("pending", mapper.insertedRecord.getStatus());
        assertEquals("本科生", mapper.insertedRecord.getIdentityType());
    }

    @Test
    void submitCampusRejectsDuplicatePendingCampusRecord() {
        FakeAuthCenterMapper mapper = new FakeAuthCenterMapper();
        mapper.pendingCampusCount = 1;
        AuthCenterService service = new AuthCenterServiceImpl(mapper);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.submitCampus(campusRequest())
        );

        assertEquals("campus authentication is already pending", exception.getMessage());
    }

    @Test
    void phoneAuthenticationUsesMockCodeUpdatesPhoneAndWritesPassedRecord() {
        FakeAuthCenterMapper mapper = new FakeAuthCenterMapper();
        AuthCenterService service = new AuthCenterServiceImpl(mapper);
        AuthCenterPhoneCodeRequest codeRequest = new AuthCenterPhoneCodeRequest();
        codeRequest.setUserId(1L);
        codeRequest.setPhone("13812345678");

        AuthCenterPhoneCodeVO codeVO = service.sendPhoneCode(codeRequest);

        assertEquals("123456", codeVO.getCode());
        AuthCenterPhoneSubmitRequest submitRequest = new AuthCenterPhoneSubmitRequest();
        submitRequest.setUserId(1L);
        submitRequest.setPhone("13812345678");
        submitRequest.setCode("123456");
        AuthCenterVO.RecordVO record = service.submitPhone(submitRequest);

        assertEquals("已通过", record.getStatus());
        assertEquals("13812345678", mapper.updatedPhone);
        assertEquals(true, mapper.phoneCodeUsed);
        assertEquals("phone", mapper.insertedRecord.getAuthType());
    }

    @Test
    void phoneAuthenticationRejectsWrongCode() {
        FakeAuthCenterMapper mapper = new FakeAuthCenterMapper();
        mapper.phoneCode = phoneCode("13812345678");
        AuthCenterService service = new AuthCenterServiceImpl(mapper);
        AuthCenterPhoneSubmitRequest request = new AuthCenterPhoneSubmitRequest();
        request.setUserId(1L);
        request.setPhone("13812345678");
        request.setCode("000000");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.submitPhone(request));

        assertEquals("phone code is invalid or expired", exception.getMessage());
    }

    private AuthCenterCampusSubmitRequest campusRequest() {
        AuthCenterCampusSubmitRequest request = new AuthCenterCampusSubmitRequest();
        request.setUserId(1L);
        request.setSchool("华东师范大学");
        request.setStudentNo("20240001");
        request.setRealName("张三");
        request.setIdentityType("本科生");
        request.setMaterialName("学生证");
        request.setMaterialUrl("/uploads/auth-materials/demo.jpg");
        return request;
    }

    private AuthCenterRecord campusRecord() {
        AuthCenterRecord record = new AuthCenterRecord();
        record.setRecordId(1L);
        record.setUserId(1L);
        record.setAuthType("campus");
        record.setSchool("华东师范大学");
        record.setStudentNo("20240001");
        record.setStatus("pending");
        record.setFeedback("预计1-3个工作日内完成审核");
        record.setActionText("-");
        record.setSubmittedAt(LocalDateTime.of(2026, 6, 18, 10, 0));
        return record;
    }

    private AuthCenterPhoneCode phoneCode(String phone) {
        AuthCenterPhoneCode code = new AuthCenterPhoneCode();
        code.setCodeId(1L);
        code.setUserId(1L);
        code.setPhone(phone);
        code.setCode("123456");
        code.setUsed(false);
        code.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        return code;
    }

    private static class FakeAuthCenterMapper implements AuthCenterMapper {
        private final UserProfile profile = new UserProfile();
        private final List<AuthCenterRecord> records = new ArrayList<>();
        private int pendingCampusCount;
        private AuthCenterRecord insertedRecord;
        private AuthCenterPhoneCode phoneCode;
        private String updatedPhone;
        private boolean phoneCodeUsed;

        private FakeAuthCenterMapper() {
            profile.setUserId(1L);
            profile.setNickname("小星同学");
            profile.setVerified(false);
            profile.setVerifyStatus("unverified");
        }

        @Override
        public UserProfile selectProfile(Long userId) {
            return profile;
        }

        @Override
        public int countPendingCampusRecord(Long userId) {
            return pendingCampusCount;
        }

        @Override
        public int insertRecord(AuthCenterRecord record) {
            record.setRecordId(2L);
            insertedRecord = record;
            records.add(record);
            return 1;
        }

        @Override
        public List<AuthCenterRecord> selectRecords(Long userId) {
            return records;
        }

        @Override
        public List<AuthCenterVO.BenefitVO> selectBenefits() {
            AuthCenterVO.BenefitVO benefit = new AuthCenterVO.BenefitVO();
            benefit.setLabel("发布需求");
            benefit.setIcon("Document");
            return List.of(benefit);
        }

        @Override
        public List<AuthCenterVO.SampleVO> selectSamples() {
            AuthCenterVO.SampleVO sample = new AuthCenterVO.SampleVO();
            sample.setLabel("学生证");
            sample.setTone("sample-id");
            return List.of(sample);
        }

        @Override
        public List<AuthCenterVO.RightVO> selectRights() {
            AuthCenterVO.RightVO right = new AuthCenterVO.RightVO();
            right.setTitle("提升可信度");
            right.setDescription("认证标识展示");
            right.setIcon("Lock");
            return List.of(right);
        }

        @Override
        public int insertPhoneCode(AuthCenterPhoneCode code) {
            code.setCodeId(1L);
            phoneCode = code;
            return 1;
        }

        @Override
        public AuthCenterPhoneCode selectLatestPhoneCode(Long userId, String phone, LocalDateTime now) {
            return phoneCode;
        }

        @Override
        public int markPhoneCodeUsed(Long codeId) {
            phoneCodeUsed = true;
            return 1;
        }

        @Override
        public int updateAccountPhone(Long userId, String phone) {
            updatedPhone = phone;
            return 1;
        }
    }
}
