package com.campusmate.controller;

import com.campusmate.common.result.Result;
import com.campusmate.domain.dto.AuthCenterCampusSubmitRequest;
import com.campusmate.domain.dto.AuthCenterPhoneCodeRequest;
import com.campusmate.domain.dto.AuthCenterPhoneSubmitRequest;
import com.campusmate.domain.vo.AuthCenterPhoneCodeVO;
import com.campusmate.domain.vo.AuthCenterVO;
import com.campusmate.service.AuthCenterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth-center")
public class AuthCenterController {

    private static final long MAX_MATERIAL_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_MATERIAL_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "application/pdf"
    );

    private final AuthCenterService authCenterService;
    private final Path uploadRoot;

    public AuthCenterController(AuthCenterService authCenterService,
                                @Value("${campusmate.upload-root:uploads}") String uploadRoot) {
        this.authCenterService = authCenterService;
        this.uploadRoot = Path.of(uploadRoot);
    }

    @GetMapping
    public Result<AuthCenterVO> getAuthCenter(@RequestParam(required = false) Long userId,
                                              @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId) {
        return Result.ok(authCenterService.getAuthCenter(resolveUserId(userId, currentUserId)));
    }

    @GetMapping("/records")
    public Result<List<AuthCenterVO.RecordVO>> listRecords(
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        return Result.ok(authCenterService.listRecords(resolveUserId(userId, currentUserId)));
    }

    @PostMapping("/campus")
    public Result<AuthCenterVO.RecordVO> submitCampus(
            @RequestBody AuthCenterCampusSubmitRequest request,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        request.setUserId(resolveUserId(request.getUserId(), currentUserId));
        return Result.ok(authCenterService.submitCampus(request));
    }

    @PostMapping("/material")
    public Result<Map<String, String>> uploadMaterial(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("material file is required");
        }
        if (file.getSize() > MAX_MATERIAL_SIZE) {
            throw new IllegalArgumentException("material file must not exceed 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MATERIAL_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("material file type is not supported");
        }
        String extension = resolveMaterialExtension(file.getOriginalFilename(), contentType);
        Path materialDir = uploadRoot.resolve("auth-materials").normalize();
        Files.createDirectories(materialDir);
        String filename = UUID.randomUUID() + extension;
        Path target = materialDir.resolve(filename).normalize();
        if (!target.startsWith(materialDir)) {
            throw new IllegalArgumentException("material filename is invalid");
        }
        file.transferTo(target);
        return Result.ok(Map.of(
                "materialName", file.getOriginalFilename() == null ? filename : file.getOriginalFilename(),
                "materialUrl", "/uploads/auth-materials/" + filename
        ));
    }

    @PostMapping("/phone/code")
    public Result<AuthCenterPhoneCodeVO> sendPhoneCode(
            @RequestBody AuthCenterPhoneCodeRequest request,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        request.setUserId(resolveUserId(request.getUserId(), currentUserId));
        return Result.ok(authCenterService.sendPhoneCode(request));
    }

    @PostMapping("/phone")
    public Result<AuthCenterVO.RecordVO> submitPhone(
            @RequestBody AuthCenterPhoneSubmitRequest request,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        request.setUserId(resolveUserId(request.getUserId(), currentUserId));
        return Result.ok(authCenterService.submitPhone(request));
    }

    private Long resolveUserId(Long requestUserId, Long currentUserId) {
        return currentUserId == null ? requestUserId : currentUserId;
    }

    private String resolveMaterialExtension(String originalFilename, String contentType) {
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < originalFilename.length() - 1) {
                String extension = originalFilename.substring(dotIndex).toLowerCase();
                if (extension.matches("\\.(jpg|jpeg|png|pdf)")) {
                    return extension;
                }
            }
        }
        if ("image/png".equals(contentType)) {
            return ".png";
        }
        if ("application/pdf".equals(contentType)) {
            return ".pdf";
        }
        return ".jpg";
    }
}
