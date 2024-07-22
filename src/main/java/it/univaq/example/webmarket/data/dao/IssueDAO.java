package it.univaq.example.webmarket.data.dao;

import it.univaq.example.webmarket.data.model.Issue;
import it.univaq.framework.data.DataException;
import java.util.List;

/**
 *
 * @author Giuseppe Della Penna
 */
public interface IssueDAO {

    //metodo "factory" che permettono di creare
    //e inizializzare opportune implementazioni
    //delle interfacce del modello dati, nascondendo
    //all'utente tutti i particolari
    //factory method to create and initialize
    //suitable implementations of the data model interfaces,
    //hiding all the implementation details
    Issue createIssue();

    Issue getIssue(int issue_key) throws DataException;

    Issue getLatestIssue() throws DataException;

    List<Issue> getIssues() throws DataException;

    int getLatestIssueNumber() throws DataException;

    void storeIssue(Issue issue) throws DataException;

}