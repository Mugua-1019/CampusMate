package com.campusmate.service;

import com.campusmate.domain.dto.ChatMessageRequest;
import com.campusmate.domain.vo.ChatVO;

import java.util.List;

public interface ChatService {

    ChatVO getChatHome(Long currentUserId);

    ChatVO.ConversationVO getPeerProfile(Long currentUserId, Long peerUserId, String sourceType, String sourceTitle);

    List<ChatVO.MessageVO> getMessages(Long currentUserId, Long conversationId);

    ChatVO.MessageVO sendMessage(Long currentUserId, ChatMessageRequest request);

    void updateArchiveStatus(Long currentUserId, Long conversationId, boolean archived);
}
