package it.univaq.example.webshop.data.model.impl.proxy;

import it.univaq.example.webshop.data.dao.CategoryDAO;
import it.univaq.example.webshop.data.dao.ProposalDAO;
import it.univaq.example.webshop.data.dao.RequestCharacteristicDAO;
import it.univaq.example.webshop.data.dao.UserDAO;
import it.univaq.example.webshop.data.model.Category;
import it.univaq.example.webshop.data.model.Proposal;
import it.univaq.example.webshop.data.model.RequestCharacteristic;
import it.univaq.example.webshop.data.model.User;
import it.univaq.example.webshop.data.model.impl.OrderStateEnum;
import it.univaq.example.webshop.data.model.impl.RequestImpl;
import it.univaq.example.webshop.data.model.impl.RequestStateEnum;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestProxy extends RequestImpl implements DataItemProxy {

    protected boolean modified;
    protected int category_key = 0;
    protected int ordering_key = 0;
    protected int technician_key = 0;

    protected DataLayer dataLayer;

    public RequestProxy(DataLayer d) {
        super();
        //dependency injection
        this.dataLayer = d;
        this.modified = false;
        this.category_key = 0;
        this.ordering_key = 0;
        this.technician_key = 0;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        this.modified = true;
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(description);
        this.modified = true;
    }

    @Override
    public Category getCategory() {
        if (super.getCategory() == null && category_key > 0) {
            try {
                super.setCategory(((CategoryDAO) dataLayer.getDAO(Category.class)).getCategory(category_key));
            } catch (DataException ex) {
                Logger.getLogger(RequestProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getCategory();
    }

    @Override
    public void setCategory(Category category) {
        super.setCategory(category);
        this.category_key = category.getKey();
        this.modified = true;
    }

    @Override
    public User getOrdering() {
        if (super.getOrdering() == null && ordering_key > 0) {
            try {
                super.setOrderding(((UserDAO) dataLayer.getDAO(User.class)).getUser(ordering_key));
            } catch (DataException ex) {
                Logger.getLogger(RequestProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getOrdering();
    }

    @Override
    public void setOrderding(User ordering) {
        super.setOrderding(ordering);
        this.ordering_key = ordering.getKey();
        this.modified = true;
    }

    @Override
    public User getTechnician() {
        if (super.getTechnician() == null && technician_key > 0) {
            try {
                super.setTechnician(((UserDAO) dataLayer.getDAO(User.class)).getUser(technician_key));
            } catch (DataException ex) {
                Logger.getLogger(RequestProxy.class.getName()).log(Level.SEVERE, null, ex);
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
    public void setCreationDate(LocalDate date) {
        super.setCreationDate(date);
        this.modified = true;
    }

    @Override
    public void setRequestState(RequestStateEnum value) {
        super.setRequestState(value);
        this.modified = true;
    }

    @Override
    public void setOrderState(OrderStateEnum value) {
        super.setOrderState(value);
        this.modified = true;
    }

    @Override
    public List<RequestCharacteristic> getRequestCharacteristics() {
        if (super.getRequestCharacteristics() == null) {
            try {
                super.setRequestCharacteristics(((RequestCharacteristicDAO) dataLayer.getDAO(RequestCharacteristic.class)).getRequestCharacteristicsByRequest(this.getKey()));
            } catch (DataException ex) {
                Logger.getLogger(RequestProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getRequestCharacteristics();
    }

    @Override
    public void setRequestCharacteristics(List<RequestCharacteristic> requestCharacteristics) {
        super.setRequestCharacteristics(requestCharacteristics);
        this.modified = true;
    }

    @Override
    public void addRequestCharacteristic(RequestCharacteristic requestCharacteristic) {
        super.addRequestCharacteristic(requestCharacteristic);
        this.modified = true;
    }

    @Override
    public List<Proposal> getProposals() {
        if (super.getProposals() == null) {
            try {
                super.setProposals(((ProposalDAO) dataLayer.getDAO(Proposal.class)).getProposalsByRequest(this.getKey()));
            } catch (DataException ex) {
                Logger.getLogger(RequestProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getProposals();
    }

    @Override
    public void setProposals(List<Proposal> proposals) {
        super.setProposals(proposals);
        this.modified = true;
    }

    @Override
    public void addProposal(Proposal proposal) {
        super.addProposal(proposal);
        this.modified = true;
    }

    @Override
    public void setNotes(String notes) {
        super.setNotes(notes);
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

    public void setCategoryKey(int category_key) {
        this.category_key = category_key;
        super.setCategory(null);
    }

    public void setOrderingKey(int ordering_key) {
        this.ordering_key = ordering_key;
        super.setOrderding(null);
    }

    public void setTechnicianKey(int technician_key) {
        this.technician_key = technician_key;
        super.setTechnician(null);
    }
}