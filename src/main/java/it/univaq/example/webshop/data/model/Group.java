package it.univaq.example.webshop.data.model;

import java.util.List;

import it.univaq.example.webshop.data.model.impl.UserRoleEnum;
import it.univaq.framework.data.DataItem;

public interface Group extends DataItem<Integer> {

    UserRoleEnum getName();

    void setName(UserRoleEnum name);

    List<User> getUsers();

    void setUsers(List<User> users);

    void addUser(User user);

    List<Service> getServices();

    void setServices(List<Service> services);

    void addService(Service service);

}

 
