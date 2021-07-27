package web.dao;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import web.model.Role;
import web.model.User;
import web.myExcetion.SaveObjectException;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public List<User> getAllUsers() {
        return entityManager.createQuery("SELECT user FROM User user", User.class).getResultList();
    }

    public User getUserById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public User findByUsername(String username) {
        try {
            return entityManager.createQuery("SELECT user FROM User user WHERE user.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public Set<Role> getSetRoles(String... roles) {
        Set<Role> roleSet = new HashSet<>();
        for (String role : roles) {
            roleSet.add(entityManager.createQuery("SELECT role FROM Role role WHERE role.authority=:role"
                    , Role.class).setParameter("role", role).getSingleResult());
        }
        return roleSet;
    }
}
