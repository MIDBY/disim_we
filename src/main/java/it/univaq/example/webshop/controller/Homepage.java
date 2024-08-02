package it.univaq.example.webshop.controller;

import java.io.*;
import java.net.URLEncoder;

import javax.servlet.*;
import javax.servlet.http.*;
import it.univaq.framework.result.TemplateManagerException;
import it.univaq.framework.result.TemplateResult;

public class Homepage extends WebshopBaseController {

    private void action_anonymous(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, TemplateManagerException {
        TemplateResult res = new TemplateResult(getServletContext());
        String completeRequestURL = request.getRequestURL()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        request.setAttribute("title", "Homepage");
        request.setAttribute("completeRequestURL",
                "\"login?referrer=" + URLEncoder.encode(completeRequestURL, "UTF-8") + "\\\"");
        res.activate("admin_template/homepage.ftl.html", request, response);
    }

    private void action_logged(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, TemplateManagerException {
        // try {
        TemplateResult res = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Homepage");
        res.activate("admin_template/homepage.ftl.html", request, response);
        // } catch (DataException ex) {
        // handleError("Data access exception: " + ex.getMessage(), request, response);
        // }
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
        try {
            HttpSession s = request.getSession(false);
            if (s == null) {
                action_anonymous(request, response);
            } else {
                action_logged(request, response);
            }
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Main Webshop servlet";
    }
    // </editor-fold>
}