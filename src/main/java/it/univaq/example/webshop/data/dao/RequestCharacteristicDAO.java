package it.univaq.example.webshop.data.dao;

import it.univaq.example.webshop.data.model.RequestCharacteristic;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface RequestCharacteristicDAO {

    RequestCharacteristic createRequestCharacteristic();

    RequestCharacteristic getRequestCharacteristic(int requestCharacteristic_key) throws DataException;

    List<RequestCharacteristic> getRequestCharacteristics() throws DataException;

    List<RequestCharacteristic> getRequestCharacteristicsByRequest(int request_key) throws DataException;

    List<RequestCharacteristic> getRequestCharacteristicsByCharacteristic(int char_key) throws DataException;

    void setRequestCharacteristic(RequestCharacteristic requestCharacteristic) throws DataException;

    void deleteRequestCharacteristic(RequestCharacteristic requestCharacteristic) throws DataException;

}