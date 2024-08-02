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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.*;

public class Register extends WebshopBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response)
            throws IOException, TemplateManagerException {
        TemplateResult result = new TemplateResult(getServletContext());
        request.setAttribute("referrer", request.getParameter("referrer"));
        request.setAttribute("title", "Sign-up");
        result.activate("register.html", request, response);

        // //esempio di creazione utente
        // //create user example
        // try {
        // User u = ((NewspaperDataLayer)
        // request.getAttribute("datalayer")).getUserDAO().createUser();
        // u.setUsername("a");
        // u.setPassword(SecurityHelpers.getPasswordHashPBKDF2("p"));
        // ((NewspaperDataLayer)
        // request.getAttribute("datalayer")).getUserDAO().storeUser(u);
        // } catch (DataException | NoSuchAlgorithmException | InvalidKeySpecException
        // ex) {
        // Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        // }
    }

    // nota: usente di default nel database: nome a, password p
    // note: default user in the database: name: a, password p
    private void action_register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("u");
        String email = request.getParameter("e");
        String password = request.getParameter("p");
        String address = request.getParameter("a") + ", " + request.getParameter("c") + ", "
                + request.getParameter("k");

        if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            try {
                User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().createUser();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(SecurityHelpers.getPasswordHashPBKDF2(password));
                user.setAddress(address);

                ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().setUser(user);

                User check = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user.getKey());
                if (check != null && SecurityHelpers.checkPasswordHashPBKDF2(password, check.getPassword())) {
                    SecurityHelpers.createSession(request, email, user.getKey());
                    // se è stato trasmesso un URL di origine, torniamo a quell'indirizzo
                    // if an origin URL has been transmitted, return to it
                    if (request.getParameter("referrer") != null) {
                        response.sendRedirect(request.getParameter("referrer"));
                    } else {
                        response.sendRedirect("homepage");
                    }                    
                    return;
                }
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | DataException ex) {
                Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // se la validazione fallisce...
        // if the validation fails...
        handleError("Registration failed", request, response);
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
