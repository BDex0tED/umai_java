package com.sayra.umai.mapper;

import com.sayra.umai.model.dto.ChatSessionDTO;
import com.sayra.umai.model.entity.work.ChatSession;
import com.sayra.umai.model.response.ChatSessionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatSessionMapper {
    ChatSessionResponse toChatSessionResponse(ChatSession chatSession);

    List<ChatSessionResponse> toChatSessionResponse(List<ChatSession> chatSessions);
}
