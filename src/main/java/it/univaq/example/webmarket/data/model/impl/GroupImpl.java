package it.univaq.example.webmarket.data.model.impl;

import it.univaq.example.webmarket.data.model.Group;
import it.univaq.framework.data.DataItemImpl;

public class GroupImpl extends DataItemImpl<Integer> implements Group {
    
    private Integer id;
    private UserRoleEnum name;

    public GroupImpl() {
        super();
        name = UserRoleEnum.ORDINANTE;
    }

    @Override
    public Integer getId() {
        return id;
    }
    
    @Override
    public UserRoleEnum getName() {
        return name;
    }

    @Override
    public void setName(UserRoleEnum name) {
        this.name = name;
    }
}
