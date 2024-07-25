package it.univaq.example.webmarket.data.dao.impl;

import it.univaq.example.webmarket.data.dao.CharacteristicDAO;
import it.univaq.example.webmarket.data.model.Characteristic;
import it.univaq.example.webmarket.data.model.impl.proxy.CharacteristicProxy;
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

public class CharacteristicDAO_MySQL extends DAO implements CharacteristicDAO {

    private PreparedStatement sCharacteristicByID, sCharacteristics, sCharacteristicsByCategory, iCharacteristic, uCharacteristic, dCharacteristic;

    public CharacteristicDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sCharacteristicByID = connection.prepareStatement("SELECT * FROM caratteristica WHERE id=?");
            sCharacteristicsByCategory = connection.prepareStatement("SELECT id FROM caratteristica WHERE id_categoria=?");
            sCharacteristics = connection.prepareStatement("SELECT id FROM caratteristica");
            iCharacteristic = connection.prepareStatement("INSERT INTO caratteristica (nome,id_categoria,valori_default) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
            uCharacteristic = connection.prepareStatement("UPDATE caratteristica SET nome=?,id_categoria=?,valori_default=?,versione=? WHERE id=? and versione=?");
            dCharacteristic = connection.prepareStatement("DELETE FROM caratteristica WHERE id=?");

        } catch (SQLException ex) {
            throw new DataException("Error initializing webshop data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        //anche chiudere i PreparedStamenent � una buona pratica...
        //also closing PreparedStamenents is a good practice...
        try {

            sCharacteristicByID.close();
            sCharacteristicsByCategory.close();
            sCharacteristics.close();
            iCharacteristic.close();
            uCharacteristic.close();
            dCharacteristic.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        super.destroy();
    }

    @Override
    public Characteristic createCharacteristic() {
        return new CharacteristicProxy(getDataLayer());
    }

    //helper
    private CharacteristicProxy createCharacteristic(ResultSet rs) throws DataException {
        CharacteristicProxy a = (CharacteristicProxy) createCharacteristic();
        try {
            a.setKey(rs.getInt("id"));
            a.setName(rs.getString("nome"));
            a.setCategoryKey(rs.getInt("id_categoria"));
            a.setDefaultValues(rs.getString("valori_default"));
            a.setVersion(rs.getLong("versione"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create characteristic object form ResultSet", ex);
        }
        return a;
    }

    @Override
    public Characteristic getCharacteristic(int characteristic_key) throws DataException {
        Characteristic a = null;
        //prima vediamo se l'oggetto è già stato caricato
        //first look for this object in the cache
        if (dataLayer.getCache().has(Characteristic.class, characteristic_key)) {
            a = dataLayer.getCache().get(Characteristic.class, characteristic_key);
        } else {
            //altrimenti lo carichiamo dal database
            //otherwise load it from database
            try {
                sCharacteristicByID.setInt(1, characteristic_key);
                try (ResultSet rs = sCharacteristicByID.executeQuery()) {
                    if (rs.next()) {
                        a = createCharacteristic(rs);
                        //e lo mettiamo anche nella cache
                        //and put it also in the cache
                        dataLayer.getCache().add(Characteristic.class, a);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load characteristic by ID", ex);
            }
        }
        return a;
    }

    @Override
    public List<Characteristic> getCharacteristics(int category_key) throws DataException {
        List<Characteristic> result = new ArrayList<Characteristic>();

        try {
            sCharacteristicsByCategory.setInt(1, category_key);
            try (ResultSet rs = sCharacteristicsByCategory.executeQuery()) {
                while (rs.next()) {
                    result.add((Characteristic) getCharacteristic(rs.getInt("id")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load characteristics by category", ex);
        }
        return result;
    }

    @Override
    public List<Characteristic> getCharacteristics() throws DataException {
        List<Characteristic> result = new ArrayList<Characteristic>();

        try (ResultSet rs = sCharacteristics.executeQuery()) {
            while (rs.next()) {
                result.add((Characteristic) getCharacteristic(rs.getInt("id")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load characteristics", ex);
        }
        return result;
    }

    @Override
    public void setCharacteristic(Characteristic characteristic) throws DataException {
        try {
            if (characteristic.getKey() != null && characteristic.getKey() > 0) { //update
                //non facciamo nulla se l'oggetto è un proxy e indica di non aver subito modifiche
                //do not store the object if it is a proxy and does not indicate any modification
                if (characteristic instanceof DataItemProxy && !((DataItemProxy) characteristic).isModified()) {
                    return;
                }
                uCharacteristic.setString(1, characteristic.getName());
                if (characteristic.getCategory() != null) {
                    uCharacteristic.setInt(2, characteristic.getCategory().getKey());
                } else {
                    uCharacteristic.setNull(2, java.sql.Types.INTEGER);
                }                
                uCharacteristic.setString(3, characteristic.getDefaultValues());

                long current_version = characteristic.getVersion();
                long next_version = current_version + 1;

                uCharacteristic.setLong(4, next_version);
                uCharacteristic.setInt(5, characteristic.getKey());
                uCharacteristic.setLong(6, current_version);

                if (uCharacteristic.executeUpdate() == 0) {
                    throw new OptimisticLockException(characteristic);
                } else {
                    characteristic.setVersion(next_version);
                }
            } else { //insert
                iCharacteristic.setString(1, characteristic.getName());
                if (characteristic.getCategory() != null) {
                    iCharacteristic.setInt(2, characteristic.getCategory().getKey());
                } else {
                    iCharacteristic.setNull(2, java.sql.Types.INTEGER);
                }
                iCharacteristic.setString(3, characteristic.getDefaultValues());

                if (iCharacteristic.executeUpdate() == 1) {
                    try (ResultSet keys = iCharacteristic.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            characteristic.setKey(key);
                            dataLayer.getCache().add(Characteristic.class, characteristic);
                        }
                    }
                }
            }
            if (characteristic instanceof DataItemProxy) {
                ((DataItemProxy) characteristic).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store characteristic", ex);
        }
    }
}