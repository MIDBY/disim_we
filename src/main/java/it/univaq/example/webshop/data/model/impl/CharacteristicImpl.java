package it.univaq.example.webshop.data.model.impl;

import it.univaq.example.webshop.data.model.Category;
import it.univaq.example.webshop.data.model.Characteristic;
import it.univaq.framework.data.DataItemImpl;

public class CharacteristicImpl extends DataItemImpl<Integer> implements Characteristic {

    private String name;
    private Category category;
    private String defaultValues;

    public CharacteristicImpl() {
        super();
        name = "";
        category = null;
        defaultValues = "";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Category getCategory(){
        return category;
    }

    @Override
    public void setCategory(Category category){
        this.category = category;
    }

    @Override
    public String getDefaultValues() {
        return defaultValues;
    }

    @Override
    public void setDefaultValues(String values) {
        defaultValues = values;
    }
    
}
