package lk.ijse.seranity_mental_health_therapy_center.dao.custom.impl;

import lk.ijse.seranity_mental_health_therapy_center.dao.custom.UserDAO;
import lk.ijse.seranity_mental_health_therapy_center.db.FactoryConfiguration;
import lk.ijse.seranity_mental_health_therapy_center.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public boolean save(User user) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(user);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean update(User user) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(user);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean delete(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public User search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(User.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public List<User> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery("FROM User", User.class).getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public String getNextId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String lastId = (String) session.createQuery(
                            "SELECT u.id FROM User u ORDER BY u.id DESC")
                    .setMaxResults(1)
                    .uniqueResult();

            if (lastId == null) return "U001";

            int num = Integer.parseInt(lastId.substring(1));
            return String.format("U%03d", num + 1);
        } finally {
            session.close();
        }
    }

    @Override
    public User searchByUsername(String username) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery(
                            "FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
        } finally {
            session.close();
        }
    }

    @Override
    public boolean isUsernameExists(String username) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Long count = session.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                    .setParameter("username", username)
                    .uniqueResult();
            return count > 0;
        } finally {
            session.close();
        }
    }

    @Override
    public User getUserByUsername(String username) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            User user = session.createQuery(
                            "FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
            transaction.commit();
            return user;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
    @Override
    public String generateNextId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String lastId = session.createQuery(
                            "SELECT u.id FROM User u ORDER BY u.id DESC", String.class)
                    .setMaxResults(1).uniqueResult();
            if (lastId == null) return "U001";
            int num = Integer.parseInt(lastId.substring(1)) + 1;
            return String.format("U%03d", num);
        } finally {
            session.close();
        }
    }
}