package it.univaq.example.webmarket.data.model;

import it.univaq.framework.data.DataItem;

public interface User extends DataItem<Integer> {

    Integer getId();

    String getUsername();
    
    void setUsername(String username);

    String getEmail();

    void setEmail(String email);

    String getPassword();
    
    void setPassword(String password);

    Boolean isAccepted();

    void setAccepted(Boolean accepted);

}