package it.univaq.example.webmarket.data.dao;

import it.univaq.example.webmarket.data.model.Notification;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface NotificationDAO {

    Notification createNotification();

    Notification getNotification(int notification_key) throws DataException;

    List<Notification> getNotificationsByUser(int user_key) throws DataException;

    List<Notification> getNotificationsNotReadByUser(int user_key) throws DataException;

    void setNotification(Notification notification) throws DataException;

    void deleteNotification(Notification notification) throws DataException;

}