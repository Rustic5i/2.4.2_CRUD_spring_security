package web.dao;

import web.model.Role;
import web.model.User;
import web.myExcetion.SaveObjectException;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Set;

public interface DAO {
    void saveUser(User user) throws SaveObjectException;

    void updateUser(User updateUser) throws SaveObjectException;

    void removeUserById(Long id);

    List<User> getAllUsers();

    User getUserById(Long id);

    User findByUsername(String username);

    Set<Role> getSetRoles(String... roles);
}
