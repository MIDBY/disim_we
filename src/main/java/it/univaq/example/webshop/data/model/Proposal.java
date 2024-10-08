package it.univaq.example.webshop.data.model;

import java.time.LocalDateTime;
import it.univaq.example.webshop.data.model.impl.ProposalStateEnum;
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

    LocalDateTime getCreationDate();

    void setCreationDate(LocalDateTime creationDate);

    ProposalStateEnum getProposalState();

    void setProposalState(ProposalStateEnum value);

    String getMotivation();

    void setMotivation(String motivation);
       
}
