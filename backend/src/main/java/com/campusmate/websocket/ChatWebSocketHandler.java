package com.campusmate.websocket;

import com.campusmate.domain.vo.ChatVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = resolveUserId(session.getUri());
        if (userId == null || userId <= 0) {
            session.close(CloseStatus.BAD_DATA.withReason("userId is required"));
            return;
        }
        session.getAttributes().put("userId", userId);
        sessions.put(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Object userId = session.getAttributes().get("userId");
        if (userId instanceof Long) {
            sessions.remove(userId);
        }
    }

    public boolean isOnline(Long userId) {
        WebSocketSession session = userId == null ? null : sessions.get(userId);
        return session != null && session.isOpen();
    }

    public void pushMessage(Long receiverUserId, ChatVO.MessageVO message) {
        WebSocketSession session = receiverUserId == null ? null : sessions.get(receiverUserId);
        if (session == null || !session.isOpen()) {
            return;
        }
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of(
                    "type", "chat.message",
                    "data", message
            ))));
        } catch (IOException error) {
            throw new IllegalStateException("chat websocket push failed", error);
        }
    }

    private Long resolveUserId(URI uri) {
        if (uri == null) {
            return null;
        }
        String value = UriComponentsBuilder.fromUri(uri).build().getQueryParams().getFirst("userId");
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException error) {
            return null;
        }
    }
}
