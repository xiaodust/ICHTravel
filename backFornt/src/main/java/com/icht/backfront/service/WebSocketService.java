package com.icht.backfront.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icht.backfront.model.WebSocketMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import reactor.core.Disposable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class WebSocketService implements WebSocketHandler {
    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private final ConcurrentHashMap<String, Disposable> subscriptions = new ConcurrentHashMap<>();
    private final ChatService chatService;

    @Autowired
    public WebSocketService(ChatService chatService) {
        this.chatService =chatService;
    }

    private final ConcurrentHashMap<String, Long> lastHeartbeatTime = new ConcurrentHashMap<>();
    private ScheduledExecutorService heartbeatExecutor;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocket onOpen: " + session.getId());
        sessions.add(session);
        lastHeartbeatTime.put(session.getId(), System.currentTimeMillis());
        if (heartbeatExecutor == null || heartbeatExecutor.isShutdown()) {
            heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
            heartbeatExecutor.scheduleAtFixedRate(this::checkHeartbeat, 30, 30, TimeUnit.SECONDS);
        }
    }

    // 心跳检测
    private void checkHeartbeat() {
        long now = System.currentTimeMillis();
        for (WebSocketSession session : sessions) {
            Long lastTime = lastHeartbeatTime.get(session.getId());
            if (lastTime != null && now - lastTime > 60000) { // 60秒无心跳则关闭连接
                try {
                    System.out.println("心跳超时，关闭连接: " + session.getId());
                    session.close(new CloseStatus(1001, "心跳超时"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String userMessage = message.getPayload().toString();
        System.out.println("收到客户端消息: " + userMessage);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(userMessage);
        String messageType = jsonNode.get("type").asText();
        String messageContent = null;
        String messageChatId = null;

        if (jsonNode.get("content") != null) {
            messageContent = jsonNode.get("content").asText();
        }
        if (jsonNode.get("chatId") != null) {
            messageChatId = jsonNode.get("chatId").asText();
        }

        lastHeartbeatTime.put(session.getId(), System.currentTimeMillis());

        if (messageType.equals("ping")) {
            sendMessage(session, new WebSocketMsg(
                    "ping",
                    "",
                    true
            ));
            return;
        }

        if (messageType.equals("chat")) {
            // 取消之前的订阅（如果存在）
            Disposable previousSubscription = subscriptions.get(session.getId());
            if (previousSubscription != null && !previousSubscription.isDisposed()) {
                previousSubscription.dispose();
            }

            StringBuilder roleReplyBuilder = new StringBuilder();

            // 调用大模型并流式返回结果
            Disposable subscription = chatService.QueryStream(messageContent,messageChatId)
                    .subscribe(
                            response -> {
                                WebSocketMsg responseMessage = new WebSocketMsg(
                                        "llm_QueryStream_Answer",
                                        response,
                                        false
                                );
                                roleReplyBuilder.append(responseMessage.getData());
                                sendMessage(session, responseMessage);
                            },
                            error -> {
                                System.err.println("大模型调用错误: " + error.getMessage());
                                WebSocketMsg errorMessage = new WebSocketMsg(
                                        "error",
                                        "抱歉，处理请求时发生错误: " + error.getMessage(),
                                        true
                                );
                                sendMessage(session, errorMessage);
                            },
                            () -> {
                                System.out.println("流式响应完成");
                                WebSocketMsg endMessage = new WebSocketMsg(
                                        "llm_QueryStream_Answer",
                                        "",
                                        true
                                );
                                roleReplyBuilder.append(endMessage.getData());

                                sendMessage(session, endMessage);
                            }
                    );

            subscriptions.put(session.getId(), subscription);
        }
    }

    private void sendMessage(WebSocketSession session, WebSocketMsg webSocketMessage) {
        try {
            if (session.isOpen()) {
                String jsonMessage = webSocketMessage.toString();
                session.sendMessage(new TextMessage(jsonMessage));
            }
        } catch (Exception e) {
            System.err.println("发送消息失败: " + e.getMessage());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String errorMessage = "WebSocket错误: " + exception.getMessage();
        System.err.println(errorMessage);
        exception.printStackTrace();

        WebSocketMsg errorMsg = new WebSocketMsg(
                "error",
                "连接发生错误: " + exception.getMessage(),
                true
        );
        sendMessage(session, errorMsg);

        session.close(new CloseStatus(1001, errorMessage));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("WebSocket onClose: " + session.getId() + ", 代码: " + closeStatus.getCode() + ", 原因: " + closeStatus.getReason());
        sessions.remove(session);
        // 取消订阅
        Disposable subscription = subscriptions.get(session.getId());
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
        subscriptions.remove(session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
