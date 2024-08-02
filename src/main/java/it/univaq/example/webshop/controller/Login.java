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

public class Login extends WebshopBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response)
            throws IOException, TemplateManagerException {
        TemplateResult result = new TemplateResult(getServletContext());
        request.setAttribute("referrer", request.getParameter("referrer"));
        request.setAttribute("title", "Sign-in");
        result.activate("login.html", request, response);

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

    private void action_login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("e");
        String password = request.getParameter("p");

        if (!email.isEmpty() && !password.isEmpty()) {
            try {
                User u = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUserByEmail(email);
                if (u != null && SecurityHelpers.checkPasswordHashPBKDF2(password, u.getPassword())) {
                    // se la validazione ha successo
                    // if the identity validation succeeds
                    SecurityHelpers.createSession(request, email, u.getKey());
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
                action_default(request, response);
            }
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        }
    }
}