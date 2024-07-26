package it.univaq.example.webmarket.data.model.impl;

import java.time.LocalDate;

import it.univaq.example.webmarket.data.model.Notification;
import it.univaq.example.webmarket.data.model.User;
import it.univaq.framework.data.DataItemImpl;

public class NotificationImpl extends DataItemImpl<Integer> implements Notification {

    private User recipient;
    private String message;
    private LocalDate date;
    private Boolean read;

    public NotificationImpl() {
        super();
        recipient = null;
        message = "";
        date = LocalDate.now();
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
    public LocalDate getDate() {
        return date;
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
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
