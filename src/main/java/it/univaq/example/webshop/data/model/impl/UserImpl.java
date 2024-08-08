package it.univaq.example.webshop.data.model.impl;

import java.time.LocalDate;
import java.util.List;

import it.univaq.example.webshop.data.model.Notification;
import it.univaq.example.webshop.data.model.User;
import it.univaq.framework.data.DataItemImpl;

public class UserImpl extends DataItemImpl<Integer> implements User {

    private String username;
    private String email;
    private String password;
    private String address;
    private LocalDate subscriptionDate;
    private Boolean accepted;
    private List<Notification> notifications;

    public UserImpl() {
        super();
        username = "";
        email = "";
        password = "";
        address = "";
        accepted = false;
        notifications = null;
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
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

        /**
     * @return the subscription date
     */
    public LocalDate getSubscriptionDate() {
        return subscriptionDate;
    }

    /**
     * @param subscriptionDate the subscription date to set
     */
    public void setSubscriptionDate(LocalDate subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
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

    @Override
    public List<Notification> getNotifications() {
        return notifications;
    }


    @Override
    public void setNotifications(List<Notification> notifications) {
        this.notifications.addAll(notifications);
    }

    @Override
    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    @Override
    public void removeNotification(Notification notification) {
        notifications.remove(notification);
    }

    @Override
    public void readNotification(Notification notification) {
        notifications.get(notifications.indexOf(notification)).setRead(true);
    }

}