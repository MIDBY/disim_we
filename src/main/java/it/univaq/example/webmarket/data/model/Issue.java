package it.univaq.example.webmarket.data.model;

import it.univaq.framework.data.DataItem;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Giuseppe Della Penna
 */
public interface Issue extends DataItem<Integer> {

    int getNumber();

    void setNumber(int number);

    LocalDate getDate();

    void setDate(LocalDate date);

    List<Article> getArticles();

    List<Image> getImages();

    void setArticles(List<Article> articles);

    void setImages(List<Image> images);
}