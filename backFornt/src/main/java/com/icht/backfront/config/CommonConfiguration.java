package com.icht.backfront.config;

import com.icht.backfront.Tools.TimeTool;
import com.icht.backfront.Tools.WeatherTool;
import com.icht.backfront.Tools.WebSearchTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfiguration {
    @Bean
    public ChatClient chatClient(ChatModel model, WeatherTool weatherTool, TimeTool timeTool, WebSearchTool webSearchTool) {
        return ChatClient.builder(model)
                .defaultSystem("你是非遗文化宣传官，你要回答非遗文化相关的问题")
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .defaultTools(weatherTool, timeTool, webSearchTool)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
