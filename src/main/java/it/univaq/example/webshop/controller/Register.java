package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.User;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateManagerException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.security.SecurityHelpers;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.*;
import javax.servlet.http.*;

public class Register extends WebshopBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response)
            throws IOException, TemplateManagerException {
        TemplateResult result = new TemplateResult(getServletContext());
        request.setAttribute("referrer", request.getParameter("referrer"));
        request.setAttribute("title", "Sign-up");
        if((request.getAttribute("errorRegister") != null) && request.getAttribute("errorRegister").equals(true)) {
            request.setAttribute("username", request.getParameter("u"));
            request.setAttribute("address", request.getParameter("a"));
            request.setAttribute("number", request.getParameter("n"));
            request.setAttribute("city", request.getParameter("c"));
            request.setAttribute("cap", request.getParameter("k"));
        } else {
            request.setAttribute("username", "");
            request.setAttribute("address", "");
            request.setAttribute("number", "");
            request.setAttribute("city", "");
            request.setAttribute("cap", "");
        }
                
        result.activate("register.html", request, response);
    }

    private void action_register(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException {
        String username = request.getParameter("u");
        String email = request.getParameter("e");
        String password = request.getParameter("p");
        String address = request.getParameter("a") + ", " + request.getParameter("n") + ", " + request.getParameter("c") + ", "
                + request.getParameter("k") + ", " + request.getParameter("t");

        if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            try {
                User checkUser = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUserByEmail(email);
                if(checkUser == null) {
                    //continuo la registrazione
                    User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().createUser();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPassword(SecurityHelpers.getPasswordHashPBKDF2(password));
                    user.setAddress(address);
                    ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().setUser(user);

                    SecurityHelpers.createSession(request, email, user.getKey());
                    // se è stato trasmesso un URL di origine, torniamo a quell'indirizzo
                    // if an origin URL has been transmitted, return to it
                    boolean send = Boolean.parseBoolean(getServletContext().getInitParameter("sendEmail"));
                    if(send)
                        sendMail(email, "Hi "+username+",\n welcome to WebShop! \nAnother email will arrive as soon as possible confirming acceptance of your account in our shop!");
                    if (request.getParameter("referrer") != null) {
                        response.sendRedirect(request.getParameter("referrer"));
                    } else {
                        request.getSession().setAttribute("status", "registrationSuccess");
                        response.sendRedirect("login");
                    }                    
                } else {
                    //mostro messaggio utente già esistente
                    request.setAttribute("errorRegister", true);
                    action_default(request, response);
                }
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | DataException ex) {
                Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // se la validazione fallisce...
        // if the validation fails...
        handleError("Registration failed", request, response);
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
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        try {
            if (request.getParameter("register") != null) {
                action_register(request, response);
            } else {
                String https_redirect_url = SecurityHelpers.checkHttps(request);
                request.setAttribute("https-redirect", https_redirect_url);
                request.setAttribute("errorRegister", false);
                action_default(request, response);
            }
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Register User servlet";
    }
}
