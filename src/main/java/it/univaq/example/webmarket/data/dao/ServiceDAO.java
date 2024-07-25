package it.univaq.example.webmarket.data.dao;

import it.univaq.example.webmarket.data.model.Service;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface ServiceDAO {

    Service createService();

    Service getService(int service_key) throws DataException;

    List<Service> getServices() throws DataException;

}