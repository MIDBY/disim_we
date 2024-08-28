package it.univaq.example.webshop.data.model.impl.proxy;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import it.univaq.example.webshop.data.dao.UserDAO;
import it.univaq.example.webshop.data.model.User;
import it.univaq.example.webshop.data.model.impl.GroupImpl;
import it.univaq.example.webshop.data.model.impl.UserRoleEnum;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;


public class GroupProxy extends GroupImpl implements DataItemProxy  {

    protected boolean modified;
    protected DataLayer dataLayer;

    public GroupProxy(DataLayer d) {
        super();
        //dependency injection
        this.dataLayer = d;
        this.modified = false;
    }

  
    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public void setName(UserRoleEnum name) {
        super.setName(name);
        this.modified = true;
    }

    @Override
    public List<User> getUsers() {
        if (super.getUsers() == null) {
            try {
                super.setUsers(((UserDAO) dataLayer.getDAO(User.class)).getUsersByGroup(this.getKey()));
            } catch (DataException ex) {
                Logger.getLogger(GroupProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getUsers();
    }

    @Override
    public void setUsers(List<User> users) {
        super.setUsers(users);
        this.modified = true;
    }

    @Override
    public void addUser(User user) {
        super.addUser(user);
        this.modified = true;
    }

    //METODI DEL PROXY
    //PROXY-ONLY METHODS

    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

}