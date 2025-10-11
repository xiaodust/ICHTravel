package com.icht.backfront.dataobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icht.backfront.model.Note;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class NoteDO {
    private String id;

    private String userId;

    private String userName;

    private String context;

    private String image;

    private int commentCount;

    private int liked;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreated;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtModified;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
        BeanUtils.copyProperties(note,this);
    }

    public Note ToModel(){
        Note note = new Note();
        note.setId(this.getId());
        note.setUserId(this.getUserId());
        note.setUserName(this.getUserName());
        note.setContext(this.getContext());
        note.setImage(this.getImage());
        note.setCommentCount(this.getCommentCount());
        note.setLiked(this.getLiked());
        note.setGmtCreated(this.getGmtCreated());
        note.setGmtModified(this.getGmtModified());
        return note;
    }
}
