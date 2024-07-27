package it.univaq.example.webshop.data.model.impl.proxy;

import java.util.logging.Level;
import java.util.logging.Logger;

import it.univaq.example.webshop.data.dao.CharacteristicDAO;
import it.univaq.example.webshop.data.dao.RequestDAO;
import it.univaq.example.webshop.data.model.Characteristic;
import it.univaq.example.webshop.data.model.Request;
import it.univaq.example.webshop.data.model.impl.RequestCharacteristicImpl;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;


public class RequestCharacteristicProxy extends RequestCharacteristicImpl implements DataItemProxy  {

    protected boolean modified;
    protected DataLayer dataLayer;
    protected int request_key;
    protected int characteristic_key;

    public RequestCharacteristicProxy(DataLayer d) {
        super();
        //dependency injection
        this.dataLayer = d;
        this.modified = false;
        request_key = 0;
        characteristic_key = 0;
    }

  
    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }


    @Override
    public void setValue(String value) {
        super.setValue(value);
        this.modified = true;
    }

@Override
    public Request getRequest() {
        if (super.getRequest() == null && request_key > 0) {
            try {
                super.setRequest(((RequestDAO) dataLayer.getDAO(Request.class)).getRequest(request_key));
            } catch (DataException ex) {
                Logger.getLogger(RequestCharacteristicProxy.class.getName()).log(Level.SEVERE, null, ex);
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
    public Characteristic getCharacteristic() {
        if (super.getCharacteristic() == null && characteristic_key > 0) {
            try {
                super.setCharacteristic(((CharacteristicDAO) dataLayer.getDAO(Characteristic.class)).getCharacteristic(characteristic_key));
            } catch (DataException ex) {
                Logger.getLogger(RequestCharacteristicProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getCharacteristic();
    }

    @Override
    public void setCharacteristic(Characteristic characteristic) {
        super.setCharacteristic(characteristic);
        this.characteristic_key = characteristic.getKey();
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

    public void setCharacteristicKey(int characteristic_key) {
        this.characteristic_key = characteristic_key;
        super.setCharacteristic(null);
    }
}