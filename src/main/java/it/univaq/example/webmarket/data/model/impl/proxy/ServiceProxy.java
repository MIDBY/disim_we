package it.univaq.example.webmarket.data.model.impl.proxy;

import it.univaq.example.webmarket.data.model.impl.ServiceImpl;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;


public class ServiceProxy extends ServiceImpl implements DataItemProxy  {

    protected boolean modified;
    protected DataLayer dataLayer;

    public ServiceProxy(DataLayer d) {
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
    public void setScript(String script) {
        super.setScript(script);
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