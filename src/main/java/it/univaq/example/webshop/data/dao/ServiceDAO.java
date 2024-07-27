package it.univaq.example.webshop.data.dao;

import it.univaq.example.webshop.data.model.Service;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface ServiceDAO {

    Service createService();

    Service getService(int service_key) throws DataException;

    List<Service> getServices() throws DataException;

    List<Service> getServicesByGroup(int group_key) throws DataException;

    void setService(Service service) throws DataException;

    void deleteService(Service service) throws DataException;

}