package it.univaq.example.webshop.data.model;

import it.univaq.framework.data.DataItem;

/**
 *
 * @author Giuseppe Della Penna
 */
public interface Author extends DataItem<Integer> {

    String getName();

    String getSurname();

    void setName(String name);

    void setSurname(String surname);

}