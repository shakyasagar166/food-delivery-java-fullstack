package com.zosh.service;

import com.zosh.response.AiChatResponse;

public interface AiChatService {
    AiChatResponse chatWithAi(String userMessage);
}
