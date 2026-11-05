package com.zosh.controller;

import com.zosh.request.AiChatRequest;
import com.zosh.response.AiChatResponse;
import com.zosh.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/ai", "/ai"})
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@RequestBody AiChatRequest request) {
        AiChatResponse response = aiChatService.chatWithAi(request.getMessage());
        return ResponseEntity.ok(response);
    }
}
