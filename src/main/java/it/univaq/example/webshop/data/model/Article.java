package it.univaq.example.webshop.data.model;

import it.univaq.framework.data.DataItem;
import java.util.List;

/**
 *
 * @author Giuseppe Della Penna
 */
public interface Article extends DataItem<Integer> {

    Author getAuthor();

    void setAuthor(Author author);

    String getText();

    void setText(String text);

    String getTitle();

    void setTitle(String title);

    List<Image> getImages();

    void setImages(List<Image> images);

    void addImage(Image image);
    
    boolean isPublished();

    Issue getIssue();

    void setIssue(Issue issue);

    int getPage();

    void setPage(int page);

}