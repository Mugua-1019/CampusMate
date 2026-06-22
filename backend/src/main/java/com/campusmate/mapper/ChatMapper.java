package com.campusmate.mapper;

import com.campusmate.domain.entity.ChatConversation;
import com.campusmate.domain.entity.ChatMessage;
import com.campusmate.domain.entity.UserProfile;
import com.campusmate.domain.vo.ChatVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMapper {

    UserProfile selectUserProfile(@Param("userId") Long userId);

    ChatConversation selectConversationById(@Param("conversationId") Long conversationId);

    Long selectConversationBetweenUsers(@Param("userA") Long userA, @Param("userB") Long userB);

    int insertConversation(ChatConversation conversation);

    int insertConversationMember(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    int upsertConversationMember(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    int countConversationMember(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    List<ChatVO.ConversationVO> selectConversations(@Param("userId") Long userId, @Param("archived") Boolean archived);

    List<ChatMessage> selectMessages(@Param("conversationId") Long conversationId);

    int insertMessage(ChatMessage message);

    int updateConversationLastMessage(ChatMessage message);

    int incrementUnread(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    int markRead(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    int updateArchiveStatus(@Param("conversationId") Long conversationId,
                            @Param("userId") Long userId,
                            @Param("archived") boolean archived);
}
