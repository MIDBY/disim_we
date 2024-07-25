package it.univaq.example.webmarket.data.model.impl;

import it.univaq.example.webmarket.data.model.Service;
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
