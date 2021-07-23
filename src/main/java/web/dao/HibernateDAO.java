package web.dao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import web.model.User;
import web.myExcetion.SaveObjectException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
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
        User user = null;
        try {
            user = entityManager.createQuery("SELECT user from User user where user.username = :username", User.class)
                    .setParameter("username", updateUser.getUsername())
                    .getSingleResult();
        }catch (NoResultException e){
            e.getStackTrace();
            entityManager.merge(updateUser);
        }
        if (user != null) {
            throw new SaveObjectException("Exception: User с таким именем уже существует");
        }
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
}
