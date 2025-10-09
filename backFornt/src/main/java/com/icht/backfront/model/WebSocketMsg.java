package com.icht.backfront.model;

public class WebSocketMsg<T> {
    private String type;
    private T data;
    private boolean streamEnd = false;
    private  String chatId;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public WebSocketMsg() {
    }

    public WebSocketMsg(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public WebSocketMsg(String type, T data, boolean streamEnd) {
        this.type = type;
        this.data = data;
        this.streamEnd = streamEnd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isStreamEnd() {
        return streamEnd;
    }

    public void setStreamEnd(boolean streamEnd) {
        this.streamEnd = streamEnd;
    }

    @Override
    public String toString() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String dataStr = objectMapper.writeValueAsString(data);
            return "{\"type\":\"" + type + "\",\"data\":" + dataStr + ",\"streamEnd\":" + streamEnd + "}";
        } catch (Exception e) {
            // 如果序列化失败，使用原来的逻辑作为备用
            String dataStr;
            if (data instanceof String) {
                dataStr = "\"" + data + "\"";
            } else {
                dataStr = String.valueOf(data);
            }
            return "{\"type\":\"" + type + "\",\"data\":" + dataStr + ",\"streamEnd\":" + streamEnd + "}";
        }
    }
}
