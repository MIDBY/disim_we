package it.univaq.example.webmarket.data.dao;

import it.univaq.example.webmarket.data.model.Group;
import it.univaq.example.webmarket.data.model.impl.UserRoleEnum;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface GroupDAO {

    Group getGroup(UserRoleEnum value) throws DataException;

    List<Group> getGroups() throws DataException;

}