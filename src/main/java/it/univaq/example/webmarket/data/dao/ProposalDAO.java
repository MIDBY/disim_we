package it.univaq.example.webmarket.data.dao;

import java.util.List;
import it.univaq.example.webmarket.data.model.Proposal;
import it.univaq.example.webmarket.data.model.impl.ProposalStateEnum;
import it.univaq.framework.data.DataException;

public interface ProposalDAO {

    Proposal createProposal();

    Proposal getProposal(int proposal_key) throws DataException;

    List<Proposal> getProposalsByRequest(int request_key) throws DataException;

    List<Proposal> getProposals() throws DataException;

    List<Proposal> getProposalsByState(ProposalStateEnum value) throws DataException;

    void setProposal(Proposal proposal) throws DataException;
}