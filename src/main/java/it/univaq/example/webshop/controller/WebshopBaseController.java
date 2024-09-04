package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.framework.controller.AbstractBaseController;
import it.univaq.framework.data.DataLayer;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import javax.sql.DataSource;

public abstract class WebshopBaseController extends AbstractBaseController {

    @Override
    protected DataLayer createDataLayer(DataSource ds) throws ServletException {
        try {
            return new WebshopDataLayer(ds);
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

}