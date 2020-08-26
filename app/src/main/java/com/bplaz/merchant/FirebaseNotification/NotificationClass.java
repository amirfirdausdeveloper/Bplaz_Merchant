package com.bplaz.merchant.FirebaseNotification;

public class NotificationClass {
    private String title;
    private String message;

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
