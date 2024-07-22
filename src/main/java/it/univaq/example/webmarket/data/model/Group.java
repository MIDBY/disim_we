package it.univaq.example.webmarket.data.model;

import it.univaq.example.webmarket.data.model.impl.UserRoleEnum;
import it.univaq.framework.data.DataItem;

public interface Group extends DataItem<Integer> {

    Integer getId();

    UserRoleEnum getName();

    void setName(UserRoleEnum value);

}

 
