package it.univaq.example.webmarket.data.model.impl;

import it.univaq.example.webmarket.data.model.Category;
import it.univaq.example.webmarket.data.model.Characteristic;
import it.univaq.framework.data.DataItemImpl;

public class CharacteristicImpl extends DataItemImpl<Integer> implements Characteristic {

    private Integer id;
    private String name;
    private Category category;
    private String defaultValues;

    public CharacteristicImpl() {
        super();
        name = "";
        category = new CategoryImpl();
        defaultValues = "";
    }

    @Override
    public Integer getId() {
        return id;
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
