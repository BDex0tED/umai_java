package com.sayra.umai.service.impl;

import com.sayra.umai.exception.ResourceNotFoundException;
import com.sayra.umai.mapper.ChatSessionMapper;
import com.sayra.umai.model.entity.user.UserEntity;
import com.sayra.umai.model.request.ChatSessionRequest;
import com.sayra.umai.model.response.ChatSessionResponse;
import com.sayra.umai.repo.ChatSessionRepo;
import com.sayra.umai.repo.UserEntityRepo;
import com.sayra.umai.model.dto.ChatSessionDTO;
import com.sayra.umai.model.entity.work.ChatSession;
import com.sayra.umai.repo.MessageRepo;
import com.sayra.umai.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sayra.umai.exception.ValidationException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatSessionServiceImpl implements ChatSessionService {
    private final UserService userService;
    private final ChatSessionMapper chatSessionMapper;
    private final ChatSessionRepo chatSessionRepo;


    @Transactional(readOnly = true)
    @Override
    public Page<ChatSessionResponse> getUserSessions(Pageable pageable){
        UserEntity user = userService.getCurrentUser();
        Page<ChatSession> chatSessions = chatSessionRepo.findAllByUser(user, pageable);

        return chatSessions.map(chatSessionMapper::toChatSessionResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public ChatSessionResponse getChatSession(Long id){
        UserEntity user = userService.getCurrentUser();
        ChatSession chatSession = chatSessionRepo.findByIdAndUser(id, user).orElseThrow(()->new ResourceNotFoundException("ChatSession not found"));

        return chatSessionMapper.toChatSessionResponse(chatSession);
    }

    @Transactional
    @Override
    public ChatSessionResponse createSession(ChatSessionRequest chatSessionRequest){
        UserEntity user = userService.getCurrentUser();

        ChatSession chatSession = new ChatSession();
        chatSession.setTitle(chatSessionRequest.title());
        chatSession.setUser(user);

        return chatSessionMapper.toChatSessionResponse(chatSessionRepo.save(chatSession));
    }


    @Transactional
    @Override
    public void delete(Long id) {
        chatSessionRepo.delete(chatSessionRepo.findByIdAndUser(id, userService.getCurrentUser()).orElseThrow(()->new ResourceNotFoundException("ChatSession not found")));
    }




}
