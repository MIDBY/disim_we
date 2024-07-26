package it.univaq.example.webmarket.data.model;

import java.time.LocalDate;
import it.univaq.framework.data.DataItem;

public interface Notification extends DataItem<Integer> {
    
    User getRecipient();

    void setRecipient(User recipient);

    String getMessage();

    void setMessage(String message);

    LocalDate getCreationDate();

    void setCreationDate(LocalDate creationDate);

    Boolean isRead();

    void setRead(boolean read);
}
