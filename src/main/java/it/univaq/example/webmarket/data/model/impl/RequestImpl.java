package it.univaq.example.webmarket.data.model.impl;

import java.time.LocalDate;
import java.util.List;
import it.univaq.example.webmarket.data.model.Category;
import it.univaq.example.webmarket.data.model.Proposal;
import it.univaq.example.webmarket.data.model.Request;
import it.univaq.example.webmarket.data.model.RequestCharacteristic;
import it.univaq.example.webmarket.data.model.User;
import it.univaq.framework.data.DataItemImpl;

public class RequestImpl extends DataItemImpl<Integer> implements Request {
    
    private String title;
    private String description;
    private Category category;
    private User ordering;
    private User technician;
    private LocalDate creationDate;
    private RequestStateEnum requestState;
    private OrderStateEnum orderState;
    private List<RequestCharacteristic> characteristics;
    private List<Proposal> proposals;
    private String notes;

    public RequestImpl() {
        super();
        title = "";
        description = "";
        category = null;
        ordering = null;
        technician = null;
        creationDate = LocalDate.now();
        requestState = RequestStateEnum.NUOVO;
        orderState = OrderStateEnum.INCORSO;
        characteristics = null;
        proposals = null;
        notes = "";
    }

    @Override
    public String getTitle() {
       return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
       this.description = description;
    }

    @Override
    public Category getCategory() {
        return category;
    }

    @Override
    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public User getOrdering() {
        return ordering;
    }

    @Override
    public void setOrderding(User orderingUser) {
        ordering = orderingUser;
    }

    @Override
    public User getTechnician() {
        return technician;
    }

    @Override
    public void setTechnician(User techUser) {
        technician = techUser;
    }

    @Override
    public LocalDate getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public RequestStateEnum getRequestState() {
        return requestState;
    }

    @Override
    public void setRequestState(RequestStateEnum value) {
        requestState = value;
    }

    @Override
    public OrderStateEnum getOrderState() {
        return orderState;
    }

    @Override
    public void setOrderState(OrderStateEnum value) {
        orderState = value;
    }

    @Override
    public List<RequestCharacteristic> getRequestCharacteristics() {
        return characteristics;
    }

    @Override
    public void setRequestCharacteristics(List<RequestCharacteristic> requestCharacteristics) {
        characteristics = requestCharacteristics;
    }

    @Override
    public void addRequestCharacteristic(RequestCharacteristic requestCharacteristic) {
        this.characteristics.add(requestCharacteristic);
    }


    @Override
    public List<Proposal> getProposals() {
        return proposals;
    }

    @Override
    public void setProposals(List<Proposal> proposals) {
        this.proposals = proposals;
    }

    @Override
    public void addProposal(Proposal proposal) {
        proposals.add(proposal);
    }

    @Override
    public String getNotes() {
        return notes;
    }

    @Override
    public void setNotes(String notes) {
        this.notes = notes;
    }

    
}
