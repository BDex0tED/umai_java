package com.sayra.umai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sayra.umai.exception.ResourceNotFoundException;
import com.sayra.umai.model.entity.user.UserEntity; // Импортируем UserEntity
import com.sayra.umai.model.entity.work.ChatSession;
import com.sayra.umai.model.entity.work.Message;
import com.sayra.umai.model.entity.work.SenderEnum;
import com.sayra.umai.model.request.AIAskRequest;
import com.sayra.umai.repo.ChatSessionRepo;
import com.sayra.umai.repo.MessageRepo;
import com.sayra.umai.repo.UserEntityRepo; // Подключим репозиторий юзеров напрямую
import com.sayra.umai.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebsocketHandler extends TextWebSocketHandler {

    private final AiService aiService;
    private final ChatSessionRepo chatSessionRepo;
    private final MessageRepo messageRepo;
    private final ObjectMapper objectMapper;
    private final UserEntityRepo userRepo;

    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        activeSessions.put(session.getId(), session);
        log.info("New Websocket connection: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        Principal principal = session.getPrincipal();
        if (principal == null) {
            log.error("Unauthorized request to WebSocket");
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        UserEntity currentUser = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String payload = message.getPayload();
        AIAskRequest request = objectMapper.readValue(payload, AIAskRequest.class);

        saveMessageToDb(request.sessionId(), request.query(), SenderEnum.USER, currentUser);

        aiService.askAiAsync(request).thenAccept(aiResponseText -> {
            try {
                saveMessageToDb(request.sessionId(), aiResponseText, SenderEnum.AI, currentUser);
                if (session.isOpen()) {
                    String jsonResponse = "{\"text\": \"" + aiResponseText + "\", \"sender\": \"AI\"}";
                    session.sendMessage(new TextMessage(jsonResponse));
                }
            } catch (Exception e) {
                log.error("Error while sending request to AI", e);
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        activeSessions.remove(session.getId());
        log.info("Websocket connection closed {}", session.getId());
    }

    private void saveMessageToDb(Long sessionId, String content, SenderEnum sender, UserEntity user) {
        ChatSession chatS = chatSessionRepo.findByIdAndUser(sessionId, user)
                .orElseThrow(() -> new ResourceNotFoundException("ChatSession not found"));

        Message m = new Message();
        m.setChatSession(chatS);
        m.setSender(sender);
        m.setContent(content);
        messageRepo.save(m);
    }
}