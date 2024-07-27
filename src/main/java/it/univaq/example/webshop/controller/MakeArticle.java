package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.NewspaperDataLayer;
import it.univaq.example.webshop.data.model.Article;
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
public class MakeArticle extends NewspaperBaseController {

    private void action_article(HttpServletRequest request, HttpServletResponse response, int k) throws IOException, ServletException, TemplateManagerException {
        try {
            Article article = ((NewspaperDataLayer) request.getAttribute("datalayer")).getArticleDAO().getArticle(k);
            if (article != null) {
                request.setAttribute("article", article);
                request.setAttribute("page_title", "Read Article");
                //verrà usato automaticamente il template di outline spcificato tra i context parameters
                //the outlne template specified through the context parameters will be added by the TemplateResult to the specified template
                TemplateResult res = new TemplateResult(getServletContext());
                //aggiungiamo al template un wrapper che ci permette di chiamare la funzione stripSlashes
                //add to the template a wrapper object that allows to call the stripslashes function
                request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
                res.activate("article.ftl.html", request, response);
            } else {
                handleError("Unable to load article", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        int k;
        try {
            k = SecurityHelpers.checkNumeric(request.getParameter("k"));
            action_article(request, response, k);
        } catch (NumberFormatException ex) {
            handleError("Article key not specified", request, response);
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
        return "Render article servlet";
    }// </editor-fold>
}