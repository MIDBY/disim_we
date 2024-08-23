package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Group;
import it.univaq.example.webshop.data.model.User;
import it.univaq.example.webshop.data.model.impl.UserRoleEnum;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateManagerException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.security.SecurityHelpers;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.*;

public class Login extends WebshopBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response)
            throws IOException, TemplateManagerException {
        TemplateResult result = new TemplateResult(getServletContext());
        request.setAttribute("referrer", request.getParameter("referrer"));
        request.setAttribute("title", "Sign-in");            
        result.activate("login.html", request, response);
    }

    private void action_login(HttpServletRequest request, HttpServletResponse response) throws IOException,TemplateManagerException {
        String email = request.getParameter("e");
        String password = request.getParameter("p");

        if (!email.isEmpty() && !password.isEmpty()) {
            try {
                User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUserByEmail(email);
                if(user != null) {                
                    Boolean correctPassword = SecurityHelpers.checkPasswordHashPBKDF2(password, user.getPassword());
                    if (user != null && correctPassword) {
                        //utente esiste e password corretta, 
                        //controllo se l'utente è nel gruppo Ordinante ed accettato altrimenti passa 
                        Group group = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(user.getKey());
                        if(group.getName().equals(UserRoleEnum.ORDINANTE)) {
                            if(user.isAccepted()) {         
                                SecurityHelpers.createSession(request, email, user.getKey());                   
                                if (request.getParameter("referrer") != null) {
                                    response.sendRedirect(request.getParameter("referrer"));
                                } else {       
                                    //TODO: cambiare index con la prima pagina dello store
                                    response.sendRedirect("index");
                                }
                            } else {
                                //se ordinante non è stato ancora accettato compare pop up
                                request.setAttribute("errorLogin", false);
                                request.setAttribute("status", "userNotAllowed");
                                action_default(request, response);
                            }
                        } else {
                            //utente Amministratore o tecnino, reindirizzato al backend
                            SecurityHelpers.createSession(request, email, user.getKey());
                            if (request.getParameter("referrer") != null) {
                                response.sendRedirect(request.getParameter("referrer"));
                            } else {                            
                                response.sendRedirect("homepage");
                            }
                        }
                    } else {
                        //password errata
                        request.setAttribute("errorLogin", true);
                        request.setAttribute("status", "null");
                        action_default(request, response);
                    }
                } else {
                    //email errata
                    request.setAttribute("errorLogin", true);
                    request.setAttribute("status", "null");
                    action_default(request, response);
                }

            } catch (NoSuchAlgorithmException | InvalidKeySpecException | DataException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // se la validazione fallisce...
        // if the validation fails...
        handleError("Login failed", request, response);
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
            if (request.getParameter("login") != null) {
                action_login(request, response);
            } else {
                String https_redirect_url = SecurityHelpers.checkHttps(request);
                request.setAttribute("https-redirect", https_redirect_url);
                request.setAttribute("errorLogin", false);
                //qui settare status
                HttpSession s = request.getSession(false);
                try {
                    request.setAttribute("status", s.getAttribute("status").toString());
                } catch (NullPointerException e) {
                    request.setAttribute("status", "null");
                }
                action_default(request, response);
            }
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        }
    }
}