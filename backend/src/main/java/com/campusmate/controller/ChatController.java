package com.campusmate.controller;

import com.campusmate.common.result.Result;
import com.campusmate.domain.dto.ChatArchiveRequest;
import com.campusmate.domain.dto.ChatMessageRequest;
import com.campusmate.domain.vo.ChatVO;
import com.campusmate.service.ChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private static final Set<String> ALLOWED_FILE_TYPES = Set.of(
            "application/pdf",
            "application/zip",
            "application/x-zip-compressed",
            "text/plain",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"
    );

    private final ChatService chatService;
    private final Path uploadRoot;

    public ChatController(ChatService chatService,
                          @Value("${campusmate.upload-root:uploads}") String uploadRoot) {
        this.chatService = chatService;
        this.uploadRoot = Path.of(uploadRoot);
    }

    @GetMapping
    public Result<ChatVO> getChatHome(@RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId) {
        return Result.ok(chatService.getChatHome(currentUserId));
    }

    @GetMapping("/users/{peerUserId}")
    public Result<ChatVO.ConversationVO> getPeerProfile(
            @PathVariable Long peerUserId,
            @RequestParam(required = false) String sourceType,
            @RequestParam(required = false) String sourceTitle,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        return Result.ok(chatService.getPeerProfile(currentUserId, peerUserId, sourceType, sourceTitle));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public Result<java.util.List<ChatVO.MessageVO>> getMessages(
            @PathVariable Long conversationId,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        return Result.ok(chatService.getMessages(currentUserId, conversationId));
    }

    @PostMapping("/messages")
    public Result<ChatVO.MessageVO> sendMessage(
            @RequestBody ChatMessageRequest request,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        return Result.ok(chatService.sendMessage(currentUserId, request));
    }

    @PatchMapping("/conversations/{conversationId}/archive")
    public Result<Void> updateArchiveStatus(
            @PathVariable Long conversationId,
            @RequestBody ChatArchiveRequest request,
            @RequestHeader(value = "X-CampusMate-User-Id", required = false) Long currentUserId
    ) {
        chatService.updateArchiveStatus(currentUserId, conversationId, request != null && Boolean.TRUE.equals(request.getArchived()));
        return Result.ok(null);
    }

    @PostMapping("/attachments")
    public Result<Map<String, String>> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type
    ) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("attachment file is required");
        }
        String normalizedType = "image".equals(type) ? "image" : "file";
        String contentType = file.getContentType();
        if ("image".equals(normalizedType) && (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType))) {
            throw new IllegalArgumentException("image file type is not supported");
        }
        if ("file".equals(normalizedType) && contentType != null && !ALLOWED_FILE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("attachment file type is not supported");
        }

        Path attachmentDir = uploadRoot.resolve("chat-" + normalizedType + "s").normalize();
        Files.createDirectories(attachmentDir);
        String originalName = file.getOriginalFilename() == null ? "attachment" : file.getOriginalFilename();
        String extension = resolveExtension(originalName, contentType, normalizedType);
        String filename = UUID.randomUUID() + extension;
        Path target = attachmentDir.resolve(filename).normalize();
        if (!target.startsWith(attachmentDir)) {
            throw new IllegalArgumentException("attachment filename is invalid");
        }
        file.transferTo(target);
        return Result.ok(Map.of(
                "url", "/uploads/chat-" + normalizedType + "s/" + filename,
                "name", originalName,
                "type", normalizedType
        ));
    }

    private String resolveExtension(String originalName, String contentType, String type) {
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < originalName.length() - 1) {
            String extension = originalName.substring(dotIndex).toLowerCase();
            if (extension.matches("\\.[a-z0-9]{1,12}")) {
                return extension;
            }
        }
        if ("image".equals(type)) {
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
        return ".bin";
    }
}
