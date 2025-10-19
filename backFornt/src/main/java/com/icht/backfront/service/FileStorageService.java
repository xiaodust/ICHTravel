package com.icht.backfront.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    // 定义存储路径 - 使用相对路径存储在项目目录下
    private final String uploadDir = "src/main/resources/static/uploads/";

    @PostConstruct
    public void init() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;

        try {
            // 确保路径安全，防止路径遍历攻击
            if (fileName.contains("..")) {
                throw new FileStorageException("文件名包含无效路径序列: " + fileName);
            }
            // 构建目标文件路径
            Path targetLocation = Paths.get(uploadDir).resolve(uniqueFileName);
            // 复制文件到目标位置
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return uniqueFileName; // 返回存储后的文件名
        } catch (IOException ex) {
            throw new FileStorageException("无法存储文件: " + fileName, ex);
        }
    }

}
