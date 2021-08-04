package web.dao;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.expression.Sets;
import web.model.Role;
import web.model.User;
import web.myExcetion.SaveObjectException;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Scope(proxyMode = ScopedProxyMode.INTERFACES)
public class HibernateDAO implements DAO {

    @PersistenceContext()
    private EntityManager entityManager;

    @Override
    @Transactional(rollbackFor = {Exception.class}) //мы говорим Spring, эй, spring,
    // если ты видишь какое-либо исключение, Runtime exception или Checked exception,
    // пожалуйста, откатите транзакцию (не сохраняйте запись в БД) Аминь !
    public void saveUser(User user) throws SaveObjectException {
        try {
            entityManager.persist(user);
        } catch (PersistenceException e) {
            throw new SaveObjectException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updateUser(User updateUser) throws SaveObjectException {
        User user = findByUsername(updateUser.getUsername());

        if (user != null && user.getId() != updateUser.getId()) {
            throw new SaveObjectException("Exception: User с таким именем уже существует");
        }

        entityManager.merge(updateUser);
    }

    @Override
    @Transactional
    public void removeUserById(Long id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
        }

    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        List<User> users = entityManager.createQuery("SELECT user FROM User user", User.class).getResultList();
        System.out.println(users.get(1).getRoles()); // и тут поменять
        return users;
    }

    public User getUserById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    @Transactional
    public User findByUsername(String username) {
        User user = new User();
        try {
           user = entityManager.createQuery("SELECT user FROM User user WHERE user.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
           System.out.println(user.getAuthorities()); // тут поменять
        } catch (NoResultException e) {
            return null;
        }
        return user;
    }

    @Override
    @Transactional
    public Set<Role> getSetRoles(String... roles) { // Использование List  в параметрах HQL запроса
        Set<Role> roleSet;
        roleSet = entityManager
                .createQuery("SELECT role FROM Role role WHERE role.authority IN (:roles)"
                        , Role.class)
                .setParameter("roles", Arrays.asList(roles))
                .getResultStream().collect(Collectors.toSet());
        return roleSet;
    }
}
