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
import java.util.Random;
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

public class ForgotPassword extends WebshopBaseController {
    private int otp;
    private String email;

    private void action_default(HttpServletRequest request, HttpServletResponse response)
            throws IOException, TemplateManagerException {
        TemplateResult result = new TemplateResult(getServletContext());
        request.setAttribute("referrer", request.getParameter("referrer"));
        request.setAttribute("title", "Password Recovery");
        if(request.getAttribute("errorEmail") == null )
            request.setAttribute("errorEmail", "");
        result.activate("forgot_password.html", request, response);
    }

    private void action_passwordRecover(HttpServletRequest request, HttpServletResponse response) throws IOException,TemplateManagerException {
        TemplateResult result = new TemplateResult(getServletContext());
        email = request.getParameter("e");

        if (!email.isEmpty() && !email.equals("")) {
            try {
                User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUserByEmail(email);
                if(user != null && user.getKey() > 0) {

                    //Send otp
                    Random rand = new Random();
                    otp = rand.nextInt(1255650);

                    String to = email;
                    Properties props = new Properties();
                    props.put("mail.smtp.host", "smtp.gmail.com");
                    props.put("mail.smtp.socketFactory.port", "465");
                    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.port", "465");

                    Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication(){
                            return new PasswordAuthentication("rossofuoco1999@gmail.com", "vmne carf ozpp qqyd ");
                        }
                    });
                    //compose message
                    try {
                        MimeMessage message = new MimeMessage(session);
                        message.setFrom(new InternetAddress(email));
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                        message.setSubject("Hello");
                        message.setText("Your OTP is: " + otp);

                        Transport.send(message);
                        System.out.println("Message sent successfully");
                    } catch (MessagingException ex) {
                        throw new RuntimeException(ex);
                    }

                    request.setAttribute("referrer", request.getParameter("referrer"));
                    request.setAttribute("title", "Password recovery");
                    request.setAttribute("errorOtp", false);
                    result.activate("enter_otp.html", request, response);

                } else {
                    //email non esiste
                    request.setAttribute("errorEmail", true);
                    action_default(request, response);
                }
            } catch (DataException ex) {
                Logger.getLogger(ForgotPassword.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        handleError("Password recovery with otp failed", request, response);
    }

    private void action_validateOtp(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException {
        TemplateResult result = new TemplateResult(getServletContext());
        int value = Integer.parseInt(request.getParameter("otp"));
        
        if(value == otp) {
            request.setAttribute("referrer", request.getParameter("referrer"));
            request.setAttribute("title", "Password Recovery");
            request.setAttribute("errorPassword", false);
            request.setAttribute("errorEqual", false);
            result.activate("new_password.html", request, response);
        } else {
            request.setAttribute("referrer", request.getParameter("referrer"));
            request.setAttribute("title", "Password recovery");
            request.setAttribute("errorOtp", true);
            result.activate("enter_otp.html", request, response);
        }
        handleError("Validation otp failed", request, response);
    }

    private void action_newPassword(HttpServletRequest request, HttpServletResponse response) throws IOException,TemplateManagerException {
        TemplateResult result = new TemplateResult(getServletContext());

        String newPassword = request.getParameter("p1");
        String confPassword = request.getParameter("p2");

        if (!newPassword.isEmpty() && !confPassword.isEmpty() && newPassword.equals(confPassword)) {
            try {
                User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUserByEmail(email);
                if(!SecurityHelpers.checkPasswordHashPBKDF2(newPassword, user.getPassword())){
                    //password nuova diversa dalla precedente, va bene
                    user.setPassword(SecurityHelpers.getPasswordHashPBKDF2(newPassword));
                    ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().setUser(user);

                    SecurityHelpers.createSession(request, email, user.getKey());
                    request.getSession().setAttribute("status", "resetSuccess");
                    response.sendRedirect("login");
                } else {
                    //password uguali, deve essere differente, mostra errore
                    request.setAttribute("referrer", request.getParameter("referrer"));
                    request.setAttribute("title", "Password Recovery");
                    request.setAttribute("errorPassword", false);
                    request.setAttribute("errorEqual", true);
                    result.activate("new_password.html", request, response);
                }
                
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | DataException ex) {
                Logger.getLogger(ForgotPassword.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            request.setAttribute("referrer", request.getParameter("referrer"));
            request.setAttribute("title", "Password Recovery");
            request.setAttribute("errorPassword", true);
            request.setAttribute("errorEqual", false);
            result.activate("new_password.html", request, response);
        }
        // se la validazione fallisce...
        // if the validation fails...
        handleError("Password recovery failed", request, response);
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
            if (request.getParameter("forgot") != null) {
                action_passwordRecover(request, response);
            } else if (request.getParameter("validate_otp") != null) {
                action_validateOtp(request, response);
            } else if (request.getParameter("new_password") != null) {
                action_newPassword(request, response);
            } else {
                String https_redirect_url = SecurityHelpers.checkHttps(request);
                request.setAttribute("https-redirect", https_redirect_url);
                request.setAttribute("errorEmail", false);
                action_default(request, response);
            }
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        }
    }
}