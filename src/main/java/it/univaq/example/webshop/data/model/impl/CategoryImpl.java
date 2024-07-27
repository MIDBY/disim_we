package it.univaq.example.webshop.data.model.impl;

import java.util.List;

import it.univaq.example.webshop.data.model.Category;
import it.univaq.example.webshop.data.model.Characteristic;
import it.univaq.example.webshop.data.model.Image;
import it.univaq.framework.data.DataItemImpl;

public class CategoryImpl extends DataItemImpl<Integer> implements Category {

    private String name;
    private Image image;
    private Category fatherCategory;
    private List<Characteristic> characteristics;


    public CategoryImpl() {
        super();
        name = "";
        image = null;
        fatherCategory = null;
        characteristics = null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public Category getFatherCategory() {
        return fatherCategory;
    }

    @Override
    public void setFatherCategory(Category fathCategory) {
        fatherCategory = fathCategory;
    }

    @Override
    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

    @Override
    public void setCharacteristics(List<Characteristic> characteristics) {
        this.characteristics = characteristics;
    }

    @Override
    public void addCharacteristic(Characteristic characteristic) {
        characteristics.add(characteristic);
    }


    
}
