package web.dao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import web.model.User;
import web.myExcetion.SaveObjectException;
import javax.persistence.*;
import java.util.List;

@Repository
public class HibernateDAO implements DAO {

    @PersistenceContext()
    private EntityManager entityManager;

    @Override
    @Transactional(rollbackFor = {Exception.class}) //мы говорим Spring, эй, spring,
    // если ты видишь какое-либо исключение, Runtime exception или Checked exception,
    // пожалуйста, откатите транзакцию (не сохраняйте запись в БД)
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
        if (user != null) {
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
}
