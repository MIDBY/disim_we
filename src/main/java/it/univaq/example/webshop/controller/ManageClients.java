package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Group;
import it.univaq.example.webshop.data.model.Notification;
import it.univaq.example.webshop.data.model.Request;
import it.univaq.example.webshop.data.model.User;
import it.univaq.example.webshop.data.model.impl.NotificationTypeEnum;
import it.univaq.example.webshop.data.model.impl.RequestStateEnum;
import it.univaq.example.webshop.data.model.impl.UserRoleEnum;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.result.TemplateManagerException;
import it.univaq.framework.security.SecurityHelpers;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
            Group group = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(user_key);

            request.setAttribute("username", user.getUsername());
            request.setAttribute("group", group.getName());            
            
            Group ord = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByName(UserRoleEnum.ORDINANTE);
            List<User> users = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUsersByGroup(ord.getKey());
            request.setAttribute("users", users);

            Map<Integer,Integer> requestsTot = new HashMap<>();
            Map<Integer,Integer> requestsActive = new HashMap<>();
            for(User u : users){
                int count = 0;
                int active = 0;
                List<Request> req = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequestsByOrdering(u.getKey());
                for(Request r : req) {
                    if(!r.getRequestState().equals(RequestStateEnum.CHIUSO))
                        active ++;
                }
                count += req.size(); 
                requestsTot.put(u.getKey(), count);
                requestsActive.put(u.getKey(), active);
            }
            request.setAttribute("requestsTot", requestsTot);
            request.setAttribute("requestsActive", requestsActive);
            res.activate("listClients.html", request, response);
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
                boolean send = Boolean.parseBoolean(getServletContext().getInitParameter("sendEmail"));
                if(user.isAccepted()){
                    if(send)
                        sendMail(user.getEmail(), "Administration Mail: \nYou're been verified from our site.\nNow you can login and buy everything you want! Have a nice day");
                    sendNotification(request, response, user, "Welcome in Webshop, new client!", NotificationTypeEnum.INFO, "index");
                } else {
                    if(send)
                        sendMail(user.getEmail(), "Administration Mail: \nWe're sorry, you're not longer verified on our site.\n Write on our mail to get informations about");
                    sendNotification(request, response, user, "We're sorry, you're not allowed anymore to stay in Webshop. Bye!", NotificationTypeEnum.INFO, "");
                }

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
                boolean send = Boolean.parseBoolean(getServletContext().getInitParameter("sendEmail"));
                if(send)
                    sendMail(user.getEmail(), "Administration Mail: \nCongratulations!\nYou're been assumed in our site as technician. From now you can work with os! Have a nice day");
                sendNotification(request, response, user, "Welcome in Webshop, new technician!", NotificationTypeEnum.INFO, "profile");

                action_default(request, response);
            } else {
                handleError("Cannot update user", request, response);
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

        request.setAttribute("title", "Clients");
        request.setAttribute("themeMode", request.getSession().getAttribute("themeMode"));
        request.setAttribute("themeSkin", request.getSession().getAttribute("themeSkin"));
        
        int user_key;
        try {
            HttpSession s = request.getSession(false);
            if (s != null) {
                if(SecurityHelpers.checkPermissionScript(request)) {
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
                    if(s.getAttribute("userid") != null) {
                        Group myGroup = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(Integer.parseInt(request.getSession().getAttribute("userid").toString()));
                        if(myGroup.getName().equals(UserRoleEnum.TECNICO))
                            response.sendRedirect("homepage");
                        else
                            response.sendRedirect("index");
                    } else
                        response.sendRedirect("login");
                    
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
        return "Manage Clients servlet";
    }// </editor-fold>
  

}