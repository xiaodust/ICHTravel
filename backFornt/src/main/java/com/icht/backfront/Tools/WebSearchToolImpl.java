package com.icht.backfront.Tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class WebSearchToolImpl implements WebSearchTool{
    private final String key;
    private final String url;

    public WebSearchToolImpl(@Value("${spring.ai.websearch.api-key}") String key,
                             @Value("${spring.ai.websearch.url}") String url) {
        this.key = key;
        this.url = url;
    }

    @Override
    @Tool(description = "进行网络搜索来查询信息",name = "webSearch")
    public String search(String text) throws JsonProcessingException {

        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索内容不能为空");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        HttpClient httpClient= HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        HttpClient client=HttpClient.newHttpClient();
        // 创建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", text);
        requestBody.put("freshness", "noLimit");
        requestBody.put("summary", true);
        requestBody.put("count", 10);

        String requestBodyJson = objectMapper.writeValueAsString(requestBody);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + key)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .timeout(Duration.ofSeconds(60))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // 解析JSON响应
                JsonNode rootNode = objectMapper.readTree(response.body());
                return rootNode.toPrettyString();
            } else {
                return "搜索失败: 状态码 " + response.statusCode() + ", 响应体: " + response.body();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
