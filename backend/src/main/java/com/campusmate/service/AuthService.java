package com.campusmate.service;

import com.campusmate.domain.dto.AuthLoginRequest;
import com.campusmate.domain.dto.AuthRegisterRequest;
import com.campusmate.domain.dto.AuthResetPasswordRequest;
import com.campusmate.domain.vo.AuthUserVO;

public interface AuthService {

    AuthUserVO login(AuthLoginRequest request);

    AuthUserVO register(AuthRegisterRequest request);

    void resetPassword(AuthResetPasswordRequest request);
}
