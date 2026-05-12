package lk.ijse.seranity_mental_health_therapy_center.dao.custom.impl;

import lk.ijse.seranity_mental_health_therapy_center.dao.custom.RegistrationDAO;
import lk.ijse.seranity_mental_health_therapy_center.db.FactoryConfiguration;
import lk.ijse.seranity_mental_health_therapy_center.entity.Registration;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class RegistrationDAOImpl implements RegistrationDAO {

    @Override
    public boolean save(Registration registration) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(registration);
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
    public boolean update(Registration registration) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(registration);
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
            Registration registration = session.get(Registration.class, id);
            if (registration != null) {
                session.remove(registration);
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
    public Registration search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(Registration.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Registration> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery("FROM Registration", Registration.class).getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public String getNextId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String lastId = (String) session.createQuery(
                            "SELECT r.id FROM Registration r ORDER BY r.id DESC")
                    .setMaxResults(1)
                    .uniqueResult();

            if (lastId == null) return "R001";

            int num = Integer.parseInt(lastId.substring(1));
            return String.format("R%03d", num + 1);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Registration> getRegistrationsByPatientId(String patientId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery(
                            "FROM Registration r WHERE r.patient.id = :patientId", Registration.class)
                    .setParameter("patientId", patientId)
                    .getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public boolean isAlreadyRegistered(String patientId, String programId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Long count = session.createQuery(
                            "SELECT COUNT(r) FROM Registration r WHERE r.patient.id = :patientId " +
                                    "AND r.therapyProgram.id = :programId", Long.class)
                    .setParameter("patientId", patientId)
                    .setParameter("programId", programId)
                    .uniqueResult();
            return count > 0;
        } finally {
            session.close();
        }
    }
    @Override
    public String generateNextId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String lastId = session.createQuery(
                            "SELECT r.id FROM Registration r ORDER BY r.id DESC", String.class)
                    .setMaxResults(1).uniqueResult();
            if (lastId == null) return "REG001";
            int num = Integer.parseInt(lastId.substring(3)) + 1;
            return String.format("REG%03d", num);
        } finally {
            session.close();
        }
    }
}