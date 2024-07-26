package it.univaq.example.webmarket.data.dao.impl;

import it.univaq.example.webmarket.data.dao.GroupDAO;
import it.univaq.example.webmarket.data.model.Group;
import it.univaq.example.webmarket.data.model.impl.GroupImpl;
import it.univaq.example.webmarket.data.model.impl.UserRoleEnum;
import it.univaq.example.webmarket.data.model.impl.proxy.GroupProxy;
import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import it.univaq.framework.data.DataLayer;

public class GroupDAO_MySQL extends DAO implements GroupDAO {

    private PreparedStatement sGroupByID, sGroups, sGroupByName;

    public GroupDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sGroupByID = connection.prepareStatement("SELECT * FROM gruppo WHERE id=?");
            sGroups = connection.prepareStatement("SELECT id FROM gruppo");
            sGroupByName = connection.prepareStatement("SELECT id FROM gruppo where nome=?");
            
        } catch (SQLException ex) {
            throw new DataException("Error initializing webshop data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            sGroupByID.close();
            sGroups.close();
            sGroupByName.close();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        super.destroy();
    }

    @Override
    public Group createGroup() {
        return new GroupProxy(getDataLayer());
    }

    private GroupProxy createGroup(ResultSet rs) throws DataException {
        try {
            GroupProxy a = (GroupProxy)createGroup();
            a.setKey(rs.getInt("id"));
            a.setName(rs.getObject("nome", UserRoleEnum.class));
            a.setVersion(rs.getLong("versione"));
            return a;
        } catch (SQLException ex) {
            throw new DataException("Unable to create group object form ResultSet", ex);
        }
    }

    @Override
    public Group getGroup(int group_key) throws DataException {
        Group a = null;
        if (dataLayer.getCache().has(Group.class, group_key)) {
            a = dataLayer.getCache().get(Group.class, group_key);
        } else {
            try {
                sGroupByID.setInt(1, group_key);
                try (ResultSet rs = sGroupByID.executeQuery()) {
                    if (rs.next()) {
                        a = createGroup(rs);
                        dataLayer.getCache().add(Group.class, a);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load group by ID", ex);
            }
        }
        return a;
    }

    @Override
    public List<Group> getGroups() throws DataException {
        List<Group> result = new ArrayList<Group>();

        try (ResultSet rs = sGroups.executeQuery()) {
            while (rs.next()) {
                result.add(getGroup(rs.getInt("id")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load groups", ex);
        }
        return result;
    }

     @Override
    public Group getGroupByName(UserRoleEnum value) throws DataException {
        Group result = new GroupImpl();
        try {
            sGroupByName.setString(1, value.toString());
            try (ResultSet rs = sGroupByName.executeQuery()) {
                while(rs.next()) {
                    result = getGroup(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            throw new DataException("Unable to load group by name", e);
        }
        return result;
    }

}