package it.univaq.example.webshop.data.dao;

import it.univaq.example.webshop.data.model.Author;
import it.univaq.framework.data.DataException;
import java.util.List;

/**
 *
 * @author Giuseppe Della Penna
 */
public interface AuthorDAO {

    //metodo "factory" che permettono di creare
    //e inizializzare opportune implementazioni
    //delle interfacce del modello dati, nascondendo
    //all'utente tutti i particolari
    //factory method to create and initialize
    //suitable implementations of the data model interfaces,
    //hiding all the implementation details
    Author createAuthor();

    Author getAuthor(int author_key) throws DataException;

    List<Author> getAuthors() throws DataException;

}