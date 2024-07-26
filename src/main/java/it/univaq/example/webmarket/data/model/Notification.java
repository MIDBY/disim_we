package it.univaq.example.webmarket.data.model;

import java.time.LocalDate;
import it.univaq.framework.data.DataItem;

public interface Notification extends DataItem<Integer> {
    
    User getRecipient();

    void setRecipient(User recipient);

    String getMessage();

    void setMessage(String message);

    LocalDate getDate();

    void setDate(LocalDate date);

    Boolean isRead();

    void setRead(boolean read);
}
