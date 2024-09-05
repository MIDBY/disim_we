package it.univaq.example.webshop.data.model;

import it.univaq.framework.data.DataItem;

public interface Service extends DataItem<Integer> {

    String getScript();

    void setScript(String script);
}

 
