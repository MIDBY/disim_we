package it.univaq.example.webshop.data.model;

import java.time.LocalDate;
import java.util.List;

import it.univaq.example.webshop.data.model.impl.OrderStateEnum;
import it.univaq.example.webshop.data.model.impl.RequestStateEnum;
import it.univaq.framework.data.DataItem;

public interface Request extends DataItem<Integer> {
    
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

    List<RequestCharacteristic> getRequestCharacteristics();

    void setRequestCharacteristics(List<RequestCharacteristic> requestCharacteristics);

    void addRequestCharacteristic(RequestCharacteristic requestCharacteristic);

    List<Proposal> getProposals();

    void setProposals(List<Proposal> proposals);

    void addProposal(Proposal proposal);

    String getNotes();

    void setNotes(String notes);
}
