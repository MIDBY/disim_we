package it.univaq.example.webmarket.data.model.impl.proxy;

import it.univaq.example.webmarket.data.dao.RequestDAO;
import it.univaq.example.webmarket.data.dao.UserDAO;
import it.univaq.example.webmarket.data.model.impl.ProposalImpl;
import it.univaq.example.webmarket.data.model.impl.ProposalStateEnum;
import it.univaq.example.webmarket.data.model.Request;
import it.univaq.example.webmarket.data.model.User;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProposalProxy extends ProposalImpl implements DataItemProxy {

    protected boolean modified;
    protected int request_key = 0;
    protected int technician_key = 0;

    protected DataLayer dataLayer;

    public ProposalProxy(DataLayer d) {
        super();
        //dependency injection
        this.dataLayer = d;
        this.modified = false;
        this.request_key = 0;
        this.technician_key = 0;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public Request getRequest() {
        if (super.getRequest() == null && request_key > 0) {
            try {
                super.setRequest(((RequestDAO) dataLayer.getDAO(Request.class)).getRequest(request_key));
            } catch (DataException ex) {
                Logger.getLogger(ProposalProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getRequest();
    }

    @Override
    public void setRequest(Request request) {
        super.setRequest(request);
        this.request_key = request.getKey();
        this.modified = true;
    }

    @Override
    public User getTechnician() {
        if (super.getTechnician() == null && technician_key > 0) {
            try {
                super.setTechnician(((UserDAO) dataLayer.getDAO(User.class)).getUser(technician_key));
            } catch (DataException ex) {
                Logger.getLogger(ProposalProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getTechnician();
    }

    @Override
    public void setTechnician(User technician) {
        super.setTechnician(technician);
        this.technician_key = technician.getKey();
        this.modified = true;
    }

    @Override
    public void setProductName(String productName) {
        super.setProductName(productName);
        this.modified = true;
    }

    @Override
    public void setProducerName(String producerName) {
        super.setProductName(producerName);
        this.modified = true;
    }

    @Override
    public void setProductDescription(String description) {
        super.setProductDescription(description);
        this.modified = true;
    }

    @Override
    public void setProductPrice(Float price) {
        super.setProductPrice(price);
        this.modified = true;
    }

    @Override
    public boolean setUrl(String url) {
        this.modified = true;        
        return super.setUrl(url);
    }

    @Override
    public void setNotes(String notes) {
        super.setNotes(notes);
        this.modified = true;
    }

    @Override
    public void setCreationDate(LocalDate date) {
        super.setCreationDate(date);
        this.modified = true;
    }

    @Override
    public void setProposalState(ProposalStateEnum value) {
        super.setProposalState(value);
        this.modified = true;
    }

    @Override
    public void setMotivation(String motivation) {
        super.setMotivation(motivation);
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

    public void setRequestKey(int request_key) {
        this.request_key = request_key;
        super.setRequest(null);
    }

    public void setTechnicianKey(int technician_key) {
        this.technician_key = technician_key;
        super.setTechnician(null);
    }
}