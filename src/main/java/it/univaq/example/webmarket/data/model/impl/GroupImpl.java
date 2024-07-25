package it.univaq.example.webmarket.data.model.impl;

import java.util.List;
import it.univaq.example.webmarket.data.model.Group;
import it.univaq.example.webmarket.data.model.Service;
import it.univaq.example.webmarket.data.model.User;
import it.univaq.framework.data.DataItemImpl;

public class GroupImpl extends DataItemImpl<Integer> implements Group {
    
    private UserRoleEnum name;
    private List<User> users;
    private List<Service> services;

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

    @Override
    public List<Service> getServices() {
        return services;
    }

    @Override
    public void setServices(List<Service> services) {
        this.services = services;
    }
}
