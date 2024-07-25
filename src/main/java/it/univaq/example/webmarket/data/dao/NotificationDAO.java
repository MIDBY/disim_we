package it.univaq.example.webmarket.data.dao;

import it.univaq.example.webmarket.data.model.Notification;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface NotificationDAO {

    Notification createNotification();

    List<Notification> getNotifications(int user_key) throws DataException;

    Notification getNotificationById(int notification_key) throws DataException;

    void readNotification(int notification_key) throws DataException;

}