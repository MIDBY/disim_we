package it.univaq.example.webshop.data.dao;

import java.util.List;

import it.univaq.example.webshop.data.model.Category;
import it.univaq.framework.data.DataException;

public interface CategoryDAO {

    Category createCategory();

    Category getCategory(int category_key) throws DataException;

    List<Category> getCategories() throws DataException;

    List<Category> getCategoriesByDeleted(boolean deleted) throws DataException;

    List<Category> getFatherCategories() throws DataException;

    List<Category> getCategoriesSonsOf(int category_key) throws DataException;

    List<Category> getMostSoldCategories() throws DataException;

    List<Category> getCategoriesByImage(int image_key) throws DataException;

    void setCategory(Category category) throws DataException;

    void deleteCategory(Category category) throws DataException;
    
}