package com.sayra.umai.WorkPackage.Controllers;

import com.sayra.umai.WorkPackage.DTO.ChatSessionDTO;
import com.sayra.umai.WorkPackage.Services.ChatSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat-sessions")
public class ChatSessionController {
    private final ChatSessionService chatSessionService;
    public ChatSessionController(ChatSessionService chatSessionService) {
        this.chatSessionService = chatSessionService;
    }

    @GetMapping()
    public ResponseEntity<List<ChatSessionDTO>> getAllChatSessions(Principal principal){
        return ResponseEntity.ok(chatSessionService.getUserSessions(principal));
    }

//    public ResponseEntity<List<ChatSessionDTO>> getChatSession(Principal principal, Long id){
//        return ResponseEntity.ok(chatSessionService.createOrContinueChatSession(principal, id));
//    }
}
