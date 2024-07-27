package it.univaq.example.webshop.data.model.impl.proxy;

import java.util.logging.Level;
import java.util.logging.Logger;

import it.univaq.example.webshop.data.dao.CategoryDAO;
import it.univaq.example.webshop.data.model.Category;
import it.univaq.example.webshop.data.model.impl.CharacteristicImpl;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;


public class CharacteristicProxy extends CharacteristicImpl implements DataItemProxy  {

    protected boolean modified;
    protected DataLayer dataLayer;
    protected Integer category_key;

    public CharacteristicProxy(DataLayer d) {
        super();
        //dependency injection
        this.dataLayer = d;
        this.modified = false;
        this.category_key = 0;
    }
  
    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        this.modified = true;
    }

    @Override
    public Category getCategory() {
        //notare come la categoria in relazione venga caricata solo su richiesta
        if (super.getCategory() == null && category_key > 0) {
            try {
                super.setCategory(((CategoryDAO) dataLayer.getDAO(Category.class)).getCategory(category_key));
            } catch (DataException ex) {
                Logger.getLogger(CharacteristicProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getCategory();
    }

    @Override
    public void setCategory(Category category) {
        super.setCategory(category);
        if (category != null) {
            this.category_key = category.getKey();
        } else {
            this.category_key = 0;
        }
        this.modified = true;
    }

    @Override
    public void setDefaultValues(String defaultValues) {
        super.setDefaultValues(defaultValues);
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

    public void setCategoryKey(int category_key) {
        this.category_key = category_key;
        super.setCategory(null);
    }

}