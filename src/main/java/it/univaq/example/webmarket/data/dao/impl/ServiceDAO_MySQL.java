package it.univaq.example.webmarket.data.dao.impl;

import it.univaq.example.webmarket.data.dao.ServiceDAO;
import it.univaq.example.webmarket.data.model.Service;
import it.univaq.example.webmarket.data.model.impl.proxy.ServiceProxy;
import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import it.univaq.framework.data.DataLayer;
import it.univaq.framework.data.OptimisticLockException;

public class ServiceDAO_MySQL extends DAO implements ServiceDAO {

    private PreparedStatement sServiceByID, sServices, sServicesByGroup, iService, uService, dService;

    public ServiceDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sServiceByID = connection.prepareStatement("SELECT * FROM servizio WHERE id=?");
            sServices = connection.prepareStatement("SELECT id FROM servizio");
            sServicesByGroup = connection.prepareStatement("SELECT id_servizio FROM gruppo_servizio where id_gruppo=?");
            iService = connection.prepareStatement("INSERT INTO servizio (script) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            uService = connection.prepareStatement("UPDATE servizio SET script=?, versione=? WHERE id=? and versione=?");
            dService = connection.prepareStatement("DELETE FROM servizio WHERE id=?");
        } catch (SQLException ex) {
            throw new DataException("Error initializing webshop data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            sServiceByID.close();
            sServices.close();
            sServicesByGroup.close();
            iService.close();
            uService.close();
            dService.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        super.destroy();
    }

    @Override
    public Service createService() {
        return new ServiceProxy(getDataLayer());
    }

    private ServiceProxy createService(ResultSet rs) throws DataException {
        try {
            ServiceProxy a = (ServiceProxy)createService();
            a.setKey(rs.getInt("id"));
            a.setScript(rs.getString("script"));
            a.setVersion(rs.getLong("versione"));
            return a;
        } catch (SQLException ex) {
            throw new DataException("Unable to create service object form ResultSet", ex);
        }
    }

    @Override
    public Service getService(int service_key) throws DataException {
        Service a = null;
        if (dataLayer.getCache().has(Service.class, service_key)) {
            a = dataLayer.getCache().get(Service.class, service_key);
        } else {
            try {
                sServiceByID.setInt(1, service_key);
                try (ResultSet rs = sServiceByID.executeQuery()) {
                    if (rs.next()) {
                        a = createService(rs);
                        dataLayer.getCache().add(Service.class, a);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load service by ID", ex);
            }
        }
        return a;
    }

    @Override
    public List<Service> getServices() throws DataException {
        List<Service> result = new ArrayList<Service>();

        try (ResultSet rs = sServices.executeQuery()) {
            while (rs.next()) {
                result.add(getService(rs.getInt("id")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load services", ex);
        }
        return result;
    }

    @Override
    public List<Service> getServicesByGroup(int group_key) throws DataException {
        List<Service> result = new ArrayList<Service>();
        try {
            sServicesByGroup.setInt(1, group_key);
            try (ResultSet rs = sServicesByGroup.executeQuery()) {
                while (rs.next()) {
                    result.add(getService(rs.getInt("id")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load services by group", ex);
        }
        return result;
    }

        @Override
    public void setService(Service service) throws DataException {
        try {
            if (service.getKey() != null && service.getKey() > 0) { //update
                if (service instanceof DataItemProxy && !((DataItemProxy) service).isModified()) {
                    return;
                }
                uService.setString(1, service.getScript());

                long current_version = service.getVersion();
                long next_version = current_version + 1;

                uService.setLong(2, next_version);
                uService.setInt(3, service.getKey());
                uService.setLong(4, current_version);

                if (uService.executeUpdate() == 0) {
                    throw new OptimisticLockException(service);
                } else {
                    service.setVersion(next_version);
                }
            } else { //insert
                iService.setString(1, service.getScript());

                if (iService.executeUpdate() == 1) {
                    try ( ResultSet keys = iService.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            service.setKey(key);
                            dataLayer.getCache().add(Service.class, service);
                        }
                    }
                }
            }

            if (service instanceof DataItemProxy) {
                ((DataItemProxy) service).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store service", ex);
        }
    }

    @Override
    public void deleteService(Service service) throws DataException {
        try {
            if (service.getKey() != null && service.getKey() > 0) { //delete
                dService.setInt(1, service.getKey());
                if (dService.executeUpdate() == 0) {
                    throw new OptimisticLockException(service);
                } else {
                    dataLayer.getCache().delete(Service.class, service);
                }
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to delete service", ex);
        }
    }
}