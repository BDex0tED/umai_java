package com.sayra.umai.controller;

import com.sayra.umai.model.dto.ChatSessionDTO;
import com.sayra.umai.model.request.ChatSessionRequest;
import com.sayra.umai.model.response.ChatSessionResponse;
import com.sayra.umai.service.ChatSessionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat-sessions")
@RequiredArgsConstructor
@Validated
public class ChatSessionController {
    private final ChatSessionService chatSessionService;

    @GetMapping
    public ResponseEntity<Page<ChatSessionResponse>> getAllChatSessions(@PageableDefault(size = 30,sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(chatSessionService.getUserSessions(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatSessionResponse> getChatSession(@PathVariable @Positive Long id){
        return ResponseEntity.ok(chatSessionService.getChatSession(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChatSession(@PathVariable @Positive Long id){
        chatSessionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ChatSessionResponse> createChatSession(@RequestBody @Valid ChatSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatSessionService.createSession(request));
    }

}
