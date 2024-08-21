package it.univaq.example.webshop.data.model;

import java.time.LocalDateTime;

import it.univaq.example.webshop.data.model.impl.NotificationTypeEnum;
import it.univaq.framework.data.DataItem;

public interface Notification extends DataItem<Integer> {
    
    User getRecipient();

    void setRecipient(User recipient);

    String getMessage();

    void setMessage(String message);

    String getLink();

    void setLink(String link);

    NotificationTypeEnum getType();

    void setType(NotificationTypeEnum value);

    LocalDateTime getCreationDate();

    void setCreationDate(LocalDateTime creationDate);

    Boolean isRead();

    void setRead(boolean read);
}
