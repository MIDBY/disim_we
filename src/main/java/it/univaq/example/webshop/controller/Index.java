package it.univaq.example.webshop.controller;

import java.io.*;
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
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Notification;
import it.univaq.example.webshop.data.model.Request;
import it.univaq.example.webshop.data.model.User;
import it.univaq.example.webshop.data.model.impl.NotificationTypeEnum;
import it.univaq.example.webshop.data.model.impl.OrderStateEnum;
import it.univaq.example.webshop.data.model.impl.RequestStateEnum;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateManagerException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.security.SecurityHelpers;

public class Index extends WebshopBaseController {

    private void action_anonymous(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, TemplateManagerException {
        TemplateResult res = new TemplateResult(getServletContext());
        String completeRequestURL = request.getRequestURL()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        request.setAttribute("template", 2);
        request.setAttribute("email", "Hacker");
        request.setAttribute("address", "Stranger");
        request.setAttribute("completeRequestURL", "login?referrer=" + completeRequestURL);
        res.activate("index.html", request, response);
    }

    private void action_logged(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, TemplateManagerException {
        try {
            int user_key = Integer.parseInt(request.getSession().getAttribute("userid").toString());
            User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
            if(user != null) {
                TemplateResult res = new TemplateResult(getServletContext());
                request.setAttribute("email", user.getEmail());
                request.setAttribute("address", user.getAddress());

                List<Request> requests = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequestsByOrdering(user_key);
                List<Request> shippingRequests = new ArrayList<>();
                List<Request> closedRequests = new ArrayList<>();
                for(Request r : requests){
                    r.getCategory();
                    r.getRequestCharacteristics();
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

                request.setAttribute("completeRequestURL", "login");
                res.activate("index.html", request, response);
            } else {
                action_anonymous(request, response);
            }

            
        } catch (DataException ex) {
         handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_cancel(HttpServletRequest request, HttpServletResponse response, int req_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Request req = null;
            if (req_key > 0) {
                req = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequest(req_key);
            }
            if (req != null) {
                req.setRequestState(RequestStateEnum.ANNULLATO);
                ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().setRequest(req);
                if(req.getTechnician() != null) {
                    boolean send = Boolean.parseBoolean(getServletContext().getInitParameter("sendEmail"));
                    if(send)
                        sendMail(req.getTechnician().getEmail(), "WebShop: \nWe're sorry!\nUser has canceled request: "+req.getTitle()+". \nIt will be closed.");
                    sendNotification(request, response, req.getTechnician(), req.getOrdering().getUsername() + " has canceled request : " + req.getTitle(), NotificationTypeEnum.ANNULLATO, "orders?myOrders");
                }
                action_logged(request, response);
            } else {
                handleError("Cannot update request", request, response);
            }
            
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_orderReceived(HttpServletRequest request, HttpServletResponse response, int req_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Request req = null;
            if (req_key > 0) {
                req = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequest(req_key);
            }
            if (req != null) {
                req.setOrderState(OrderStateEnum.valueOf(request.getParameter("orderState")));
                req.setRequestState(RequestStateEnum.CHIUSO);
                ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().setRequest(req);
                   
                boolean send = Boolean.parseBoolean(getServletContext().getInitParameter("sendEmail"));
                if(send)
                    sendMail(req.getTechnician().getEmail(), "WebShop: \n"+req.getOrdering().getUsername()+" has received order!\nGo to check the response.");
                sendNotification(request, response, req.getTechnician(), req.getOrdering().getUsername() + " has received order: " + req.getTitle(), NotificationTypeEnum.CHIUSO, "orders?myOrders");
                action_logged(request, response);
            } else {
                handleError("Cannot update request", request, response);
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
        int req_key = 0;
        try {
            HttpSession s = request.getSession(false);
            if (s == null) {
                action_anonymous(request, response);
            } else {
                if(SecurityHelpers.checkPermissionScript(request)) {
                    if(request.getParameter("cancel") != null) {
                        req_key = SecurityHelpers.checkNumeric(request.getParameter("cancel"));
                        action_cancel(request, response, req_key);
                    } else {
                        if(request.getParameter("ship") != null) {
                            req_key = SecurityHelpers.checkNumeric(request.getParameter("ship"));
                            action_orderReceived(request, response, req_key);
                        } else
                            action_logged(request, response);
                    }
                } else {
                    response.sendRedirect("homepage");
                }
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