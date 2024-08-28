package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Group;
import it.univaq.example.webshop.data.model.Notification;
import it.univaq.example.webshop.data.model.User;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.security.SecurityHelpers;
import it.univaq.framework.result.TemplateManagerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ManageNotifications extends WebshopBaseController {

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
            User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
            Group group = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(user_key);

            request.setAttribute("username", user.getUsername());
            request.setAttribute("group", group.getName());        

            request.setAttribute("notifications", ((WebshopDataLayer) request.getAttribute("datalayer")).getNotificationDAO().getNotificationsByUser(user_key));
            
            res.activate("listNotifications.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_read(HttpServletRequest request, HttpServletResponse response, int not_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Notification notification = null;
            if(not_key > 0) 
                notification = ((WebshopDataLayer) request.getAttribute("datalayer")).getNotificationDAO().getNotification(not_key);

            if(notification != null) {
                if(notification.isRead())
                    notification.setRead(false);
                else
                    notification.setRead(true);

                ((WebshopDataLayer) request.getAttribute("datalayer")).getNotificationDAO().setNotification(notification);
                
                action_default(request, response);
            } else
                handleError("Cannot read notification", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_All(HttpServletRequest request, HttpServletResponse response, boolean setRead) throws IOException, ServletException, TemplateManagerException {
        try {
            String[] notificationsA = request.getParameterValues("check[]");
            List<Notification> notifications = new ArrayList<>();
            for(String s : notificationsA){
                notifications.add(((WebshopDataLayer) request.getAttribute("datalayer")).getNotificationDAO().getNotification(Integer.parseInt(s)));
            }

            for(Notification n : notifications) {
                n.setRead(setRead);
                ((WebshopDataLayer) request.getAttribute("datalayer")).getNotificationDAO().setNotification(n);
            }

            action_default(request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_delete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException {
        try {
            String[] notificationsA = request.getParameterValues("check[]");
            List<Notification> notifications = new ArrayList<>();
            for(String s : notificationsA){
                notifications.add(((WebshopDataLayer) request.getAttribute("datalayer")).getNotificationDAO().getNotification(Integer.parseInt(s)));
            }

            for(Notification n : notifications) {
                ((WebshopDataLayer) request.getAttribute("datalayer")).getNotificationDAO().deleteNotification(n);
            }

            action_default(request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }
    

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        request.setAttribute("title", "Notifications");
        request.setAttribute("themeMode", request.getSession().getAttribute("themeMode"));
        request.setAttribute("themeSkin", request.getSession().getAttribute("themeSkin"));
        
        try {
            HttpSession s = request.getSession(false);
            if (s != null) {
                if(request.getParameter("read") != null) {
                    int not_key = SecurityHelpers.checkNumeric(request.getParameter("read"));
                    action_read(request, response, not_key);
                } else if(request.getParameter("readAll") != null) {
                    action_All(request, response, true);
                } else if(request.getParameter("unreadAll") != null) {
                    action_All(request, response, false);
                } else if(request.getParameter("delete") != null) {
                    action_delete(request, response);
                } else
                    action_default(request, response);
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
        return "Manage Notifications servlet";
    }// </editor-fold>
  

}