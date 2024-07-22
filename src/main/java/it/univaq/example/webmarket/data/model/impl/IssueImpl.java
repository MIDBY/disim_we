package it.univaq.example.webmarket.data.model.impl;

import it.univaq.example.webmarket.data.model.Article;
import it.univaq.example.webmarket.data.model.Image;
import it.univaq.example.webmarket.data.model.Issue;
import it.univaq.framework.data.DataItemImpl;
import java.time.LocalDate;

import java.util.List;

public class IssueImpl  extends DataItemImpl<Integer> implements Issue {


    private int number;
    private LocalDate date;
    private List<Article> articles;
    private List<Image> images;

    public IssueImpl() {
        super();
        number = 0;
        date = null;
        articles = null;
        images = null;
    }

   

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public void setNumber(int number) {
        this.number = number;

    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public List<Article> getArticles() {
        return articles;
    }

    @Override
    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public List<Image> getImages() {
        return images;
    }

    @Override
    public void setImages(List<Image> images) {
        this.images = images;
    }

 

}