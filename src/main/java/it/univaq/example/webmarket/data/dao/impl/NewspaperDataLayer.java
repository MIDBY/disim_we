package it.univaq.example.webmarket.data.dao.impl;

import it.univaq.example.webmarket.data.dao.ArticleDAO;
import it.univaq.example.webmarket.data.dao.AuthorDAO;
import it.univaq.example.webmarket.data.dao.ImageDAO;
import it.univaq.example.webmarket.data.dao.IssueDAO;
import it.univaq.example.webmarket.data.dao.UserDAO;
import it.univaq.example.webmarket.data.model.Article;
import it.univaq.example.webmarket.data.model.Author;
import it.univaq.example.webmarket.data.model.Image;
import it.univaq.example.webmarket.data.model.Issue;
import it.univaq.example.webmarket.data.model.User;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 *
 * @author Giuseppe Della Penna
 */
public class NewspaperDataLayer extends DataLayer {

    public NewspaperDataLayer(DataSource datasource) throws SQLException {
        super(datasource);
    }

    @Override
    public void init() throws DataException {
        //registriamo i nostri dao
        //register our daos
        registerDAO(Article.class, new ArticleDAO_MySQL(this));
        registerDAO(Author.class, new AuthorDAO_MySQL(this));
        registerDAO(Issue.class, new IssueDAO_MySQL(this));
        registerDAO(Image.class, new ImageDAO_MySQL(this));
        registerDAO(User.class, new UserDAO_MySQL(this));
    }

    //helpers    
    public ArticleDAO getArticleDAO() {
        return (ArticleDAO) getDAO(Article.class);
    }

    public AuthorDAO getAuthorDAO() {
        return (AuthorDAO) getDAO(Author.class);
    }

    public IssueDAO getIssueDAO() {
        return (IssueDAO) getDAO(Issue.class);
    }

    public ImageDAO getImageDAO() {
        return (ImageDAO) getDAO(Image.class);
    }
    
     public UserDAO getUserDAO() {
        return (UserDAO) getDAO(User.class);
    }

}