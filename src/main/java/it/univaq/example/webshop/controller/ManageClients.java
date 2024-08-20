package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Group;
import it.univaq.example.webshop.data.model.Request;
import it.univaq.example.webshop.data.model.User;
import it.univaq.example.webshop.data.model.impl.RequestStateEnum;
import it.univaq.example.webshop.data.model.impl.UserRoleEnum;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.result.TemplateManagerException;
import it.univaq.framework.security.SecurityHelpers;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ManageClients extends WebshopBaseController {


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

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            int user_key = Integer.parseInt(request.getSession().getAttribute("userid").toString());
            User user = ((WebshopDataLayer)request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
            Group group = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(user_key);

            request.setAttribute("username", user.getUsername());
            request.setAttribute("group", group.getName());            
            
            Group ord = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByName(UserRoleEnum.ORDINANTE);
            List<User> users = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUsersByGroup(ord.getKey());
            request.setAttribute("users", users);

            Map<Integer,Integer> requests = new HashMap<>();
            for(User u : users){
                int count = 0;
                List<Request> req = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequestsByOrdering(u.getKey());
                for(Request r : req) {
                    if(r.getRequestState().equals(RequestStateEnum.CHIUSO))
                        count++;
                }
                requests.put(u.getKey(), count);
            }
            request.setAttribute("requests", requests);
            res.activate("clients.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_verify(HttpServletRequest request, HttpServletResponse response, int user_key) throws IOException, ServletException, TemplateManagerException {
        try {
            User user = null;
            if (user_key > 0) {
                user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
            }
            if (user != null) {
                if(request.getParameter("verify").equals("1"))
                    user.setAccepted(true);
                else
                    user.setAccepted(false);
                ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().setUser(user);
                action_default(request, response);
            } else {
                handleError("Cannot verify user", request, response);
            }
            
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_assume(HttpServletRequest request, HttpServletResponse response, int user_key) throws IOException, ServletException, TemplateManagerException {
        try {
            User user = null;
            if (user_key > 0) {
                user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
            }
            if (user != null) {
                ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().changeUserGroup(user_key, UserRoleEnum.TECNICO);
                action_default(request, response);
            } else {
                handleError("Cannot update user", request, response);
            }
            
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        request.setAttribute("title", "Clients");

        int user_key;
        try {
            HttpSession s = request.getSession(false);
            if (s != null) {
                if(request.getParameter("verify") != null) {
                    user_key = SecurityHelpers.checkNumeric(request.getParameter("user"));
                    action_verify(request, response, user_key);
                } else if (request.getParameter("assume") != null) {
                    user_key = SecurityHelpers.checkNumeric(request.getParameter("user"));
                    action_assume(request, response, user_key);
                } else {
                    action_default(request, response);
                }
            } else {
                action_anonymous(request, response);
            }
        } catch (NumberFormatException ex) {
            handleError("Invalid number submitted", request, response);
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Manage Clients servlet";
    }// </editor-fold>
  

}