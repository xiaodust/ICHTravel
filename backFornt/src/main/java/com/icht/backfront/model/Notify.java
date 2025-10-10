package com.icht.backfront.model;

public class Notify {
     private NotifyPlatform notifyPlatform;

     private String notifyType;

     private String title;

     private String text;

     private String pciUrl;

     private String link;

    public NotifyPlatform getNotifyPlatform() {
        return notifyPlatform;
    }

    public void setNotifyPlatform(NotifyPlatform notifyPlatform) {
        this.notifyPlatform = notifyPlatform;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPciUrl() {
        return pciUrl;
    }

    public void setPciUrl(String pciUrl) {
        this.pciUrl = pciUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
