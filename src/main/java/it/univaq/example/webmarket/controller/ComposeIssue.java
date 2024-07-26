package it.univaq.example.webmarket.controller;

import it.univaq.example.webmarket.data.dao.impl.NewspaperDataLayer;
import it.univaq.example.webmarket.data.model.Article;
import it.univaq.example.webmarket.data.model.Issue;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.SplitSlashesFmkExt;
import it.univaq.framework.result.TemplateManagerException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.security.SecurityHelpers;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Giuseppe
 */
public class ComposeIssue extends NewspaperBaseController {

    //creiamo e inizializziamo gli array statici per i campi data
    //create and initialize static arrays for date fields
    private static final List<Integer> days;
    private static final List<Integer> months;
    private static final List<Integer> years;

    static {
        days = new ArrayList<Integer>();
        months = new ArrayList<Integer>();
        years = new ArrayList<Integer>();
        for (int i = 1; i <= 31; ++i) {
            days.add(i);
        }
        for (int i = 1; i <= 12; ++i) {
            months.add(i);
        }
        int base_year = LocalDate.now().get(ChronoField.YEAR);
        for (int i = -20; i <= 3; ++i) {
            years.add(base_year + i);
        }

    }

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            request.setAttribute("issues", ((NewspaperDataLayer) request.getAttribute("datalayer")).getIssueDAO().getIssues());
            res.activate("compose_list.ftl.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_compose(HttpServletRequest request, HttpServletResponse response, int issue_key) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            //aggiungiamo al template un wrapper che ci permette di chiamare la funzione stripSlashes
            //add to the template a wrapper object that allows to call the stripslashes function
            request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
            request.setAttribute("days", days);
            request.setAttribute("months", months);
            request.setAttribute("years", years);
            if (issue_key > 0) {
                Issue issue = ((NewspaperDataLayer) request.getAttribute("datalayer")).getIssueDAO().getIssue(issue_key);
                if (issue != null) {
                    request.setAttribute("issue", issue);
                    request.setAttribute("unused", ((NewspaperDataLayer) request.getAttribute("datalayer")).getArticleDAO().getUnassignedArticles());
                    request.setAttribute("used", ((NewspaperDataLayer) request.getAttribute("datalayer")).getArticleDAO().getArticles(issue));
                    res.activate("compose_single.ftl.html", request, response);
                } else {
                    handleError("Undefined issue", request, response);

                }
            } else {
                //issue_key==0 indica un nuovo numero 
                //issue_key==0 indicates a new issue
                Issue issue = ((NewspaperDataLayer) request.getAttribute("datalayer")).getIssueDAO().createIssue();
                issue.setNumber(((NewspaperDataLayer) request.getAttribute("datalayer")).getIssueDAO().getLatestIssueNumber() + 1);
                issue.setDate(LocalDate.now());
                request.setAttribute("issue", issue);
                //forza prima a compilare i dati essenziali per creare un numero
                //forces first to compile the mandatory fields to create an issue
                request.setAttribute("unused", Collections.EMPTY_LIST);
                request.setAttribute("used", Collections.EMPTY_LIST);
                res.activate("compose_single.ftl.html", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_set_properties(HttpServletRequest request, HttpServletResponse response, int issue_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Issue issue;
            if (issue_key > 0) {
                issue = ((NewspaperDataLayer) request.getAttribute("datalayer")).getIssueDAO().getIssue(issue_key);
            } else {
                issue = ((NewspaperDataLayer) request.getAttribute("datalayer")).getIssueDAO().createIssue();
            }
            if (issue != null
                    && request.getParameter("number") != null
                    && request.getParameter("day") != null
                    && request.getParameter("month") != null
                    && request.getParameter("year") != null) {
                issue.setNumber(SecurityHelpers.checkNumeric(request.getParameter("number")));
                LocalDate date = LocalDate.of(
                        SecurityHelpers.checkNumeric(request.getParameter("year")),
                        SecurityHelpers.checkNumeric(request.getParameter("month")),
                        SecurityHelpers.checkNumeric(request.getParameter("day")));
                issue.setDate(date);
                ((NewspaperDataLayer) request.getAttribute("datalayer")).getIssueDAO().storeIssue(issue);
                //delega il resto del processo all'azione compose
                //delegates the rest of the process to the compose action
                action_compose(request, response, issue.getKey());
            } else {
                handleError("Cannot update issue: insufficient parameters", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_add_article(HttpServletRequest request, HttpServletResponse response, int issue_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Issue issue = ((NewspaperDataLayer) request.getAttribute("datalayer")).getIssueDAO().getIssue(issue_key);

            if (issue != null && request.getParameter("aarticle") != null && request.getParameter("page") != null) {
                Article article = ((NewspaperDataLayer) request.getAttribute("datalayer")).getArticleDAO().getArticle(SecurityHelpers.checkNumeric(request.getParameter("aarticle")));
                if (article != null) {
                    article.setIssue(issue);
                    article.setPage(SecurityHelpers.checkNumeric(request.getParameter("page")));
                    ((NewspaperDataLayer) request.getAttribute("datalayer")).getArticleDAO().storeArticle(article);
                    //delega il resto del processo all'azione compose
                    //delegates the rest of the process to the compose action
                    action_compose(request, response, issue_key);
                } else {
                    handleError("Cannot add undefined article", request, response);
                }
            } else {
                handleError("Cannot add article: insufficient parameters", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_remove_article(HttpServletRequest request, HttpServletResponse response, int issue_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Issue issue = ((NewspaperDataLayer) request.getAttribute("datalayer")).getIssueDAO().getIssue(issue_key);
            if (issue != null && request.getParameter("rarticle") != null) {
                Article article = ((NewspaperDataLayer) request.getAttribute("datalayer")).getArticleDAO().getArticle(SecurityHelpers.checkNumeric(request.getParameter("rarticle")));
                if (article != null) {
                    if (article.getIssue().getKey() == issue.getKey()) {
                        article.setPage(0);
                        article.setIssue(null);
                        ((NewspaperDataLayer) request.getAttribute("datalayer")).getArticleDAO().storeArticle(article);
                    }
                    //delega il resto del processo all'azione di default
                    //delegates the rest of the process to the default action
                    action_compose(request, response, issue_key);
                } else {
                    handleError("Cannot remove undefined article", request, response);
                }
            } else {
                handleError("Cannot remove article: insufficient parameters", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        request.setAttribute("page_title", "Compose Issue");

        int issue_key;
        try {
            if (request.getParameter("n") != null) {
                issue_key = SecurityHelpers.checkNumeric(request.getParameter("n"));
                if (request.getParameter("add") != null) {
                    action_add_article(request, response, issue_key);
                } else if (request.getParameter("remove") != null) {
                    action_remove_article(request, response, issue_key);
                } else if (request.getParameter("update") != null) {
                    action_set_properties(request, response, issue_key);
                } else {
                    action_compose(request, response, issue_key);
                }
            } else {
                action_default(request, response);
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
        return "Compose Issue servlet";
    }// </editor-fold>

}