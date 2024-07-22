package it.univaq.example.webmarket.controller;

import it.univaq.example.webmarket.data.dao.impl.NewspaperDataLayer;
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

    @Override
    public void fillDataModel(Map datamodel, HttpServletRequest request, ServletContext context) {        
        try {
            datamodel.put("latest_issue", ((NewspaperDataLayer) request.getAttribute("datalayer")).getIssueDAO().getLatestIssue());
        } catch (DataException ex) {
            Logger.getLogger(DummyModelFiller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}