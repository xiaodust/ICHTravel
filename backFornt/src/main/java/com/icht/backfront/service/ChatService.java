package com.icht.backfront.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
public class ChatService {
    private final ChatClient chatClient;

    @Autowired
    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public Flux<String> QueryStream(String message,String chatId) {
        return chatClient.prompt().user(message).
                advisors(
                        a-> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY,chatId).
                                param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)).stream().content();
    }

    public String Query(String message, String sysMsg) {
        return chatClient.prompt().system(sysMsg).user(message).call().content();
    }
}
