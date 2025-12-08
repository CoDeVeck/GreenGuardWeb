package com.Cibertec.GreenGuard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Cibertec.GreenGuard.dto.request.PromptRequest;
import com.Cibertec.GreenGuard.service.ChatGPTService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

	@Autowired
	private ChatGPTService chatGPTService;
	
	@PostMapping("/conversacion")
	public String chat(@RequestBody PromptRequest prompt) {		
		return chatGPTService.getChatGPTResponse(prompt);
	}
	
}
