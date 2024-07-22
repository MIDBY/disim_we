package it.univaq.example.webmarket.data.model.impl;

import java.time.LocalDate;
import it.univaq.example.webmarket.data.model.Proposal;
import it.univaq.example.webmarket.data.model.Request;
import it.univaq.example.webmarket.data.model.User;
import it.univaq.framework.data.DataItemImpl;

public class ProposalImpl extends DataItemImpl<Integer> implements Proposal {

    private Integer id;
    private Request request;
    private User technician;
    private LocalDate creationDate;
    private ProposalStateEnum proposalState;
    private String motivation;

    public ProposalImpl() {
        super();
        request = new RequestImpl();
        technician = new UserImpl();
        creationDate = LocalDate.now();
        proposalState = ProposalStateEnum.INATTESA;
        motivation = "";
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    public void setRequest(Request request) {
        this.request = request;
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
    public ProposalStateEnum getProposalState() {
        return proposalState;
    }

    @Override
    public void setProposalState(ProposalStateEnum value) {
        proposalState = value;
    }

    @Override
    public String getMotivation() {
        return motivation;
    }

    @Override
    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }
    
}
