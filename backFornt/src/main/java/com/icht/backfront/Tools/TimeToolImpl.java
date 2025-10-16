package com.icht.backfront.Tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Component
public class TimeToolImpl implements TimeTool{

    @Override
    @Tool(description = "获取当前时间,当用户询问相关时讯，天气，时间，日期时，可以调用此工具",name = "time")
    public String getTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}
