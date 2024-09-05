package it.univaq.example.webshop.data.model.impl;

import it.univaq.example.webshop.data.model.Service;
import it.univaq.framework.data.DataItemImpl;

public class ServiceImpl extends DataItemImpl<Integer> implements Service {
    
    private String script;

    public ServiceImpl() {
        super();
        script = "";
    }

    @Override
    public String getScript() {
        return script;
    }

    @Override
    public void setScript(String script) {
        this.script = script;
    }
    

}
