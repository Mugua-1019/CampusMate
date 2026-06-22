package com.campusmate.controller;

import com.campusmate.common.result.Result;
import com.campusmate.domain.dto.AuthChangePasswordRequest;
import com.campusmate.domain.dto.AuthLoginRequest;
import com.campusmate.domain.dto.AuthRegisterRequest;
import com.campusmate.domain.dto.AuthResetPasswordRequest;
import com.campusmate.domain.vo.AuthUserVO;
import com.campusmate.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<AuthUserVO> login(@RequestBody AuthLoginRequest request) {
        return Result.ok(authService.login(request));
    }

    @PostMapping("/register")
    public Result<AuthUserVO> register(@RequestBody AuthRegisterRequest request) {
        return Result.ok(authService.register(request));
    }

    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody AuthResetPasswordRequest request) {
        authService.resetPassword(request);
        return Result.ok(null);
    }

    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestBody AuthChangePasswordRequest request,
                                       @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId) {
        if (request == null) {
            request = new AuthChangePasswordRequest();
        }
        request.setUserId(currentUserId);
        authService.changePassword(request);
        return Result.ok(null);
    }
}
