package com.sayra.umai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AiServiceConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder){
        return builder.
                baseUrl("http://localhost:8000")
                .build();
    }

}
