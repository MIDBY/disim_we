package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.DataModelFiller;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author giuse
 */
public class DummyModelFiller implements DataModelFiller {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void fillDataModel(Map datamodel, HttpServletRequest request, ServletContext context) {        
        try {
            datamodel.put("latest_request", ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getLatestRequest());
        } catch (DataException ex) {
            Logger.getLogger(DummyModelFiller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}