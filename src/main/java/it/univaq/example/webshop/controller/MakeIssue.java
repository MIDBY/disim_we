package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.NewspaperDataLayer;
import it.univaq.example.webshop.data.model.Issue;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.SplitSlashesFmkExt;
import it.univaq.framework.result.TemplateManagerException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.security.SecurityHelpers;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Giuseppe
 */
public class MakeIssue extends NewspaperBaseController {

    private void action_issue(HttpServletRequest request, HttpServletResponse response, int n) throws IOException, ServletException, TemplateManagerException {
        try {
            Issue issue = ((NewspaperDataLayer) request.getAttribute("datalayer")).getIssueDAO().getIssue(n);
            if (issue != null) {
                request.setAttribute("issue", issue);
                request.setAttribute("page_title", "Issue #" + issue.getNumber());
                //verr� usato automaticamente il template di outline spcificato tra i context parameters
                //the outlne template specified through the context parameters will be added by the TemplateResult to the specified template
                TemplateResult res = new TemplateResult(getServletContext());
                //aggiungiamo al template un wrapper che ci permette di chiamare la funzione stripSlashes
                //add to the template a wrapper object that allows to call the stripslashes function
                request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
                res.activate("issue.ftl.html", request, response);
            } else {
                handleError("Unable to load issue", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        int n;
        try {
            n = SecurityHelpers.checkNumeric(request.getParameter("n"));
            action_issue(request, response, n);
        } catch (NumberFormatException ex) {
            handleError("Invalid number specified", request, response);
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
        return "Make Issue servlet";
    }// </editor-fold>
}