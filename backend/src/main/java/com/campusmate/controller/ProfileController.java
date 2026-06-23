package com.campusmate.controller;

import com.campusmate.common.result.Result;
import com.campusmate.domain.dto.ProfilePreferenceUpdateRequest;
import com.campusmate.domain.dto.ProfileUpdateRequest;
import com.campusmate.domain.vo.ProfileVO;
import com.campusmate.service.ProfileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private static final Set<String> ALLOWED_AVATAR_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private final ProfileService profileService;
    private final Path uploadRoot;

    public ProfileController(ProfileService profileService,
                             @Value("${campusmate.upload-root:uploads}") String uploadRoot) {
        this.profileService = profileService;
        this.uploadRoot = Path.of(uploadRoot);
    }

    @GetMapping
    public Result<ProfileVO> getProfile(@RequestParam(required = false) Long userId) {
        return Result.ok(profileService.getProfile(userId));
    }

    @PutMapping
    public Result<ProfileVO> updateProfile(@RequestBody ProfileUpdateRequest request) {
        return Result.ok(profileService.updateProfile(request));
    }

    @PutMapping("/preferences")
    public Result<ProfileVO> updatePreferences(@RequestBody ProfilePreferenceUpdateRequest request) {
        return Result.ok(profileService.updatePreferences(request));
    }

    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("avatar file is required");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_AVATAR_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("avatar file type is not supported");
        }
        String extension = resolveExtension(file.getOriginalFilename(), contentType);
        Path avatarDir = uploadRoot.resolve("avatars").normalize();
        Files.createDirectories(avatarDir);
        String filename = UUID.randomUUID() + extension;
        Path target = avatarDir.resolve(filename).normalize();
        if (!target.startsWith(avatarDir)) {
            throw new IllegalArgumentException("avatar filename is invalid");
        }
        file.transferTo(target);
        return Result.ok(Map.of("avatarUrl", "/uploads/avatars/" + filename));
    }

    private String resolveExtension(String originalFilename, String contentType) {
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < originalFilename.length() - 1) {
                String extension = originalFilename.substring(dotIndex).toLowerCase();
                if (extension.matches("\\.(jpg|jpeg|png|webp|gif)")) {
                    return extension;
                }
            }
        }
        if ("image/png".equals(contentType)) {
            return ".png";
        }
        if ("image/webp".equals(contentType)) {
            return ".webp";
        }
        if ("image/gif".equals(contentType)) {
            return ".gif";
        }
        return ".jpg";
    }
}
