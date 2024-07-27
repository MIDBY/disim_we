package it.univaq.example.webshop.data.model.impl;

import it.univaq.example.webshop.data.model.Characteristic;
import it.univaq.example.webshop.data.model.Request;
import it.univaq.example.webshop.data.model.RequestCharacteristic;
import it.univaq.framework.data.DataItemImpl;

public class RequestCharacteristicImpl extends DataItemImpl<Integer> implements RequestCharacteristic {

    private Request request;
    private Characteristic characteristic;
    private String value;

    public RequestCharacteristicImpl() {
        super();
        request = new RequestImpl();
        characteristic = new CharacteristicImpl();
        value = "";
    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public Characteristic getCharacteristic() {
        return characteristic;
    }

    @Override
    public void setCharacteristic(Characteristic characteristic) {
        this.characteristic = characteristic;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    
    
}
