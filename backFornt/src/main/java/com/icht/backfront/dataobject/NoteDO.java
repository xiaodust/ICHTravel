package com.icht.backfront.dataobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icht.backfront.model.Note;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NoteDO {
    private String id;

    private String userId;

    private String userName;

    private String context;

    private String images; // 使用字符串存储JSON格式的图片列表

    private int commentCount;

    private int liked;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreated;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtModified;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public LocalDateTime getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(LocalDateTime gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public NoteDO(){}

    public NoteDO(Note note){
        this.id = note.getId();
        this.userId = note.getUserId();
        this.userName = note.getUserName();
        this.context = note.getContext();
        this.commentCount = note.getCommentCount();
        this.liked = note.getLiked();
        this.gmtCreated = note.getGmtCreated();
        this.gmtModified = note.getGmtModified();
        
        // 将List<String>转换为JSON字符串
        if (note.getImages() != null) {
            try {
                this.images = objectMapper.writeValueAsString(note.getImages());
            } catch (Exception e) {
                this.images = "[]";
            }
        }
    }

    public Note ToModel(){
        Note note = new Note();
        note.setId(this.getId());
        note.setUserId(this.getUserId());
        note.setUserName(this.getUserName());
        note.setContext(this.getContext());
        note.setCommentCount(this.getCommentCount());
        note.setLiked(this.getLiked());
        note.setGmtCreated(this.getGmtCreated());
        note.setGmtModified(this.getGmtModified());
        
        // 将存储的字符串解析为图片列表，兼容JSON数组、逗号分隔与单URL
        if (this.images != null && !this.images.isEmpty()) {
            try {
                java.util.List<String> imgList = objectMapper.readValue(
                    this.images,
                    objectMapper.getTypeFactory().constructCollectionType(java.util.List.class, String.class)
                );
                note.setImages(imgList != null ? imgList : new java.util.ArrayList<>());
            } catch (Exception e) {
                java.util.List<String> fallback = new java.util.ArrayList<>();
                String s = this.images.trim();
                if (!s.isEmpty()) {
                    // 去除可能存在的数组括号
                    if (s.startsWith("[") && s.endsWith("]")) {
                        s = s.substring(1, s.length() - 1);
                    }
                    String[] parts = s.split("\\s*,\\s*");
                    for (String p : parts) {
                        String val = p.replaceAll("^\"|\"$", "").replaceAll("^'|'$", "").trim();
                        if (!val.isEmpty()) fallback.add(val);
                    }
                }
                note.setImages(fallback);
            }
        } else {
            note.setImages(new java.util.ArrayList<>());
        }
        
        return note;
    }
}
