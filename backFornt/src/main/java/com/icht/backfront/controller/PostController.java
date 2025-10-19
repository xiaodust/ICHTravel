package com.icht.backfront.controller;

import com.icht.backfront.model.Result;
import com.icht.backfront.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public Result uploadPost(@RequestParam("image") MultipartFile image) {
        Result result = new Result();
        result.setSuccess(true);
        result.setCode("200");
        result.setMessage("上传成功");
            String fileName = fileStorageService.storeFile(image);
            // 生成图片访问URL
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(fileName)
                    .toUriString();
            result.setData(fileDownloadUri);
            return result;
}
}