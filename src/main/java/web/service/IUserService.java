package web.service;

import web.model.Role;
import web.model.User;
import web.myExcetion.SaveObjectException;

import java.util.List;
import java.util.Set;

public interface IUserService {
    void registrationUser(User newUser) throws SaveObjectException;

    List<User> getAllUsers();

    Set<Role> getSetRoles(String... roles);

    void removeUserById(Long id);

    User getUserById(Long id);

    void updateUser(User updateUser) throws SaveObjectException;
}
