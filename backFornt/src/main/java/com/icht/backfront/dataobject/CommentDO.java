package com.icht.backfront.dataobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icht.backfront.model.Comment;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class CommentDO {
    private String id;

    private String noteId;

    private String parentId;

    private String userId;

    private String userName;

    private String content;

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

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
    public CommentDO(){

    }

    public CommentDO(Comment comment){
        BeanUtils.copyProperties(comment,this);
    }

    public Comment ToModel(){
        Comment comment = new Comment();
        comment.setId(this.getId());
        comment.setNoteId(this.getNoteId());
        comment.setParentId(this.getParentId());
        comment.setUserId(this.getUserId());
        comment.setUserName(this.getUserName());
        comment.setContent(this.getContent());
        comment.setLiked(this.getLiked());
        comment.setGmtCreated(this.getGmtCreated());
        comment.setGmtModified(this.getGmtModified());
        return comment;
    }

}
