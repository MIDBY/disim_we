package it.univaq.example.webshop.controller;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Group;
import it.univaq.example.webshop.data.model.Proposal;
import it.univaq.example.webshop.data.model.Request;
import it.univaq.example.webshop.data.model.User;
import it.univaq.example.webshop.data.model.impl.OrderStateEnum;
import it.univaq.example.webshop.data.model.impl.ProposalStateEnum;
import it.univaq.example.webshop.data.model.impl.UserRoleEnum;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateManagerException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.security.SecurityHelpers;

public class Profile extends WebshopBaseController {

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

    private void action_showEdit(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, TemplateManagerException {
        try {        
            int user_key = Integer.parseInt(request.getSession().getAttribute("userid").toString());
            User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
            if(user != null) {
                Group group = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(user_key);
                String[] address = new String[5];
                Arrays.fill(address, "");
                if(!user.getAddress().isEmpty()) address = user.getAddress().split(", ");
                
                TemplateResult res = new TemplateResult(getServletContext());
                request.setAttribute("username", user.getUsername());
                request.setAttribute("group", group.getName());
                request.setAttribute("address", address[0]);
                request.setAttribute("numberVal", address[1]);
                request.setAttribute("city", address[2]);
                request.setAttribute("cap", address[3]);
                request.setAttribute("country", address[4]);
                request.setAttribute("email", user.getEmail());
                request.setAttribute("subscription", user.getSubscriptionDate());

                if(request.getAttribute("errorEmail") == null)
                    request.setAttribute("errorEmail", false);

                if(request.getAttribute("errorOldPassword") == null)
                    request.setAttribute("errorOldPassword", false);

                if(request.getAttribute("errorNewPassword") == null)
                    request.setAttribute("errorNewPassword", false);
            
                res.activate("profileEdit.html", request, response);
            } else {
                    action_anonymous(request, response);
            }        
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_profile(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, TemplateManagerException {
        try {
            int user_key = Integer.parseInt(request.getSession().getAttribute("userid").toString());
            User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
            if(user != null) {
                Group group = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(user_key);
                String[] address = new String[5];
                Arrays.fill(address, "");
                if(!user.getAddress().isEmpty()) address = user.getAddress().split(", ");

                TemplateResult res = new TemplateResult(getServletContext());
                request.setAttribute("username", user.getUsername());
                request.setAttribute("group", group.getName());
                request.setAttribute("addressTotal", user.getAddress());
                request.setAttribute("email", user.getEmail());
                request.setAttribute("subscription", user.getSubscriptionDate());
                request.setAttribute("address", address[0] + ", " + address[1]);
                request.setAttribute("city", address[2] + ", " + address[3]);
                request.setAttribute("country", address[4]);
                if(group.getName().equals(UserRoleEnum.TECNICO)){
                    request.setAttribute("techUser", true);

                    int rTechAccepted = 0;
                    float cTechEarned = 0;
                    int totRequests = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequests().size();
                    float totCash = 0;
                    List<Request> rTech = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequestsByTechnician(user.getKey());
                    if(rTech != null){
                        rTechAccepted = rTech.size();
                        for (Request r : rTech) {
                            Proposal pTech = ((WebshopDataLayer) request.getAttribute("datalayer")).getProposalDAO().getLastProposalByRequest(r.getKey());
                            if(pTech != null && pTech.getProposalState().equals(ProposalStateEnum.APPROVATO) && r.getOrderState().equals(OrderStateEnum.ACCETTATO)){
                                cTechEarned += pTech.getProductPrice();
                            } else {
                                continue;
                            }
                        }
                    }  
                    rTech = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequestsByOrderState(OrderStateEnum.ACCETTATO);
                    if(rTech != null){
                        for (Request r : rTech) {
                            Proposal pTech = ((WebshopDataLayer) request.getAttribute("datalayer")).getProposalDAO().getLastProposalByRequest(r.getKey());
                            if(pTech != null && pTech.getProposalState().equals(ProposalStateEnum.APPROVATO)){
                                totCash += pTech.getProductPrice();
                            } else {
                                continue;
                            }
                        }
                    }          
                    int percentageRequests = calculatePercentage(rTechAccepted, totRequests);                    
                    int percentageCash = calculatePercentage((int)cTechEarned, (int)totCash);

                    request.setAttribute("rTechAccepted", rTechAccepted);
                    request.setAttribute("totalRequests", totRequests);
                    request.setAttribute("cTechEarned", cTechEarned);
                    request.setAttribute("totalCash", totCash);
                    request.setAttribute("percentageRequests", percentageRequests);
                    request.setAttribute("percentageRequestsCSS", "style=width:" + percentageRequests + "%;");
                    request.setAttribute("percentageCash", percentageCash);
                    request.setAttribute("percentageCashCSS", "style=width:" + percentageCash + "%;");
                } else {
                    request.setAttribute("techUser", false);
                }
                res.activate("profile.html", request, response);
            } else {
                action_anonymous(request, response);
            }

            
        } catch (DataException ex) {
         handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_edit(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, TemplateManagerException {
        try {
            int user_key = Integer.parseInt(request.getSession().getAttribute("userid").toString());
            User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
            if(user != null) {                    
                boolean modified = false;
                if(SecurityHelpers.checkNumeric(request.getParameter("edit")) == 1) {
                    //modifica credenziali di sicurezza
                    if(request.getParameter("newemail") != null && !request.getParameter("newemail").isEmpty()) {
                        User example = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUserByEmail(request.getParameter("newemail"));
                        if(example == null) {
                            user.setEmail(request.getParameter("newemail"));
                            modified = true;
                        } else {
                            request.setAttribute("errorEmail", true);
                            action_showEdit(request, response);
                        }

                    }
                    String current = request.getParameter("currentpassword");
                    String newP = request.getParameter("newpassword");
                    if(current != null && !current.isEmpty() && newP != null && !newP.isEmpty()) {
                        if(SecurityHelpers.checkPasswordHashPBKDF2(current, user.getPassword())){
                            if(current.equals(newP)) {
                                request.setAttribute("errorNewPassword", true);
                                action_showEdit(request, response);
                            } else {
                                user.setPassword(SecurityHelpers.getPasswordHashPBKDF2(newP));
                                modified = true;
                            }
                        } else {
                            request.setAttribute("errorOldPassword", true);
                            action_showEdit(request, response);
                        }
                    }
                } else {
                    //modifica altre credenziali
                    String username = request.getParameter("u");
                    String address = request.getParameter("a") + ", " + request.getParameter("n") + ", " + request.getParameter("c") + ", "
                        + request.getParameter("k") + ", " + request.getParameter("t");

                    if (!username.isEmpty() && !username.equals(user.getUsername())){
                        user.setUsername(username);
                        modified = true;
                    }

                    if(!address.isEmpty() && !address.equals(user.getAddress())){
                        user.setAddress(address);
                        modified = true;
                    }
                }
                
                if(modified) {
                    ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().setUser(user);
                    if (request.getParameter("referrer") != null) {
                        response.sendRedirect(request.getParameter("referrer"));
                    } else {
                        if(SecurityHelpers.checkNumeric(request.getParameter("edit")) == 1) {
                            request.getSession().setAttribute("status", "credentialsSuccess");

                            response.sendRedirect("login");

                        } else {
                            //request.getSession().setAttribute("status", "registrationSuccess");
                            response.sendRedirect("profile");
                        }                    
                    }
                }
            } else {
                action_anonymous(request, response);
            }
        } catch (DataException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
         handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private int calculatePercentage(int valueNow, int valueLast) {
        if(valueNow != 0) {
            if(valueLast != 0){
                float support = (float) valueNow / valueLast;
                if(support > 1) {
                    support= (support-1)*100;
                    return (int)support;
                } else {
                    support*=100;
                    return (int)support;
                }
            } else {
                return valueNow * 100;
            }
        } else {
            if(valueLast != 0) {
                return -valueLast * 100;
            } else {
                return 0;
            }
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

        request.setAttribute("title", "Profile");
        request.setAttribute("themeMode", request.getSession().getAttribute("themeMode"));
        request.setAttribute("themeSkin", request.getSession().getAttribute("themeSkin"));
        
        try {
            HttpSession s = request.getSession(false);
            if (s != null) {
                if(SecurityHelpers.checkPermissionScript(request)) {
                    if(request.getParameter("edit") != null) {
                        if(request.getParameter("credentials") != null || request.getParameter("others") != null) {
                            action_edit(request, response);
                        } else {
                            action_showEdit(request, response);
                        }
                    } else {
                        action_profile(request, response);
                    }
                } else {
                    if(s.getAttribute("userid") != null)
                        response.sendRedirect("index");
                    else
                        response.sendRedirect("login");
                }
            } else {
                action_anonymous(request, response);
            }
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Profile servlet";
    }
    // </editor-fold>
}