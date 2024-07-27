package it.univaq.example.webshop.data.dao;

import it.univaq.example.webshop.data.model.Characteristic;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface CharacteristicDAO {

    Characteristic createCharacteristic();

    Characteristic getCharacteristic(int characteristic_key) throws DataException;

    List<Characteristic> getCharacteristics() throws DataException;

    List<Characteristic> getCharacteristicsByCategory(int category_key) throws DataException;

    void setCharacteristic(Characteristic characteristic) throws DataException;

    void deleteCharacteristic(Characteristic characteristic) throws DataException;
}