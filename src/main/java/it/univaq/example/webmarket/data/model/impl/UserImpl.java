package it.univaq.example.webmarket.data.model.impl;

import it.univaq.example.webmarket.data.model.User;
import it.univaq.framework.data.DataItemImpl;

public class UserImpl extends DataItemImpl<Integer> implements User {

    private Integer id;
    private String username;
    private String email;
    private String password;
    private Boolean accepted;

    public UserImpl() {
        super();
        username = "";
        email = "";
        password = "";
        accepted = false;
    }

        /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

        /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

        /**
     * @return is accpeted
     */
    public Boolean isAccepted() {
        return accepted;
    }

    /**
     * @param accepted the accepted to set
     */
    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

}