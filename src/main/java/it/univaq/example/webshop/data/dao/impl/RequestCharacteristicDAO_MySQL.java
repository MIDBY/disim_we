package it.univaq.example.webshop.data.dao.impl;

import it.univaq.example.webshop.data.dao.RequestCharacteristicDAO;
import it.univaq.example.webshop.data.model.RequestCharacteristic;
import it.univaq.example.webshop.data.model.impl.proxy.RequestCharacteristicProxy;
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

public class RequestCharacteristicDAO_MySQL extends DAO implements RequestCharacteristicDAO {

    private PreparedStatement sRequestCharacteristicByID, sRequestCharacteristicsByRequest, iRequestCharacteristic, uRequestCharacteristic, dRequestCharacteristic;

    public RequestCharacteristicDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sRequestCharacteristicByID = connection.prepareStatement("SELECT * FROM richiesta_caratteristica WHERE id=?");
            sRequestCharacteristicsByRequest = connection.prepareStatement("SELECT id FROM richiesta_caratteristica where idRichiesta=?");
            iRequestCharacteristic = connection.prepareStatement("INSERT INTO richiesta_caratteristica (idRichiesta,idCaratteristica,valore) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            uRequestCharacteristic = connection.prepareStatement("UPDATE richiesta_caratteristica SET idRichiesta=?,idCaratteristica=?,valore=?,versione=? WHERE id=? and versione=?");
            dRequestCharacteristic = connection.prepareStatement("DELETE FROM richiesta_caratteristica WHERE id=?");
        } catch (SQLException ex) {
            throw new DataException("Error initializing webshop data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            sRequestCharacteristicByID.close();
            sRequestCharacteristicsByRequest.close();
            iRequestCharacteristic.close();
            uRequestCharacteristic.close();
            dRequestCharacteristic.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        super.destroy();
    }

    @Override
    public RequestCharacteristic createRequestCharacteristic() {
        return new RequestCharacteristicProxy(getDataLayer());
    }

    private RequestCharacteristicProxy createRequestCharacteristic(ResultSet rs) throws DataException {
        try {
            RequestCharacteristicProxy a = (RequestCharacteristicProxy)createRequestCharacteristic();
            a.setKey(rs.getInt("id"));
            a.setRequestKey(rs.getInt("idRichiesta"));
            a.setCharacteristicKey(rs.getInt("idCaratteristica"));
            a.setValue(rs.getString("valore"));
            a.setVersion(rs.getLong("versione"));
            return a;
        } catch (SQLException ex) {
            throw new DataException("Unable to create request characteristic object form ResultSet", ex);
        }
    }

    @Override
    public RequestCharacteristic getRequestCharacteristic(int requestCharacteristic_key) throws DataException {
        RequestCharacteristic a = null;
        if (dataLayer.getCache().has(RequestCharacteristic.class, requestCharacteristic_key)) {
            a = dataLayer.getCache().get(RequestCharacteristic.class, requestCharacteristic_key);
        } else {
            try {
                sRequestCharacteristicByID.setInt(1, requestCharacteristic_key);
                try (ResultSet rs = sRequestCharacteristicByID.executeQuery()) {
                    if (rs.next()) {
                        a = createRequestCharacteristic(rs);
                        dataLayer.getCache().add(RequestCharacteristic.class, a);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load request characteristic by ID", ex);
            }
        }
        return a;
    }

    @Override
    public List<RequestCharacteristic> getRequestCharacteristicsByRequest(int request_key) throws DataException {
        List<RequestCharacteristic> result = new ArrayList<RequestCharacteristic>();
        try {
            sRequestCharacteristicsByRequest.setInt(1, request_key);
            try (ResultSet rs = sRequestCharacteristicsByRequest.executeQuery()) {
                while (rs.next()) {
                    result.add(getRequestCharacteristic(rs.getInt("id")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load request characteristics by request", ex);
        }
        return result;
    }

        @Override
    public void setRequestCharacteristic(RequestCharacteristic requestCharacteristic) throws DataException {
        try {
            if (requestCharacteristic.getKey() != null && requestCharacteristic.getKey() > 0) { //update
                if (requestCharacteristic instanceof DataItemProxy && !((DataItemProxy) requestCharacteristic).isModified()) {
                    return;
                }
                if (requestCharacteristic.getRequest() != null) {
                    uRequestCharacteristic.setInt(1, requestCharacteristic.getRequest().getKey());
                } else {
                    uRequestCharacteristic.setNull(1, java.sql.Types.INTEGER);
                }  
                if (requestCharacteristic.getCharacteristic() != null) {
                    uRequestCharacteristic.setInt(2, requestCharacteristic.getCharacteristic().getKey());
                } else {
                    uRequestCharacteristic.setNull(2, java.sql.Types.INTEGER);
                }  
                uRequestCharacteristic.setString(3, requestCharacteristic.getValue());

                long current_version = requestCharacteristic.getVersion();
                long next_version = current_version + 1;

                uRequestCharacteristic.setLong(4, next_version);
                uRequestCharacteristic.setInt(5, requestCharacteristic.getKey());
                uRequestCharacteristic.setLong(6, current_version);

                if (uRequestCharacteristic.executeUpdate() == 0) {
                    throw new OptimisticLockException(requestCharacteristic);
                } else {
                    requestCharacteristic.setVersion(next_version);
                }
            } else { //insert
                if (requestCharacteristic.getRequest() != null) {
                    iRequestCharacteristic.setInt(1, requestCharacteristic.getRequest().getKey());
                } else {
                    iRequestCharacteristic.setNull(1, java.sql.Types.INTEGER);
                }  
                if (requestCharacteristic.getCharacteristic() != null) {
                    iRequestCharacteristic.setInt(2, requestCharacteristic.getCharacteristic().getKey());
                } else {
                    iRequestCharacteristic.setNull(2, java.sql.Types.INTEGER);
                }  
                iRequestCharacteristic.setString(3, requestCharacteristic.getValue());
                if (iRequestCharacteristic.executeUpdate() == 1) {
                    try ( ResultSet keys = iRequestCharacteristic.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            requestCharacteristic.setKey(key);
                            dataLayer.getCache().add(RequestCharacteristic.class, requestCharacteristic);
                        }
                    }
                }
            }

            if (requestCharacteristic instanceof DataItemProxy) {
                ((DataItemProxy) requestCharacteristic).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store request characteristic", ex);
        }
    }

    @Override
    public void deleteRequestCharacteristic(RequestCharacteristic requestCharacteristic) throws DataException {
        try {
            if (requestCharacteristic.getKey() != null && requestCharacteristic.getKey() > 0) { //delete
                dRequestCharacteristic.setInt(1, requestCharacteristic.getKey());
                if (dRequestCharacteristic.executeUpdate() == 0) {
                    throw new OptimisticLockException(requestCharacteristic);
                } else {
                    dataLayer.getCache().delete(RequestCharacteristic.class, requestCharacteristic);
                }
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to delete request characteristic", ex);
        }
    }
}