package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Group;
import it.univaq.example.webshop.data.model.Request;
import it.univaq.example.webshop.data.model.User;
import it.univaq.example.webshop.data.model.impl.RequestStateEnum;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.result.TemplateManagerException;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ManageRequests extends WebshopBaseController {

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
            User user = ((WebshopDataLayer)request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
            Group group = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(user_key);

            request.setAttribute("username", user.getUsername());
            request.setAttribute("group", group.getName());           
            
            List<Request> requests = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequests();
            for(Request r : requests) {
                if(r.getRequestState().equals(RequestStateEnum.CHIUSO)) {
                    requests.remove(r);
                    continue;
                }
                r.getCategory();
                r.getOrdering();
                if(r.getTechnician() != null)
                    r.setTechnician(r.getTechnician());
                r.getRequestCharacteristics();
                r.getProposals();
            }
            request.setAttribute("requests", requests);

            List<Request> closedRequests = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequestsByRequestState(RequestStateEnum.CHIUSO);
            for(Request r : closedRequests) {
                r.getCategory();
                r.getOrdering();
                if(r.getTechnician() != null)
                    r.setTechnician(r.getTechnician());
                r.setRequestCharacteristics(r.getRequestCharacteristics());
                r.setProposals(r.getProposals());
            }
            request.setAttribute("crequests", closedRequests);
            
            res.activate("listRequests.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        request.setAttribute("title", "Requests");
        request.setAttribute("userid", request.getSession().getAttribute("userid"));

        try {
            HttpSession s = request.getSession(false);
            if (s != null) {
                action_default(request, response);
            } else {
                action_anonymous(request, response);
            }
        } catch (NumberFormatException ex) {
            handleError("Invalid number submitted", request, response);
        } catch (IOException | TemplateManagerException ex) {
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
        return "Manage Requests servlet";
    }// </editor-fold>
  

}