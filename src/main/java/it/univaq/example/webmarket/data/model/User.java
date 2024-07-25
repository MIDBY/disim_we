package it.univaq.example.webmarket.data.model;

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

    Boolean isAccepted();

    void setAccepted(Boolean accepted);

    List<Notification> getNotifications();

    void setNotifications(List<Notification> notifications);

    void addNotification(Notification notification);

    void removeNotification(Notification notification);

    void readNotification(Notification notification);
}