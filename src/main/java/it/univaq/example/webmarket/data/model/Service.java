package it.univaq.example.webmarket.data.model;

import it.univaq.framework.data.DataItem;

public interface Service extends DataItem<Integer> {

    Integer getId();

    String getScript();

    void setScript(String script);

}
