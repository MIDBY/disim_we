package it.univaq.example.webmarket.data.dao;

import java.util.List;
import it.univaq.example.webmarket.data.model.Category;
import it.univaq.example.webmarket.data.model.Image;
import it.univaq.framework.data.DataException;

public interface ImageDAO {

    //metodo "factory" che permettono di creare
    //e inizializzare opportune implementazioni
    //delle interfacce del modello dati, nascondendo
    //all'utente tutti i particolari
    //factory method to create and initialize
    //suitable implementations of the data model interfaces,
    //hiding all the implementation details
    Image createImage();

    List<Image> getImages() throws DataException;

    Image getImage(int image_key) throws DataException;

    Image getImageByCategory(Category category) throws DataException;

    void setImage(Image image) throws DataException;

    void deleteImage(Image image) throws DataException;
}