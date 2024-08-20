package it.univaq.example.webshop.data.model;

import java.time.LocalDate;
import java.util.List;
import it.univaq.framework.data.DataItem;

public interface User extends DataItem<Integer> {

    String getUsername();
    
    void setUsername(String username);

    String getEmail();

    void setEmail(String email);

    String getPassword();
    
    void setPassword(String password);

    String getAddress();

    void setAddress(String address);

    LocalDate getSubscriptionDate();

    void setSubscriptionDate(LocalDate subscriptionDate);

    boolean isAccepted();

    void setAccepted(boolean accepted);

    List<Notification> getNotifications();

    void setNotifications(List<Notification> notifications);

    void addNotification(Notification notification);

    void removeNotification(Notification notification);

    void readNotification(Notification notification);
}