package it.univaq.example.webmarket.data.model.impl.proxy;

import it.univaq.example.webmarket.data.dao.CategoryDAO;
import it.univaq.example.webmarket.data.dao.CharacteristicDAO;
import it.univaq.example.webmarket.data.dao.ImageDAO;
import it.univaq.example.webmarket.data.model.impl.CategoryImpl;
import it.univaq.example.webmarket.data.model.Category;
import it.univaq.example.webmarket.data.model.Characteristic;
import it.univaq.example.webmarket.data.model.Image;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CategoryProxy extends CategoryImpl implements DataItemProxy {

    protected boolean modified;
    protected int fatherCategory_key = 0;
    protected int image_key = 0;

    protected DataLayer dataLayer;

    public CategoryProxy(DataLayer d) {
        super();
        //dependency injection
        this.dataLayer = d;
        this.modified = false;
        this.fatherCategory_key = 0;
        this.image_key = 0;
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
    public Image getImage() {
        if (super.getImage() == null && image_key > 0) {
            try {
                super.setImage(((ImageDAO) dataLayer.getDAO(Image.class)).getImage(image_key));
            } catch (DataException ex) {
                Logger.getLogger(CategoryProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getImage();
    }

    @Override
    public void setImage(Image image) {
        super.setImage(image);
        if (image != null) {
            this.image_key = image.getKey();
        } else {
            this.image_key = 0;
        }
        this.modified = true;
    }

    @Override
    public Category getFatherCategory() {
        if (super.getFatherCategory() == null && fatherCategory_key > 0) {
            try {
                super.setFatherCategory(((CategoryDAO) dataLayer.getDAO(Category.class)).getCategory(fatherCategory_key));
            } catch (DataException ex) {
                Logger.getLogger(CategoryProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getFatherCategory();
    }

    @Override
    public void setFatherCategory(Category fatherCategory) {
        super.setFatherCategory(fatherCategory);
        this.fatherCategory_key = fatherCategory.getKey();
        this.modified = true;
    }

    @Override
    public List<Characteristic> getCharacteristics() {
        if (super.getCharacteristics() == null) {
            try {
                super.setCharacteristics(((CharacteristicDAO) dataLayer.getDAO(Characteristic.class)).getCharacteristicsByCategory(this.getKey()));
            } catch (DataException ex) {
                Logger.getLogger(CategoryProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getCharacteristics();
    }

    @Override
    public void setCharacteristics(List<Characteristic> characteristics) {
        super.setCharacteristics(characteristics);
        this.modified = true;
    }

    @Override
    public void addCharacteristic(Characteristic characteristic) {
        super.addCharacteristic(characteristic);
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

    public void setFatherCategoryKey(int fatherCategory_key) {
        this.fatherCategory_key = fatherCategory_key;
        //resettiamo la cache dell'autore
        super.setFatherCategory(null);
    }

    public void setImageKey(int image_key) {
        this.image_key = image_key;
        super.setImage(null);
    }
}