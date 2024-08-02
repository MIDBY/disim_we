package it.univaq.example.webshop.data.dao;

import java.util.List;

import it.univaq.example.webshop.data.model.User;
import it.univaq.example.webshop.data.model.impl.UserRoleEnum;
import it.univaq.framework.data.DataException;

public interface UserDAO {

    //metodo "factory" che permettono di creare
    //e inizializzare opportune implementazioni
    //delle interfacce del modello dati, nascondendo
    //all'utente tutti i particolari
    //factory method to create and initialize
    //suitable implementations of the data model interfaces,
    //hiding all the implementation details
    User createUser();

    User getUser(int user_key) throws DataException;
    
    User getUserByUsername(String username) throws DataException;

    User getUserByEmail(String email) throws DataException;

    List<User> getUsersByGroup(int group_key) throws DataException;

    List<User> getUsersByAccepted(boolean accepted) throws DataException;

    void setUser(User user) throws DataException;

    void changeUserGroup(int user_key, UserRoleEnum value) throws DataException;
}