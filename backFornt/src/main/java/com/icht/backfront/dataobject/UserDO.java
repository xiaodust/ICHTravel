package com.icht.backfront.dataobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icht.backfront.model.User;

import java.time.LocalDateTime;

public class UserDO {
    private String id;
    private String nickName;
    private String name;
    private String password;
    private String number;
    private String Signature;
    private String avatar;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreated;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtModified;

    public User ToMode(){
        User user = new User();
        user.setNickName(nickName);
        user.setPassword(password);
        user.setGmtCreated(gmtCreated);
        user.setGmtModified(gmtModified);
        user.setId(id);
        user.setName(name);
        user.setNumber(number);
        user.setSignature(Signature);
        user.setAvatar(avatar);
        return user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSignature() {
        return Signature;
    }

    public void setSignature(String signature) {
        Signature = signature;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
