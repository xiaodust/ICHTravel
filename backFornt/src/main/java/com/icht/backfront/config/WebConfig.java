package com.icht.backfront.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类 - 用于配置静态资源映射
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 显式配置静态资源映射
        // 将/uploads/**路径映射到系统绝对路径，确保跨设备访问一致性
        String uploadPath = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath)
                .setCachePeriod(3600); // 设置1小时缓存
        
        // 保留Spring Boot对static目录的默认映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
}