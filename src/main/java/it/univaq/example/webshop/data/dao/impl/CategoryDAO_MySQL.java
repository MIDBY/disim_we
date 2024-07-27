package it.univaq.example.webshop.data.dao.impl;

import it.univaq.example.webshop.data.dao.CategoryDAO;
import it.univaq.example.webshop.data.model.Category;
import it.univaq.example.webshop.data.model.impl.proxy.CategoryProxy;
import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import it.univaq.framework.data.DataLayer;
import it.univaq.framework.data.OptimisticLockException;

public class CategoryDAO_MySQL extends DAO implements CategoryDAO {

    private PreparedStatement sCategoryByID, sCategories, sFatherCategories, sCategoriesSonsOf, iCategory, uCategory, dCategory;

    public CategoryDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sCategoryByID = connection.prepareStatement("SELECT * FROM categoria WHERE id=?");
            sFatherCategories = connection.prepareStatement("SELECT id FROM categoria WHERE idCategoriaPadre=NULL");
            sCategories = connection.prepareStatement("SELECT id FROM categoria");
            sCategoriesSonsOf = connection.prepareStatement("SELECT id FROM categoria WHERE idCategoriaPadre=?");
            iCategory = connection.prepareStatement("INSERT INTO categoria (nome,idCategoriaPadre,idImmagine) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
            uCategory = connection.prepareStatement("UPDATE categoria SET nome=?,idCategoriaPadre=?,idImmagine=?,versione=? WHERE id=? and versione=?");
            dCategory = connection.prepareStatement("DELETE FROM categoria WHERE id=?");

        } catch (SQLException ex) {
            throw new DataException("Error initializing webshop data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        //anche chiudere i PreparedStamenent � una buona pratica...
        //also closing PreparedStamenents is a good practice...
        try {

            sCategoryByID.close();
            sFatherCategories.close();
            sCategories.close();
            iCategory.close();
            uCategory.close();
            dCategory.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        super.destroy();
    }

    @Override
    public Category createCategory() {
        return new CategoryProxy(getDataLayer());
    }

    //helper
    private CategoryProxy createCategory(ResultSet rs) throws DataException {
        CategoryProxy a = (CategoryProxy) createCategory();
        try {
            a.setKey(rs.getInt("id"));
            a.setName(rs.getString("nome"));
            a.setFatherCategoryKey(rs.getInt("idCategoriaPadre"));
            a.setImageKey(rs.getInt("idImmagine"));
            a.setVersion(rs.getLong("versione"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create category object form ResultSet", ex);
        }
        return a;
    }

    @Override
    public Category getCategory(int category_key) throws DataException {
        Category a = null;
        if (dataLayer.getCache().has(Category.class, category_key)) {
            a = dataLayer.getCache().get(Category.class, category_key);
        } else {
            try {
                sCategoryByID.setInt(1, category_key);
                try (ResultSet rs = sCategoryByID.executeQuery()) {
                    if (rs.next()) {
                        a = createCategory(rs);
                        dataLayer.getCache().add(Category.class, a);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load category by ID", ex);
            }
        }
        return a;
    }

    @Override
    public List<Category> getFatherCategories() throws DataException {
        List<Category> result = new ArrayList<Category>();
        try {
            try (ResultSet rs = sFatherCategories.executeQuery()) {
                while (rs.next()) {
                    result.add((Category) getCategory(rs.getInt("id")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load father categories", ex);
        }
        return result;
    }

    @Override
    public List<Category> getCategories() throws DataException {
        List<Category> result = new ArrayList<Category>();
        try (ResultSet rs = sCategories.executeQuery()) {
            while (rs.next()) {
                result.add((Category) getCategory(rs.getInt("id")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load categories", ex);
        }
        return result;
    }

    @Override
    public List<Category> getCategoriesSonsOf(int category_key) throws DataException {
        List<Category> result = new ArrayList<Category>();
        
        try {
            sCategoriesSonsOf.setInt(1, category_key);
            try (ResultSet rs = sCategoriesSonsOf.executeQuery()) {
                while (rs.next()) {
                    result.add((Category) getCategory(rs.getInt("id")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load categories sons", ex);
        }
        return result;
    }

    @Override
    public void setCategory(Category category) throws DataException {
        try {
            if (category.getKey() != null && category.getKey() > 0) { //update
                if (category instanceof DataItemProxy && !((DataItemProxy) category).isModified()) {
                    return;
                }
                uCategory.setString(1, category.getName());
                if (category.getFatherCategory() != null) {
                    uCategory.setInt(2, category.getFatherCategory().getKey());
                } else {
                    uCategory.setNull(2, java.sql.Types.INTEGER);
                }                
                if (category.getImage() != null) {
                    uCategory.setInt(3, category.getImage().getKey());
                } else {
                    uCategory.setNull(3, java.sql.Types.INTEGER);
                }       
                long current_version = category.getVersion();
                long next_version = current_version + 1;

                uCategory.setLong(4, next_version);
                uCategory.setInt(5, category.getKey());
                uCategory.setLong(6, current_version);

                if (uCategory.executeUpdate() == 0) {
                    throw new OptimisticLockException(category);
                } else {
                    category.setVersion(next_version);
                }
            } else { //insert
                iCategory.setString(1, category.getName());
                if (category.getFatherCategory() != null) {
                    iCategory.setInt(2, category.getFatherCategory().getKey());
                } else {
                    iCategory.setNull(2, java.sql.Types.INTEGER);
                }
                if (category.getImage() != null) {
                    iCategory.setInt(3, category.getImage().getKey());
                } else {
                    iCategory.setNull(3, java.sql.Types.INTEGER);
                }
                if (iCategory.executeUpdate() == 1) {
                    try (ResultSet keys = iCategory.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            category.setKey(key);
                            dataLayer.getCache().add(Category.class, category);
                        }
                    }
                }
            }
            if (category instanceof DataItemProxy) {
                ((DataItemProxy) category).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store category", ex);
        }
    }

    @Override
    public void deleteCategory(Category category) throws DataException {
        try {
            if (category.getKey() != null && category.getKey() > 0) { //delete
                dCategory.setInt(1, category.getKey());
                if (dCategory.executeUpdate() == 0) {
                    throw new OptimisticLockException(category);
                } else {
                    dataLayer.getCache().delete(Category.class, category);
                }
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to delete category", ex);
        }
    }
}