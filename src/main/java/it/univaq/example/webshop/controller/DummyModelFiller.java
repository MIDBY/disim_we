package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Notification;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.DataModelFiller;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class DummyModelFiller implements DataModelFiller {

    @Override
    public void fillDataModel(Map<String, Object> datamodel, HttpServletRequest request, ServletContext context) {        
        try {
            if( request.getAttribute("userId") != null && Integer.parseInt(request.getAttribute("userId").toString()) > 0) {
                int user_key = Integer.parseInt(request.getAttribute("userId").toString());
                List<Notification> list = ((WebshopDataLayer) request.getAttribute("datalayer")).getNotificationDAO().getNotificationsByUser(user_key);
                if(!list.isEmpty()) {
                    datamodel.put("notifications", list);
                }
            }
        } catch (DataException ex) {
            Logger.getLogger(DummyModelFiller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}