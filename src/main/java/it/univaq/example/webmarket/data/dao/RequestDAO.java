package it.univaq.example.webmarket.data.dao;

import java.util.List;
import it.univaq.example.webmarket.data.model.Request;
import it.univaq.framework.data.DataException;

public interface RequestDAO {

    Request createRequest();

    Request getRequest(int request_key) throws DataException;

    List<Request> getRequests(int user_key) throws DataException;

    List<Request> getRequests() throws DataException;

    List<Request> getUnassignedRequests() throws DataException;

    void setRequest(Request request) throws DataException;
    
}