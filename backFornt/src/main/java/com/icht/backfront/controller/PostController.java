package com.icht.backfront.controller;

import com.icht.backfront.model.Result;
import com.icht.backfront.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 接收Base64编码的图片并保存
     * @param requestBody 包含base64Image和fileExtension的请求体
     * @return 处理结果
     */
    @PostMapping("/upload")
    public Result uploadBase64Post(@RequestBody Map<String, String> requestBody) {
        Result result = new Result();
        
        try {
            String base64Image = requestBody.get("base64Image");
            String fileExtension = requestBody.get("fileExtension");
            
            if (base64Image == null || base64Image.isEmpty()) {
                throw new IllegalArgumentException("base64Image不能为空");
            }
            
            if (fileExtension == null || fileExtension.isEmpty()) {
                fileExtension = "jpg"; // 默认扩展名
            }
            
            String fileName = fileStorageService.storeBase64Image(base64Image, fileExtension);
            
            // 生成完整的图片访问URL，包含明确的后端地址
            // 使用固定的http://localhost:8080格式，确保前端能正确访问
            String fileDownloadUri = "http://localhost:8080/uploads/" + fileName;
                    
            result.setSuccess(true);
            result.setCode("200");
            result.setMessage("上传成功");
            result.setData(fileDownloadUri);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setCode("500");
            result.setMessage("上传失败: " + e.getMessage());
        }
        
        return result;
    }
}