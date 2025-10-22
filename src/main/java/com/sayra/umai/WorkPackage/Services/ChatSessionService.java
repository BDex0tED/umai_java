package com.sayra.umai.WorkPackage.Services;

import com.sayra.umai.UserPackage.Entities.UserEntity;
import com.sayra.umai.UserPackage.Repo.UserEntityRepo;
import com.sayra.umai.WorkPackage.DTO.ChatSessionDTO;
import com.sayra.umai.WorkPackage.Entities.ChatSession;
import com.sayra.umai.WorkPackage.Repos.ChatSessionRepo;
import com.sayra.umai.WorkPackage.Repos.MessageRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class ChatSessionService {
    private final MessageRepo messageRepo;
    private final ChatSessionRepo chatSessionRepo;
    private final UserEntityRepo userEntityRepo;
    public ChatSessionService(MessageRepo messageRepo, ChatSessionRepo chatSessionRepo, UserEntityRepo userEntityRepo) {
        this.messageRepo = messageRepo;
        this.chatSessionRepo = chatSessionRepo;
        this.userEntityRepo = userEntityRepo;
    }

    public List<ChatSessionDTO> getUserSessions(Principal principal){
        UserEntity user = userEntityRepo.findByUsername(principal.getName()).orElseThrow(()->new IllegalArgumentException("User not found"));

        List<ChatSession> chatSessions = chatSessionRepo.findAllByUser(user);

        if(chatSessions.isEmpty()){
            throw new EntityNotFoundException("User has no chat sessions");
        }
        List<ChatSessionDTO> chatSessionDTOS = chatSessions.stream().map(chatSession->{
            ChatSessionDTO chatSessionDTO = new ChatSessionDTO();
            chatSessionDTO.setId(chatSession.getId());
            chatSessionDTO.setTitle(chatSession.getTitle());
            chatSessionDTO.setUserName(user.getUsername());
            return chatSessionDTO;
        }).toList();

        return chatSessionDTOS;
    }

//    public ChatSessionDTO getChatSession(Principal principal, Long id){
//        UserEntity user = userEntityRepo.findByUsername(principal.getName()).orElseThrow(()->new IllegalArgumentException("User not found"));
//        ChatSession chatSession = createOrContinueChatSession(principal, id);
//
//        ChatSessionDTO chatSessionDTO = new ChatSessionDTO();
//        chatSessionDTO.setUserName(chatSession.getUser().getUsername());
//
//    }


    public ChatSession createOrContinueChatSession(Principal principal, Long id){
        UserEntity user = userEntityRepo.findByUsername(principal.getName()).orElseThrow(()->new IllegalArgumentException("User not found"));
        if(id == null){
            ChatSession chatSession = new ChatSession();
            chatSession.setUser(user);
            chatSession.setTitle("New chat");
            chatSessionRepo.save(chatSession);
            return chatSession;
        }
        return chatSessionRepo.findByIdAndUser(id, user).orElseThrow(()->new EntityNotFoundException("Chat session not found"));
    }




}
