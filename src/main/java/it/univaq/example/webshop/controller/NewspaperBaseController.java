package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.NewspaperDataLayer;
import it.univaq.framework.controller.AbstractBaseController;
import it.univaq.framework.data.DataLayer;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.sql.DataSource;

/**
 *
 * @author Giuseppe Della Penna
 */
public abstract class NewspaperBaseController extends AbstractBaseController {

    @Override
    protected DataLayer createDataLayer(DataSource ds) throws ServletException {
        try {
            return new NewspaperDataLayer(ds);
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

}