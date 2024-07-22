package it.univaq.example.webmarket.data.model;

import it.univaq.framework.data.DataItem;

public interface Category extends DataItem<Integer> {

    Integer getId();

    String getName();

    void setName(String name);

    Category getFatherCategory();

    void setFatherCategory(Category fathCategory);
    
}
