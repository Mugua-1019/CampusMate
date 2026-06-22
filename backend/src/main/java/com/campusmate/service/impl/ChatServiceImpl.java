package com.campusmate.service.impl;

import com.campusmate.domain.dto.ChatMessageRequest;
import com.campusmate.domain.entity.ChatConversation;
import com.campusmate.domain.entity.ChatMessage;
import com.campusmate.domain.entity.UserProfile;
import com.campusmate.domain.vo.ChatVO;
import com.campusmate.mapper.ChatMapper;
import com.campusmate.service.ChatService;
import com.campusmate.websocket.ChatWebSocketHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private static final Set<String> MESSAGE_TYPES = Set.of("text", "image", "file", "location");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ChatMapper chatMapper;
    private final ChatWebSocketHandler chatWebSocketHandler;

    public ChatServiceImpl(ChatMapper chatMapper, ChatWebSocketHandler chatWebSocketHandler) {
        this.chatMapper = chatMapper;
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @Override
    public ChatVO getChatHome(Long currentUserId) {
        requireUser(currentUserId);
        ChatVO vo = new ChatVO();
        vo.setConversations(chatMapper.selectConversations(currentUserId, false).stream()
                .map(this::completeConversation)
                .collect(Collectors.toList()));
        vo.setArchivedConversations(chatMapper.selectConversations(currentUserId, true).stream()
                .map(this::completeConversation)
                .collect(Collectors.toList()));
        return vo;
    }

    @Override
    public ChatVO.ConversationVO getPeerProfile(Long currentUserId, Long peerUserId, String sourceType, String sourceTitle) {
        requireUser(currentUserId);
        requireReceiver(peerUserId, currentUserId);
        UserProfile profile = chatMapper.selectUserProfile(peerUserId);
        ChatVO.ConversationVO vo = new ChatVO.ConversationVO();
        vo.setId(null);
        vo.setPeerUserId(peerUserId);
        vo.setName(profile.getNickname());
        vo.setAvatarUrl(profile.getAvatarUrl());
        vo.setAvatarText(buildAvatarText(profile.getNickname()));
        vo.setBadge(defaultText(sourceType, "校园伙伴"));
        vo.setPreview("发送第一条消息后开启会话");
        vo.setTime("");
        vo.setUnread(0);
        vo.setOnline(chatWebSocketHandler.isOnline(peerUserId));
        vo.setArchived(false);
        vo.setGender(profile.getGender());
        vo.setAge(21);
        vo.setSchool(defaultText(profile.getCollege(), "校园"));
        vo.setGrade(profile.getGrade());
        vo.setMajor(profile.getMajor());
        vo.setMatchDate("待发送");
        vo.setSource(defaultText(sourceTitle, "校园聊天"));
        vo.setTags(Collections.singletonList(defaultText(sourceType, "校园伙伴")));
        vo.setCommonTags(splitTextList(defaultText(profile.getInterestTags(), "")));
        return vo;
    }

    @Override
    public List<ChatVO.MessageVO> getMessages(Long currentUserId, Long conversationId) {
        requireUser(currentUserId);
        requireConversationMember(conversationId, currentUserId);
        chatMapper.markRead(conversationId, currentUserId);
        return chatMapper.selectMessages(conversationId).stream()
                .map(message -> toMessageVO(message, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatVO.MessageVO sendMessage(Long currentUserId, ChatMessageRequest request) {
        requireUser(currentUserId);
        if (request == null) {
            throw new IllegalArgumentException("message request is required");
        }

        String messageType = normalizeMessageType(request.getMessageType());
        Long conversationId = request.getConversationId();
        Long receiverUserId = request.getReceiverUserId();

        if (conversationId != null) {
            ChatConversation conversation = requireConversationMember(conversationId, currentUserId);
            receiverUserId = conversation.getStarterUserId().equals(currentUserId)
                    ? conversation.getTargetUserId()
                    : conversation.getStarterUserId();
        } else {
            receiverUserId = requireReceiver(receiverUserId, currentUserId);
            conversationId = ensureConversation(currentUserId, receiverUserId, request);
        }

        validateMessagePayload(messageType, request);

        ChatMessage message = new ChatMessage();
        message.setConversationId(conversationId);
        message.setSenderUserId(currentUserId);
        message.setReceiverUserId(receiverUserId);
        message.setMessageType(messageType);
        message.setContent(buildStoredContent(messageType, request));
        message.setAttachmentUrl(trimToNull(request.getAttachmentUrl()));
        message.setAttachmentName(trimToNull(request.getAttachmentName()));
        message.setLatitude(request.getLatitude());
        message.setLongitude(request.getLongitude());
        message.setSendStatus("sent");
        message.setCreatedAt(LocalDateTime.now());

        requireOne(chatMapper.insertMessage(message), "chat message insert");
        requireOne(chatMapper.updateConversationLastMessage(message), "chat conversation last message update");
        chatMapper.upsertConversationMember(conversationId, currentUserId);
        chatMapper.upsertConversationMember(conversationId, receiverUserId);
        chatMapper.markRead(conversationId, currentUserId);
        chatMapper.incrementUnread(conversationId, receiverUserId);

        ChatVO.MessageVO vo = toMessageVO(message, currentUserId);
        chatWebSocketHandler.pushMessage(receiverUserId, vo);
        return vo;
    }

    @Override
    public void updateArchiveStatus(Long currentUserId, Long conversationId, boolean archived) {
        requireUser(currentUserId);
        requireConversationMember(conversationId, currentUserId);
        requireOne(chatMapper.updateArchiveStatus(conversationId, currentUserId, archived), "chat archive update");
    }

    private Long ensureConversation(Long currentUserId, Long receiverUserId, ChatMessageRequest request) {
        Long existed = chatMapper.selectConversationBetweenUsers(currentUserId, receiverUserId);
        if (existed != null) {
            chatMapper.upsertConversationMember(existed, currentUserId);
            chatMapper.upsertConversationMember(existed, receiverUserId);
            return existed;
        }

        ChatConversation conversation = new ChatConversation();
        conversation.setStarterUserId(currentUserId);
        conversation.setTargetUserId(receiverUserId);
        conversation.setSourceType(limitText(defaultText(request.getSourceType(), "校园伙伴"), 30));
        conversation.setSourceId(request.getSourceId());
        conversation.setSourceTitle(limitText(defaultText(request.getSourceTitle(), "校园聊天"), 120));
        requireOne(chatMapper.insertConversation(conversation), "chat conversation insert");
        requireOne(chatMapper.insertConversationMember(conversation.getConversationId(), currentUserId), "chat sender member insert");
        requireOne(chatMapper.insertConversationMember(conversation.getConversationId(), receiverUserId), "chat receiver member insert");
        return conversation.getConversationId();
    }

    private ChatConversation requireConversationMember(Long conversationId, Long userId) {
        if (conversationId == null || conversationId <= 0) {
            throw new IllegalArgumentException("conversation id is required");
        }
        ChatConversation conversation = chatMapper.selectConversationById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("conversation not found");
        }
        if (chatMapper.countConversationMember(conversationId, userId) != 1) {
            throw new SecurityException("current user is not in this conversation");
        }
        return conversation;
    }

    private Long requireReceiver(Long receiverUserId, Long currentUserId) {
        if (receiverUserId == null || receiverUserId <= 0) {
            throw new IllegalArgumentException("receiver user id is required");
        }
        if (receiverUserId.equals(currentUserId)) {
            throw new IllegalArgumentException("cannot chat with yourself");
        }
        if (chatMapper.selectUserProfile(receiverUserId) == null) {
            throw new IllegalArgumentException("receiver user not found");
        }
        return receiverUserId;
    }

    private void requireUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new SecurityException("please login first");
        }
        if (chatMapper.selectUserProfile(userId) == null) {
            throw new SecurityException("current user profile not found");
        }
    }

    private String normalizeMessageType(String value) {
        String type = defaultText(value, "text");
        if (!MESSAGE_TYPES.contains(type)) {
            throw new IllegalArgumentException("message type is not supported");
        }
        return type;
    }

    private void validateMessagePayload(String messageType, ChatMessageRequest request) {
        if ("text".equals(messageType) && trimToNull(request.getContent()) == null) {
            throw new IllegalArgumentException("message content is required");
        }
        if (("image".equals(messageType) || "file".equals(messageType)) && trimToNull(request.getAttachmentUrl()) == null) {
            throw new IllegalArgumentException("attachment url is required");
        }
        if ("location".equals(messageType) && (request.getLatitude() == null || request.getLongitude() == null)) {
            throw new IllegalArgumentException("location latitude and longitude are required");
        }
    }

    private String buildStoredContent(String messageType, ChatMessageRequest request) {
        if ("image".equals(messageType)) {
            return limitText(defaultText(request.getContent(), "[图片]"), 1000);
        }
        if ("file".equals(messageType)) {
            return limitText(defaultText(request.getContent(), "[附件] " + defaultText(request.getAttachmentName(), "文件")), 1000);
        }
        if ("location".equals(messageType)) {
            return limitText(defaultText(request.getContent(), "[定位] " + request.getLatitude() + "," + request.getLongitude()), 1000);
        }
        return limitText(defaultText(request.getContent(), ""), 1000);
    }

    private ChatVO.ConversationVO completeConversation(ChatVO.ConversationVO conversation) {
        conversation.setOnline(chatWebSocketHandler.isOnline(conversation.getPeerUserId()));
        conversation.setAge(21);
        conversation.setTags(Collections.singletonList(defaultText(conversation.getBadge(), "校园伙伴")));
        conversation.setCommonTags(splitTextList(defaultText(conversation.getCommonTagsText(), "")));
        if (conversation.getAvatarText() == null && conversation.getName() != null) {
            conversation.setAvatarText(conversation.getName().substring(0, Math.min(1, conversation.getName().length())));
        }
        return conversation;
    }

    private ChatVO.MessageVO toMessageVO(ChatMessage message, Long currentUserId) {
        ChatVO.MessageVO vo = new ChatVO.MessageVO();
        vo.setId(message.getMessageId());
        vo.setConversationId(message.getConversationId());
        vo.setSenderUserId(message.getSenderUserId());
        vo.setReceiverUserId(message.getReceiverUserId());
        vo.setMine(message.getSenderUserId() != null && message.getSenderUserId().equals(currentUserId));
        vo.setMessageType(message.getMessageType());
        vo.setText(message.getContent());
        vo.setAttachmentUrl(message.getAttachmentUrl());
        vo.setAttachmentName(message.getAttachmentName());
        vo.setLatitude(message.getLatitude());
        vo.setLongitude(message.getLongitude());
        vo.setTime(message.getCreatedAt() == null ? "" : message.getCreatedAt().format(TIME_FORMATTER));
        vo.setCreatedAt(message.getCreatedAt() == null ? null : message.getCreatedAt().toString());
        return vo;
    }

    private List<String> splitTextList(String value) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(trimmed.split("[,，·]"))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .limit(4)
                .collect(Collectors.toList());
    }

    private String buildAvatarText(String name) {
        String trimmed = trimToNull(name);
        if (trimmed == null) {
            return "同";
        }
        return trimmed.substring(0, Math.min(1, trimmed.length()));
    }

    private String defaultText(String value, String fallback) {
        String trimmed = trimToNull(value);
        return trimmed == null ? fallback : trimmed;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String limitText(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private void requireOne(int affectedRows, String action) {
        if (affectedRows != 1) {
            throw new IllegalStateException(action + " affected " + affectedRows + " rows");
        }
    }
}
