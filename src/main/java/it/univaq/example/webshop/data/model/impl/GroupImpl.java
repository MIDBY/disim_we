package it.univaq.example.webshop.data.model.impl;

import java.util.List;

import it.univaq.example.webshop.data.model.Group;
import it.univaq.example.webshop.data.model.User;
import it.univaq.framework.data.DataItemImpl;

public class GroupImpl extends DataItemImpl<Integer> implements Group {
    
    private UserRoleEnum name;
    private List<User> users;

    public GroupImpl() {
        super();
        name = UserRoleEnum.ORDINANTE;
        users = null;
    }
    
    @Override
    public UserRoleEnum getName() {
        return name;
    }

    @Override
    public void setName(UserRoleEnum name) {
        this.name = name;
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public void addUser(User user) {
        users.add(user);
    }
}
