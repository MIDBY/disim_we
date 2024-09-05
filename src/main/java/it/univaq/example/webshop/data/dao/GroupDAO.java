package it.univaq.example.webshop.data.dao;

import it.univaq.example.webshop.data.model.Group;
import it.univaq.example.webshop.data.model.impl.UserRoleEnum;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface GroupDAO {
    
    Group createGroup();

    Group getGroup (int group_key) throws DataException;

    Group getGroupByUser(int user_key) throws DataException;

    Group getGroupByService(int service_key) throws DataException;

    List<Group> getGroups() throws DataException;

    Group getGroupByName(UserRoleEnum value) throws DataException;

}