package it.univaq.example.webshop.data.model.impl.proxy;

import java.time.LocalDate;

import it.univaq.example.webshop.data.model.impl.UserImpl;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;


public class UserProxy extends UserImpl implements DataItemProxy  {

    protected boolean modified;
    protected DataLayer dataLayer;

    public UserProxy(DataLayer d) {
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
    public void setUsername(String name) {
        super.setUsername(name);
        this.modified = true;
    }

    @Override
    public void setEmail(String email) {
        super.setEmail(email);
        this.modified = true;
    }

    @Override
    public void setPassword(String surname) {
        super.setPassword(surname);
        this.modified = true;
    }

    @Override
    public void setAddress(String address) {
        super.setAddress(address);
        this.modified = true;
    }

    @Override
    public void setSubscriptionDate(LocalDate subscriptionDate) {
        super.setSubscriptionDate(subscriptionDate);
        this.modified = true;
    }

    @Override
    public void setAccepted(boolean accepted) {
        super.setAccepted(accepted);
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