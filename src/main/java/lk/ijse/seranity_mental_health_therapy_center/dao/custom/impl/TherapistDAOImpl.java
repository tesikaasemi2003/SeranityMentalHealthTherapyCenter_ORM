package lk.ijse.seranity_mental_health_therapy_center.dao.custom.impl;

import lk.ijse.seranity_mental_health_therapy_center.dao.custom.TherapistDAO;
import lk.ijse.seranity_mental_health_therapy_center.db.FactoryConfiguration;
import lk.ijse.seranity_mental_health_therapy_center.entity.Therapist;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class TherapistDAOImpl implements TherapistDAO {

    @Override
    public boolean save(Therapist therapist) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(therapist);
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
    public boolean update(Therapist therapist) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(therapist);
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
            Therapist therapist = session.get(Therapist.class, id);
            if (therapist != null) {
                session.remove(therapist);
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
    public Therapist search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(Therapist.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Therapist> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery("FROM Therapist", Therapist.class).getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public String getNextId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String lastId = (String) session.createQuery(
                            "SELECT t.id FROM Therapist t ORDER BY t.id DESC")
                    .setMaxResults(1)
                    .uniqueResult();

            if (lastId == null) return "T001";

            int num = Integer.parseInt(lastId.substring(1));
            return String.format("T%03d", num + 1);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Therapist> searchBySpecialization(String specialization) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery(
                            "FROM Therapist t WHERE t.specialization = :spec", Therapist.class)
                    .setParameter("spec", specialization)
                    .getResultList();
        } finally {
            session.close();
        }
    }
    @Override
    public String generateNextId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String lastId = session.createQuery(
                            "SELECT t.id FROM Therapist t ORDER BY t.id DESC", String.class)
                    .setMaxResults(1).uniqueResult();
            if (lastId == null) return "T001";
            int num = Integer.parseInt(lastId.substring(1)) + 1;
            return String.format("T%03d", num);
        } finally {
            session.close();
        }
    }
}