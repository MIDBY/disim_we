package it.univaq.example.webshop.data.model.impl.proxy;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.univaq.example.webshop.data.dao.UserDAO;
import it.univaq.example.webshop.data.model.User;
import it.univaq.example.webshop.data.model.impl.NotificationImpl;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;


public class NotificationProxy extends NotificationImpl implements DataItemProxy  {

    protected boolean modified;
    protected DataLayer dataLayer;
    protected int recipient_key;

    public NotificationProxy(DataLayer d) {
        super();
        //dependency injection
        this.dataLayer = d;
        this.modified = false;
        recipient_key = 0;
    }

  
    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public User getRecipient() {
        if (super.getRecipient() == null && recipient_key > 0) {
            try {
                super.setRecipient(((UserDAO) dataLayer.getDAO(User.class)).getUser(recipient_key));
            } catch (DataException ex) {
                Logger.getLogger(NotificationProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getRecipient();
    }


    @Override
    public void setRecipient(User recipient) {
        super.setRecipient(recipient);
        this.recipient_key = recipient.getKey();
        this.modified = true;
    }

    @Override
    public void setMessage(String message) {
        super.setMessage(message);
        this.modified = true;
    }

    @Override
    public void setCreationDate(LocalDate creationDate) {
        super.setCreationDate(creationDate);
        this.modified = true;
    }

    @Override
    public void setRead(boolean read) {
        super.setRead(read);
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
    
    public void setRecipientKey(int recipient_key) {
        this.recipient_key = recipient_key;
        super.setRecipient(null);
    }

}