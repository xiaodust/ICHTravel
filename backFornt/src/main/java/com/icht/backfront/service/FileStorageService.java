package com.icht.backfront.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

import jakarta.annotation.PostConstruct;

// 自定义文件存储异常类
class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

@Service
public class FileStorageService {
    // 定义存储路径 - 使用绝对路径存储在项目目录下
    private final String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";

    @PostConstruct
    public void init() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    /**
     * 存储Base64编码的图片
     * @param base64Image Base64编码的图片数据
     * @param fileExtension 文件扩展名（如jpg, png等）
     * @return 存储后的文件名
     */
    public String storeBase64Image(String base64Image, String fileExtension) {
        try {
            // 移除Base64前缀（如果有）
            String base64Data = base64Image;
            if (base64Image.contains(",")) {
                base64Data = base64Image.split(",")[1];
            }
            
            // 生成唯一文件名
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;
            
            // 解码Base64数据
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            
            // 构建目标文件路径
            Path targetLocation = Paths.get(uploadDir).resolve(uniqueFileName);
            
            // 将解码后的数据写入文件
            Files.write(targetLocation, imageBytes);
            
            return uniqueFileName; // 返回存储后的文件名
        } catch (IOException ex) {
            throw new FileStorageException("无法存储Base64图片", ex);
        } catch (IllegalArgumentException ex) {
            throw new FileStorageException("无效的Base64编码", ex);
        }
    }

}
