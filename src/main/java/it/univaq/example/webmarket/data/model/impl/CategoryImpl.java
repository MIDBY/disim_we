package it.univaq.example.webmarket.data.model.impl;

import it.univaq.example.webmarket.data.model.Category;
import it.univaq.framework.data.DataItemImpl;

public class CategoryImpl extends DataItemImpl<Integer> implements Category {

    private Integer id;
    private String name;
    private Category fatherCategory;


    public CategoryImpl() {
        super();
        name = "";
        fatherCategory = new CategoryImpl();
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
    public Category getFatherCategory() {
        return fatherCategory;
    }

    @Override
    public void setFatherCategory(Category fathCategory) {
        fatherCategory = fathCategory;
    }


    
}
