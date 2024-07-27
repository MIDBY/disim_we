package it.univaq.example.webshop.data.model;

import it.univaq.framework.data.DataItem;

public interface Characteristic extends DataItem<Integer> {

    String getName();

    void setName(String nome);

    Category getCategory();

    void setCategory(Category category);

    String getDefaultValues();

    void setDefaultValues(String values);
    
}
