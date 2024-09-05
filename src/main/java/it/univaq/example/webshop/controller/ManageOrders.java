package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Group;
import it.univaq.example.webshop.data.model.Notification;
import it.univaq.example.webshop.data.model.Request;
import it.univaq.example.webshop.data.model.RequestCharacteristic;
import it.univaq.example.webshop.data.model.User;
import it.univaq.example.webshop.data.model.impl.NotificationTypeEnum;
import it.univaq.example.webshop.data.model.impl.OrderStateEnum;
import it.univaq.example.webshop.data.model.impl.RequestStateEnum;
import it.univaq.example.webshop.data.model.impl.UserRoleEnum;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.security.SecurityHelpers;
import it.univaq.framework.result.TemplateManagerException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
 
public class ManageOrders extends WebshopBaseController {

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
            
            List<Request> requests = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getUnassignedRequests();
            for(Request r : requests) {
                r.getCategory();
                r.getOrdering();
                r.getRequestCharacteristics();
                for(RequestCharacteristic reqc : r.getRequestCharacteristics()) {
                    reqc.getCharacteristic();
                }
            }
            request.setAttribute("requests", requests);

            res.activate("listOrders.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_update(HttpServletRequest request, HttpServletResponse response, int req_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Request req = null;
            if (req_key > 0) {
                req = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequest(req_key);
            }
            if (req != null) {
                int user_key = Integer.parseInt(request.getSession().getAttribute("userid").toString());
                User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
                req.getCategory();
                req.getOrdering();
                req.setTechnician(user);
                req.setRequestState(RequestStateEnum.PRESOINCARICO);
                ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().setRequest(req);
                boolean send = Boolean.parseBoolean(getServletContext().getInitParameter("sendEmail"));
                if(send) 
                    sendMail(req.getOrdering().getEmail(), "Info mail: \nYour request: "+req.getTitle()+"\n has been taken in charge by one of our operators. \nYou will soon receive a proposal from our operator.");
                sendNotification(request, response, req.getOrdering(), "Great news! Your request "+req.getTitle()+" has been taken in charge by one of our operators!", NotificationTypeEnum.INFO, "index");
                response.sendRedirect("orders?myOrders");
            } else {
                handleError("Cannot update request", request, response);
            }
            
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_myOrders(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            int user_key = Integer.parseInt(request.getSession().getAttribute("userid").toString());
            User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
            Group group = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(user_key);

            request.setAttribute("username", user.getUsername());
            request.setAttribute("group", group.getName());            
            
            List<Request> requests = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequestsByTechnician(user_key);
            List<Request> shippingRequests = new ArrayList<>();
            List<Request> closedRequests = new ArrayList<>();
            for(Request r : requests) {
                r.getCategory();
                r.getOrdering();
                r.getTechnician();
                r.getProposals();
                if(r.getRequestState().equals(RequestStateEnum.ORDINATO))
                    shippingRequests.add(r);
                if(r.getRequestState().equals(RequestStateEnum.CHIUSO) || r.getRequestState().equals(RequestStateEnum.ANNULLATO))
                    closedRequests.add(r);
            }
            requests.removeAll(shippingRequests);
            requests.removeAll(closedRequests);

            request.setAttribute("requests", requests);
            request.setAttribute("shippingRequests", shippingRequests);
            request.setAttribute("closedRequests", closedRequests);

            res.activate("listMyOrders.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_ship(HttpServletRequest request, HttpServletResponse response, int req_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Request req = null;
            if (req_key > 0) {
                req = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequest(req_key);
            }
            if (req != null) {
                req.getOrdering();
                req.setOrderState(OrderStateEnum.SPEDITO);
                ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().setRequest(req);
                boolean send = Boolean.parseBoolean(getServletContext().getInitParameter("sendEmail"));
                if(send) 
                    sendMail(req.getOrdering().getEmail(), "Info mail: \nYour request: '+req.getTitle()+' has been shipped! \nThank you for purchasing on our site.");
                sendNotification(request, response, req.getOrdering(), "Great news! Your purchase of request "+req.getTitle()+" has been shipped!", NotificationTypeEnum.CHIUSO, "index");
                action_myOrders(request, response);
            } else {
                handleError("Cannot send request", request, response);
            }
            
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void sendNotification(HttpServletRequest request, HttpServletResponse response, User user, String message, NotificationTypeEnum type, String link) {
        try {
            Notification notification = ((WebshopDataLayer) request.getAttribute("datalayer")).getNotificationDAO().createNotification();
            notification.setRecipient(user);
            notification.setCreationDate(LocalDateTime.now());
            notification.setMessage(message);
            notification.setType(type);
            notification.setLink(link);
            ((WebshopDataLayer) request.getAttribute("datalayer")).getNotificationDAO().setNotification(notification);
        } catch (DataException e) {
            handleError("Send notification exception: " + e.getMessage(), request, response);
        }
    }

    private void sendMail(String email, String text) {
        String sender = getServletContext().getInitParameter("emailSender");
        String securityCode = getServletContext().getInitParameter("securityCode");
        String to = email;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(sender, securityCode);
            }
        });
        //compose message
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("WebMarket");
            message.setText(text);
            Transport.send(message);
            System.out.println("Message sent successfully");
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        request.setAttribute("title", "Orders");
        request.setAttribute("themeMode", request.getSession().getAttribute("themeMode"));
        request.setAttribute("themeSkin", request.getSession().getAttribute("themeSkin"));
        
        int req_key;
        try {
            HttpSession s = request.getSession(false);
            if (s != null) {
                if(SecurityHelpers.checkPermissionScript(request)) {
                    if (request.getParameter("myOrders") != null) {
                        if(request.getParameter("ship") != null) {
                            req_key = SecurityHelpers.checkNumeric(request.getParameter("ship"));
                            action_ship(request, response, req_key);
                        }else
                            action_myOrders(request, response);
                    } else {
                        if(request.getParameter("order") != null) {
                            req_key = SecurityHelpers.checkNumeric(request.getParameter("order"));
                            action_update(request, response, req_key);
                        } else
                            action_default(request, response);
                    }
                } else {
                    Group myGroup = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(Integer.parseInt(request.getSession().getAttribute("userid").toString()));
                    if(myGroup.getName().equals(UserRoleEnum.AMMINISTRATORE))
                        response.sendRedirect("homepage");
                    else
                        response.sendRedirect("index");
                }
            } else {
                action_anonymous(request, response);
            }
        } catch (NumberFormatException ex) {
            handleError("Invalid number submitted", request, response);
        } catch (IOException | TemplateManagerException | DataException ex) {
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
        return "Manage Orders servlet";
    }// </editor-fold>
  

}