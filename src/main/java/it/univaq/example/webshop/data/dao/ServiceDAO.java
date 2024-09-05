package it.univaq.example.webshop.data.dao;

import it.univaq.example.webshop.data.model.Service;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface ServiceDAO {
    
    Service createService();

    Service getService(int service_key) throws DataException;

    Service getServiceByScript(String script) throws DataException;

    List<Service> getServicesByGroup(int group_key) throws DataException;

}