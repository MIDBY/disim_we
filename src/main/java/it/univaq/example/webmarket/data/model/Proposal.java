package it.univaq.example.webmarket.data.model;

import java.time.LocalDate;
import it.univaq.example.webmarket.data.model.impl.ProposalStateEnum;
import it.univaq.framework.data.DataItem;

public interface Proposal extends DataItem<Integer> {

    Integer getId();

    Request getRequest();

    void setRequest(Request request);

    User getTechnician();

    void setTechnician(User techUser);

    LocalDate getCreationDate();

    void setCreationDate(LocalDate creationDate);

    ProposalStateEnum getProposalState();

    void setProposalState(ProposalStateEnum value);

    String getMotivation();

    void setMotivation(String motivation);
       
}
