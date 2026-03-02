package com.sayra.umai.service;

import com.sayra.umai.model.request.AIAskRequest;

import java.util.concurrent.CompletableFuture;

public interface AiService {
    CompletableFuture<String> askAiAsync(AIAskRequest request);
}
