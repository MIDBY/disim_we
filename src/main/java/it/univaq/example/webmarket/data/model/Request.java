package it.univaq.example.webmarket.data.model;

import java.time.LocalDate;
import it.univaq.example.webmarket.data.model.impl.OrderStateEnum;
import it.univaq.example.webmarket.data.model.impl.RequestStateEnum;
import it.univaq.framework.data.DataItem;

public interface Request extends DataItem<Integer> {
    
    Integer getId();

    String getTitle();

    void setTitle(String title);

    String getDescription();

    void setDescription(String description);

    Category getCategory();

    void setCategory(Category category);

    User getOrdering();

    void setOrderding(User orderingUser);

    User getTechnician();

    void setTechnician(User techUser);

    LocalDate getCreationDate();

    void setCreationDate(LocalDate creationDate);

    RequestStateEnum getRequestState();

    void setRequestState(RequestStateEnum value);

    OrderStateEnum getOrderState();

    void setOrderState(OrderStateEnum value);

    String getNotes();

    void setNotes(String notes);
}
