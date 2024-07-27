package it.univaq.example.webshop.data.dao;

import java.util.List;

import it.univaq.example.webshop.data.model.Request;
import it.univaq.framework.data.DataException;

public interface RequestDAO {

    Request createRequest();

    Request getRequest(int request_key) throws DataException;

    List<Request> getRequestsByCategory(int category_key) throws DataException;

    List<Request> getRequestsByOrdering(int user_key) throws DataException;

    List<Request> getRequestsByTechnician(int user_key) throws DataException;

    Request getLatestRequest() throws DataException;

    List<Request> getRequests() throws DataException;

    List<Request> getUnassignedRequests() throws DataException;

    void setRequest(Request request) throws DataException;
    
}