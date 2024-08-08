package it.univaq.example.webshop.data.dao;

import java.time.LocalDate;
import java.util.List;
import it.univaq.example.webshop.data.model.Proposal;
import it.univaq.example.webshop.data.model.impl.ProposalStateEnum;
import it.univaq.framework.data.DataException;

public interface ProposalDAO {

    Proposal createProposal();

    Proposal getProposal(int proposal_key) throws DataException;

    List<Proposal> getProposalsByRequest(int request_key) throws DataException;

    Proposal getLastProposalByRequest(int request_key) throws DataException;

    List<Proposal> getProposalsByTechnician(int user_key) throws DataException;

    List<Proposal> getProposalsByState(ProposalStateEnum value) throws DataException;

    List<Proposal> getProposalsByCreationMonth(LocalDate date) throws DataException;

    void setProposal(Proposal proposal) throws DataException;
}