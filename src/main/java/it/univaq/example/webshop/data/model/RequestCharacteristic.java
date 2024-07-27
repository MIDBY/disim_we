package it.univaq.example.webshop.data.model;

import it.univaq.framework.data.DataItem;

public interface RequestCharacteristic extends DataItem<Integer> {
    
    Request getRequest();

    void setRequest(Request request);

    Characteristic getCharacteristic();

    void setCharacteristic(Characteristic characteristic);

    String getValue();

    void setValue(String value);
}
