package it.univaq.example.webshop.data.dao;

import java.util.List;

import it.univaq.example.webshop.data.model.Article;
import it.univaq.example.webshop.data.model.Issue;
import it.univaq.framework.data.DataException;

/**
 *
 * @author Giuseppe Della Penna
 */
public interface ArticleDAO {

    //metodo "factory" che permettono di creare
    //e inizializzare opportune implementazioni
    //delle interfacce del modello dati, nascondendo
    //all'utente tutti i particolari
    //factory method to create and initialize
    //suitable implementations of the data model interfaces,
    //hiding all the implementation details
    Article createArticle();

    Article getArticle(int article_key) throws DataException;

    List<Article> getArticles(Issue issue) throws DataException;

    List<Article> getArticles() throws DataException;

    List<Article> getUnassignedArticles() throws DataException;

    void storeArticle(Article article) throws DataException;
    
    void publishArticle(Article article, Issue issue, int page) throws DataException;

}