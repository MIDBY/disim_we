package it.univaq.example.webshop.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Group;
import it.univaq.example.webshop.data.model.User;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateManagerException;
import it.univaq.framework.result.TemplateResult;

public class Index extends WebshopBaseController {

    private void action_anonymous(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, TemplateManagerException {
        TemplateResult res = new TemplateResult(getServletContext());
        String completeRequestURL = request.getRequestURL()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        
        request.setAttribute("username", "Hacker");
        request.setAttribute("group", "Stranger");
        request.setAttribute("completeRequestURL", "login?referrer=" + completeRequestURL);
        res.activate("homepage.ftl.html", request, response);
    }

    private void action_logged(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, TemplateManagerException {
        try {
            int user_key = Integer.parseInt(request.getSession().getAttribute("userid").toString());
            User user = ((WebshopDataLayer)request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
            if(user != null) {
                Group group = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(user_key);

                TemplateResult res = new TemplateResult(getServletContext());
                request.setAttribute("username", user.getUsername());
                request.setAttribute("group", group.getName());

                request.setAttribute("completeRequestURL", "login");
                res.activate("index.html", request, response);
            } else {
                action_anonymous(request, response);
            }

            
        } catch (DataException ex) {
         handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        request.setAttribute("userid", request.getSession().getAttribute("userid"));
        request.setAttribute("title", "Index");
        try {
            HttpSession s = request.getSession(false);
            if (s == null) {
                action_anonymous(request, response);
            } else {
                action_logged(request, response);
            }
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Homepage Front-end Webshop servlet";
    }
    // </editor-fold>
}