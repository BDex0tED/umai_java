package com.sayra.umai.service;

import com.sayra.umai.model.dto.ChatSessionDTO;
import com.sayra.umai.model.entity.work.ChatSession;
import com.sayra.umai.model.request.ChatSessionRequest;
import com.sayra.umai.model.response.ChatSessionResponse;
import jakarta.xml.bind.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.List;

public interface ChatSessionService {
    Page<ChatSessionResponse> getUserSessions(Pageable pageable);
    ChatSessionResponse getChatSession(Long id);
    ChatSessionResponse createSession(ChatSessionRequest chatSessionDTO);

    void delete(Long id);
}
