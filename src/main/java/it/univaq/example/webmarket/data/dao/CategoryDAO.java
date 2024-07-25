package it.univaq.example.webmarket.data.dao;

import java.util.List;
import it.univaq.example.webmarket.data.model.Category;
import it.univaq.framework.data.DataException;

public interface CategoryDAO {

    Category createCategory();

    Category getCategory(int category_key) throws DataException;

    List<Category> getCategories() throws DataException;

    List<Category> getFatherCategories() throws DataException;

    List<Category> getCategoriesSonsOf(int category_key) throws DataException;

    void setCategory(Category category) throws DataException;
    
}