package it.univaq.example.webshop.data.model.impl.proxy;

import it.univaq.example.webshop.data.dao.ArticleDAO;
import it.univaq.example.webshop.data.dao.ImageDAO;
import it.univaq.example.webshop.data.model.Article;
import it.univaq.example.webshop.data.model.Image;
import it.univaq.example.webshop.data.model.impl.IssueImpl;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class IssueProxy extends IssueImpl implements DataItemProxy  {

    protected boolean modified;
    protected DataLayer dataLayer;

    public IssueProxy(DataLayer d) {
        super();
        //dependency injection
        this.dataLayer = d;
        this.modified = false;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public void setNumber(int number) {
        super.setNumber(number);
        this.modified = true;
    }

 

    @Override
    public void setDate(LocalDate date) {
        super.setDate(date);
        this.modified = true;
    }

    @Override
    public List<Article> getArticles() {
        if (super.getArticles() == null) {
            try {
                super.setArticles(((ArticleDAO) dataLayer.getDAO(Article.class)).getArticles(this));
            } catch (DataException ex) {
                Logger.getLogger(IssueProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getArticles();
    }

    @Override
    public void setArticles(List<Article> articles) {
        super.setArticles(articles);
        this.modified = true;
    }

    @Override
    public List<Image> getImages() {
        if (super.getImages() == null) {
            try {
                super.setImages(((ImageDAO) dataLayer.getDAO(Image.class)).getImages());
            } catch (DataException ex) {
                Logger.getLogger(IssueProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getImages();
    }

    @Override
    public void setImages(List<Image> images) {
        super.setImages(images);
        this.modified = true;
    }

    //METODI DEL PROXY
    //PROXY-ONLY METHODS
    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

    @Override
    public boolean isModified() {
        return modified;
    }
}