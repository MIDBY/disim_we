package it.univaq.example.webmarket.data.model;

import java.time.LocalDate;
import it.univaq.example.webmarket.data.model.impl.ProposalStateEnum;
import it.univaq.framework.data.DataItem;

public interface Proposal extends DataItem<Integer> {

    Request getRequest();

    void setRequest(Request request);

    User getTechnician();

    void setTechnician(User techUser);

    String getProductName();

    void setProductName(String productName);

    String getProducerName();

    void setProducerName(String producerName);

    String getProductDescription();

    void setProductDescription(String productDescription);

    Float getProductPrice();

    void setProductPrice(Float price);

    String getUrl();

    boolean setUrl(String url);

    String getNotes();

    void setNotes(String notes);

    LocalDate getCreationDate();

    void setCreationDate(LocalDate creationDate);

    ProposalStateEnum getProposalState();

    void setProposalState(ProposalStateEnum value);

    String getMotivation();

    void setMotivation(String motivation);
       
}
