package com.sayra.umai.service.impl;

import com.sayra.umai.model.request.AIAskRequest;
import com.sayra.umai.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
    private final RestClient restClient;

    @Value("${okuulib.ai.ask_endpoint}")
    private String ASK_ENDPOINT;

    @Async
    @Override
    public CompletableFuture<String> askAiAsync(AIAskRequest request) {
        try {
            String response = restClient.post()
                    .uri(ASK_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);

            return CompletableFuture.completedFuture(response);
        } catch (Exception e) {
            log.error("Error while connecting to AI", e);
            return CompletableFuture.completedFuture("Error while connecting to AI occured");
        }
    }
}