package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.dto.request.ChatGPTRequest;
import com.Cibertec.GreenGuard.dto.request.PromptRequest;
import com.Cibertec.GreenGuard.dto.response.ChatGPTResponse;
import com.Cibertec.GreenGuard.util.ChatbotPrompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ChatGPTService {

    private final RestClient restClient;

    public ChatGPTService(RestClient restClient){
        this.restClient = restClient;
    }

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.model}")
    private String model;

    public ChatGPTResponse getChatGPTResponse(PromptRequest promptRequest) {

        ChatGPTRequest chatGPTRequest = new ChatGPTRequest(
                model,
                List.of(
                        new ChatGPTRequest.Message("system", ChatbotPrompt.BASE_PROMPT),
                        new ChatGPTRequest.Message("user", promptRequest.prompt())
                )
        );

        return restClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(chatGPTRequest)
                .retrieve()
                .body(ChatGPTResponse.class);
    }
}

