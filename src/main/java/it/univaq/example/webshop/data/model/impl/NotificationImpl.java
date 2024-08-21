package it.univaq.example.webshop.data.model.impl;

import java.time.LocalDateTime;
import it.univaq.example.webshop.data.model.Notification;
import it.univaq.example.webshop.data.model.User;
import it.univaq.framework.data.DataItemImpl;

public class NotificationImpl extends DataItemImpl<Integer> implements Notification {

    private User recipient;
    private String message;
    private String link;
    private NotificationTypeEnum type;
    private LocalDateTime creationDate;
    private Boolean read;

    public NotificationImpl() {
        super();
        recipient = null;
        message = "";
        link = "";
        type = NotificationTypeEnum.INFO;
        creationDate = LocalDateTime.now();
        read = false;
    }

    @Override
    public User getRecipient() {
        return recipient;
    }

    @Override
    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public NotificationTypeEnum getType() {
        return type;
    }

    @Override
    public void setType(NotificationTypeEnum value) {
        this.type = value;
    }

    @Override
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public Boolean isRead() {
        return read;
    }

    @Override
    public void setRead(boolean read) {
        this.read = read;
    }
}
