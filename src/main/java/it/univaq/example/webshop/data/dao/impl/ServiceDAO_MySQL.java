package it.univaq.example.webshop.data.dao.impl;

import it.univaq.example.webshop.data.dao.ServiceDAO;
import it.univaq.example.webshop.data.model.Service;
import it.univaq.example.webshop.data.model.impl.proxy.ServiceProxy;
import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import it.univaq.framework.data.DataLayer;

public class ServiceDAO_MySQL extends DAO implements ServiceDAO {

    private PreparedStatement sServiceByID, sServiceByScript, sServiceByGroup;

    public ServiceDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sServiceByID = connection.prepareStatement("SELECT * FROM servizio WHERE id=?");
            sServiceByScript = connection.prepareStatement("SELECT id FROM servizio WHERE script=?");
            sServiceByGroup = connection.prepareStatement("SELECT idServizio FROM gruppo_servizio where idGruppo=?");            
        } catch (SQLException ex) {
            throw new DataException("Error initializing webshop data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            sServiceByID.close();
            sServiceByScript.close();
            sServiceByGroup.close();
            
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
            ServiceProxy a = (ServiceProxy) createService();
            a.setKey(rs.getInt("id"));
            a.setScript(rs.getString("script"));
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
    public Service getServiceByScript(String script) throws DataException {
        try {
            sServiceByScript.setString(1, script);
            try ( ResultSet rs = sServiceByScript.executeQuery()) {
                if (rs.next()) {
                    return getService(rs.getInt("id"));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to find service by script", ex);
        }
        return null;
    }

    @Override
    public List<Service> getServicesByGroup(int group_key) throws DataException {
        List<Service> result = new ArrayList<Service>();
        try {
            sServiceByGroup.setInt(1, group_key);
            try (ResultSet rs = sServiceByGroup.executeQuery()) {
                while (rs.next()) {
                    result.add(getService(rs.getInt("idServizio")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load services by group", ex);
        }
        return result;
    }



}