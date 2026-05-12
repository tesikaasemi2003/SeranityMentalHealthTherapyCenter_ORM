package lk.ijse.seranity_mental_health_therapy_center.dao.custom.impl;

import lk.ijse.seranity_mental_health_therapy_center.dao.custom.TherapySessionDAO;
import lk.ijse.seranity_mental_health_therapy_center.db.FactoryConfiguration;
import lk.ijse.seranity_mental_health_therapy_center.entity.TherapySession;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

public class TherapySessionDAOImpl implements TherapySessionDAO {

    @Override
    public boolean save(TherapySession therapySession) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(therapySession);
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
    public boolean update(TherapySession therapySession) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(therapySession);
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
            TherapySession ts = session.get(TherapySession.class, id);
            if (ts != null) {
                session.remove(ts);
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
    public TherapySession search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(TherapySession.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public List<TherapySession> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery("FROM TherapySession", TherapySession.class).getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public String getNextId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String lastId = (String) session.createQuery(
                            "SELECT ts.id FROM TherapySession ts ORDER BY ts.id DESC")
                    .setMaxResults(1)
                    .uniqueResult();

            if (lastId == null) return "S001";

            int num = Integer.parseInt(lastId.substring(1));
            return String.format("S%03d", num + 1);
        } finally {
            session.close();
        }
    }

    @Override
    public List<TherapySession> getSessionsByPatientId(String patientId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery(
                            "FROM TherapySession ts WHERE ts.patient.id = :patientId", TherapySession.class)
                    .setParameter("patientId", patientId)
                    .getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public boolean hasConflict(String therapistId, LocalDate date, String time) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Long count = session.createQuery(
                            "SELECT COUNT(ts) FROM TherapySession ts WHERE ts.therapist.id = :therapistId " +
                                    "AND ts.sessionDate = :date AND ts.sessionTime = :time " +
                                    "AND ts.status = 'SCHEDULED'", Long.class)
                    .setParameter("therapistId", therapistId)
                    .setParameter("date", date)
                    .setParameter("time", time)
                    .uniqueResult();
            return count > 0;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean updateStatus(String sessionId, String status) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            TherapySession ts = session.get(TherapySession.class, sessionId);
            if (ts != null) {
                ts.setStatus(status);
                session.merge(ts);
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
    public String generateNextId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String lastId = session.createQuery(
                            "SELECT ts.id FROM TherapySession ts ORDER BY ts.id DESC", String.class)
                    .setMaxResults(1).uniqueResult();
            if (lastId == null) return "S001";
            int num = Integer.parseInt(lastId.substring(1)) + 1;
            return String.format("S%03d", num);
        } finally {
            session.close();
        }
    }
}