package it.univaq.example.webmarket.data.dao.impl;

import it.univaq.example.webmarket.data.dao.NotificationDAO;
import it.univaq.example.webmarket.data.model.Notification;
import it.univaq.example.webmarket.data.model.impl.proxy.NotificationProxy;
import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import it.univaq.framework.data.DataLayer;
import it.univaq.framework.data.OptimisticLockException;

public class NotificationDAO_MySQL extends DAO implements NotificationDAO {

    private PreparedStatement sNotificationByID, sNotificationsByUser, sNotificationsNotReadByUser, iNotification, uNotification, dNotification;

    public NotificationDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sNotificationByID = connection.prepareStatement("SELECT * FROM notifica WHERE id=?");
            sNotificationsByUser = connection.prepareStatement("SELECT id FROM notifica WHERE idDestinatario=?");
            sNotificationsNotReadByUser = connection.prepareStatement("SELECT id FROM notifica WHERE idDestinatario=? and letto=0");
            iNotification = connection.prepareStatement("INSERT INTO notifica (idDestinatario,messaggio,dataCreazione) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
            uNotification = connection.prepareStatement("UPDATE notifica SET idDestinatario=?,messaggio=?,dataCreazione=?,letto=?,versione=? WHERE id=? and versione=?");
            dNotification = connection.prepareStatement("DELETE FROM notifica WHERE id=?");
        } catch (SQLException ex) {
            throw new DataException("Error initializing webshop data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            sNotificationByID.close();
            sNotificationsByUser.close();
            iNotification.close();
            uNotification.close();
            dNotification.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        super.destroy();
    }

    @Override
    public Notification createNotification() {
        return new NotificationProxy(getDataLayer());
    }

    //helper
    private NotificationProxy createNotification(ResultSet rs) throws DataException {
        try {
            NotificationProxy a = (NotificationProxy)createNotification();
            a.setKey(rs.getInt("id"));
            a.setRecipientKey(rs.getInt("idDestinatario"));
            a.setMessage(rs.getString("messaggio"));
            a.setCreationDate(rs.getObject("dataCreazione", LocalDate.class));
            a.setRead(rs.getBoolean("letto"));
            a.setVersion(rs.getLong("versione"));
            return a;
        } catch (SQLException ex) {
            throw new DataException("Unable to create notification object form ResultSet", ex);
        }
    }

    @Override
    public Notification getNotification(int notification_key) throws DataException {
        Notification a = null;
        if (dataLayer.getCache().has(Notification.class, notification_key)) {
            a = dataLayer.getCache().get(Notification.class, notification_key);
        } else {
            try {
                sNotificationByID.setInt(1, notification_key);
                try (ResultSet rs = sNotificationByID.executeQuery()) {
                    if (rs.next()) {
                        a = createNotification(rs);
                        dataLayer.getCache().add(Notification.class, a);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load notification by ID", ex);
            }
        }
        return a;
    }

    @Override
    public List<Notification> getNotificationsByUser(int user_key) throws DataException {
        List<Notification> result = new ArrayList<Notification>();
        try {
            sNotificationsByUser.setInt(1, user_key);
            try (ResultSet rs = sNotificationsByUser.executeQuery()) {
                while (rs.next()) {
                    result.add(getNotification(rs.getInt("id")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load notifications", ex);
        }
        return result;
    }

    @Override
    public List<Notification> getNotificationsNotReadByUser(int user_key) throws DataException {
        List<Notification> result = new ArrayList<Notification>();
        try {
            sNotificationsNotReadByUser.setInt(1, user_key);
            try (ResultSet rs = sNotificationsNotReadByUser.executeQuery()) {
                while (rs.next()) {
                    result.add(getNotification(rs.getInt("id")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load not read notifications", ex);
        }
        return result;
    }

    @Override
    public void setNotification(Notification notification) throws DataException {
        try {
            if (notification.getKey() != null && notification.getKey() > 0) { //update
                if (notification instanceof DataItemProxy && !((DataItemProxy) notification).isModified()) {
                    return;
                }
                if (notification.getRecipient() != null) {
                    uNotification.setInt(1, notification.getRecipient().getKey());
                } else {
                    uNotification.setNull(1, java.sql.Types.INTEGER);
                }
                uNotification.setString(2, notification.getMessage());
                uNotification.setObject(3, notification.getCreationDate());
                uNotification.setBoolean(4, notification.isRead());

                long current_version = notification.getVersion();
                long next_version = current_version + 1;

                uNotification.setLong(5, next_version);
                uNotification.setInt(6, notification.getKey());
                uNotification.setLong(7, current_version);

                if (uNotification.executeUpdate() == 0) {
                    throw new OptimisticLockException(notification);
                } else {
                    notification.setVersion(next_version);
                }
            } else { //insert
                if (notification.getRecipient() != null) {
                    iNotification.setInt(1, notification.getRecipient().getKey());
                } else {
                    iNotification.setNull(1, java.sql.Types.INTEGER);
                }
                iNotification.setString(2, notification.getMessage());
                iNotification.setObject(3, notification.getCreationDate());
                iNotification.setBoolean(4, notification.isRead());
                if (iNotification.executeUpdate() == 1) {
                    try (ResultSet keys = iNotification.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            notification.setKey(key);
                            dataLayer.getCache().add(Notification.class, notification);
                        }
                    }
                }
            }
            if (notification instanceof DataItemProxy) {
                ((DataItemProxy) notification).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store notification", ex);
        }
    }

    @Override
    public void deleteNotification(Notification notification) throws DataException {
        try {
            if (notification.getKey() != null && notification.getKey() > 0) { //delete
                dNotification.setInt(1, notification.getKey());
                if (dNotification.executeUpdate() == 0) {
                    throw new OptimisticLockException(notification);
                } else {
                    dataLayer.getCache().delete(Notification.class, notification);
                }
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to delete notification", ex);
        }
    }
}