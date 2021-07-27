package web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.dao.DAO;
import web.model.Role;
import web.model.User;
import web.myExcetion.SaveObjectException;

import java.util.List;
import java.util.Set;

@Service
public class UserService implements UserDetailsService, IUserService {

    private DAO dao;

    @Autowired
    public UserService(DAO dao) {
        this.dao = dao;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User  user = findByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException(String.format("User '%s' not found ", username));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), user.getAuthorities());
    }

    private User findByUsername (String username){
        return dao.findByUsername(username);
    }

    @Override
    public void registrationUser(User newUser) throws SaveObjectException {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setAge(newUser.getAge());
        user.setEmail(newUser.getEmail());
        user.setRoles(newUser.getRoles());
        user.setPassword(encoder.encode(newUser.getPassword()));
        dao.saveUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        return dao.getAllUsers();
    }

    @Override
    public Set<Role> getSetRoles(String... roles) {
        return dao.getSetRoles(roles);
    }

    @Override
    public void removeUserById(Long id) {
        dao.removeUserById(id);
    }

    @Override
    public User getUserById(Long id) {
        return dao.getUserById(id);
    }

    @Override
    public void updateUser(User updateUser) throws SaveObjectException {
        dao.updateUser(updateUser);
    }
}
