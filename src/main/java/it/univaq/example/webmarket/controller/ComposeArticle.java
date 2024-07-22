package it.univaq.example.webmarket.controller;

import it.univaq.example.webmarket.data.dao.impl.NewspaperDataLayer;
import it.univaq.example.webmarket.data.model.Article;
import it.univaq.example.webmarket.data.model.Author;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.result.SplitSlashesFmkExt;
import it.univaq.framework.result.TemplateManagerException;
import it.univaq.framework.security.SecurityHelpers;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Giuseppe
 */
public class ComposeArticle extends NewspaperBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            //aggiungiamo al template un wrapper che ci permette di chiamare la funzione stripSlashes
            //add to the template a wrapper object that allows to call the stripslashes function
            request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
            request.setAttribute("articles", ((NewspaperDataLayer) request.getAttribute("datalayer")).getArticleDAO().getArticles());
            res.activate("write_list.ftl.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_write(HttpServletRequest request, HttpServletResponse response, int article_key) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            //aggiungiamo al template un wrapper che ci permette di chiamare la funzione stripSlashes
            //add to the template a wrapper object that allows to call the stripslashes function
            request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
            List<Author> authors = ((NewspaperDataLayer) request.getAttribute("datalayer")).getAuthorDAO().getAuthors();
            request.setAttribute("authors", authors);
            if (article_key > 0) {
                Article article = ((NewspaperDataLayer) request.getAttribute("datalayer")).getArticleDAO().getArticle(article_key);
                if (article != null) {
                    request.setAttribute("article", article);
                    res.activate("write_single.ftl.html", request, response);
                } else {
                    handleError("Undefined article", request, response);
                }
            } else {
                //article_key==0 indica un nuovo numero 
                //article_key==0 indicates a new issue
                Article article = ((NewspaperDataLayer) request.getAttribute("datalayer")).getArticleDAO().createArticle();
                request.setAttribute("article", article);
                res.activate("write_single.ftl.html", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_update(HttpServletRequest request, HttpServletResponse response, int article_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Article article;
            if (article_key > 0) {
                article = ((NewspaperDataLayer) request.getAttribute("datalayer")).getArticleDAO().getArticle(article_key);
            } else {
                article = ((NewspaperDataLayer) request.getAttribute("datalayer")).getArticleDAO().createArticle();
            }
            if (article != null && request.getParameter("author") != null && request.getParameter("title") != null && !request.getParameter("title").isEmpty()) {
                Author author = ((NewspaperDataLayer) request.getAttribute("datalayer")).getAuthorDAO().getAuthor(SecurityHelpers.checkNumeric(request.getParameter("author")));
                if (author != null) {
                    article.setTitle(SecurityHelpers.addSlashes(request.getParameter("title")));
                    article.setAuthor(author);
                    if (request.getParameter("text") != null) {
                        article.setText(SecurityHelpers.addSlashes(request.getParameter("text")));
                    }
                    ((NewspaperDataLayer) request.getAttribute("datalayer")).getArticleDAO().storeArticle(article);
                    //delega il resto del processo all'azione write
                    //delegates the rest of the process to the write action
                    action_write(request, response, article.getKey());
                } else {
                    handleError("Cannot update article: undefined author", request, response);
                }
            } else {
                handleError("Cannot update article: insufficient parameters", request, response);

            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        request.setAttribute("page_title", "Write Article");

        int article_key;
        try {
            if (request.getParameter("k") != null) {
                article_key = SecurityHelpers.checkNumeric(request.getParameter("k"));
                if (request.getParameter("update") != null) {
                    action_update(request, response, article_key);
                } else {
                    action_write(request, response, article_key);
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
        return "Write Article servlet";
    }// </editor-fold>
  

}