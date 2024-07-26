package it.univaq.example.webmarket.data.dao;

import it.univaq.example.webmarket.data.model.Group;
import it.univaq.example.webmarket.data.model.impl.UserRoleEnum;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface GroupDAO {
    
    Group createGroup();

    Group getGroup (int group_key) throws DataException;

    List<Group> getGroups() throws DataException;

    Group getGroupByName(UserRoleEnum value) throws DataException;

}