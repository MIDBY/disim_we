package it.univaq.example.webmarket.data.dao.impl;

import it.univaq.example.webmarket.data.dao.CategoryDAO;
import it.univaq.example.webmarket.data.dao.CharacteristicDAO;
import it.univaq.example.webmarket.data.dao.GroupDAO;
import it.univaq.example.webmarket.data.dao.ImageDAO;
import it.univaq.example.webmarket.data.dao.NotificationDAO;
import it.univaq.example.webmarket.data.dao.ProposalDAO;
import it.univaq.example.webmarket.data.dao.RequestCharacteristicDAO;
import it.univaq.example.webmarket.data.dao.RequestDAO;
import it.univaq.example.webmarket.data.dao.ServiceDAO;
import it.univaq.example.webmarket.data.dao.UserDAO;
import it.univaq.example.webmarket.data.model.Category;
import it.univaq.example.webmarket.data.model.Characteristic;
import it.univaq.example.webmarket.data.model.Group;
import it.univaq.example.webmarket.data.model.Image;
import it.univaq.example.webmarket.data.model.Notification;
import it.univaq.example.webmarket.data.model.Proposal;
import it.univaq.example.webmarket.data.model.Request;
import it.univaq.example.webmarket.data.model.RequestCharacteristic;
import it.univaq.example.webmarket.data.model.Service;
import it.univaq.example.webmarket.data.model.User;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import java.sql.SQLException;
import javax.sql.DataSource;

public class WebshopDataLayer extends DataLayer {

    public WebshopDataLayer(DataSource datasource) throws SQLException {
        super(datasource);
    }

    @Override
    public void init() throws DataException {
        registerDAO(Category.class, new CategoryDAO_MySQL(this));
        registerDAO(Characteristic.class, new CharacteristicDAO_MySQL(this));
        registerDAO(Group.class, new GroupDAO_MySQL(this));
        registerDAO(Image.class, new ImageDAO_MySQL(this));
        registerDAO(Notification.class, new NotificationDAO_MySQL(this));
        registerDAO(Proposal.class, new ProposalDAO_MySQL(this));
        registerDAO(RequestCharacteristic.class, new RequestCharacteristicDAO_MySQL(this));
        registerDAO(Request.class, new RequestDAO_MySQL(this));
        registerDAO(Service.class, new ServiceDAO_MySQL(this));
        registerDAO(User.class, new UserDAO_MySQL(this));

    }

    //helpers    
    public CategoryDAO getCategoryDAO() {
        return (CategoryDAO) getDAO(Category.class);
    }

    public CharacteristicDAO getCharacteristicDAO() {
        return (CharacteristicDAO) getDAO(Characteristic.class);
    }

    public GroupDAO getGroupDAO() {
        return (GroupDAO) getDAO(Group.class);
    }

    public ImageDAO getImageDAO() {
        return (ImageDAO) getDAO(Image.class);
    }
    
    public NotificationDAO getNotificationDAO() {
        return (NotificationDAO) getDAO(Notification.class);
    }

    public ProposalDAO getProposalDAO() {
        return (ProposalDAO) getDAO(Proposal.class);
    }

    public RequestCharacteristicDAO getRequestCharacteristicDAO() {
        return (RequestCharacteristicDAO) getDAO(RequestCharacteristic.class);
    }

    public RequestDAO getRequestDAO() {
        return (RequestDAO) getDAO(Request.class);
    }

    public ServiceDAO getServiceDAO() {
        return (ServiceDAO) getDAO(Service.class);
    }

    public UserDAO getUserDAO() {
        return (UserDAO) getDAO(User.class);
    }

}