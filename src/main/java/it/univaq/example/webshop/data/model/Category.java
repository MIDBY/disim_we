package it.univaq.example.webshop.data.model;

import java.util.List;
import it.univaq.framework.data.DataItem;

public interface Category extends DataItem<Integer> {

    String getName();

    void setName(String name);

    Image getImage();

    void setImage(Image image);

    Category getFatherCategory();

    void setFatherCategory(Category fathCategory);

    void setFatherCategoryNull();

    List<Characteristic> getCharacteristics();

    void setCharacteristics(List<Characteristic> characteristics);

    void addCharacteristic(Characteristic characteristic);

    boolean isDeleted();

    void setDeleted(boolean deleted);
    
}
