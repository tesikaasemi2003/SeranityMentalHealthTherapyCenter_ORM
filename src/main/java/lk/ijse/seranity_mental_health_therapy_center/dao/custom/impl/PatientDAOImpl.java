package lk.ijse.seranity_mental_health_therapy_center.dao.custom.impl;

import lk.ijse.seranity_mental_health_therapy_center.dao.custom.PatientDAO;
import lk.ijse.seranity_mental_health_therapy_center.db.FactoryConfiguration;
import lk.ijse.seranity_mental_health_therapy_center.entity.Patient;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class PatientDAOImpl implements PatientDAO {

    @Override
    public boolean save(Patient patient) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(patient);
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
    public boolean update(Patient patient) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(patient);
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
            Patient patient = session.get(Patient.class, id);
            if (patient != null) {
                session.remove(patient);
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
    public Patient search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(Patient.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Patient> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery("FROM Patient", Patient.class).getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public String getNextId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String lastId = (String) session.createQuery(
                            "SELECT p.id FROM Patient p ORDER BY p.id DESC")
                    .setMaxResults(1)
                    .uniqueResult();

            if (lastId == null) return "P001";

            int num = Integer.parseInt(lastId.substring(1));
            return String.format("P%03d", num + 1);
        } finally {
            session.close();
        }
    }

    @Override
    public Patient searchByNic(String nic) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery("FROM Patient p WHERE p.nic = :nic", Patient.class)
                    .setParameter("nic", nic)
                    .uniqueResult();
        } finally {
            session.close();
        }
    }

    // Part A — HQL: සියලු therapy programs වලට enrolled patients
    @Override
    public List<Patient> getPatientsEnrolledInAllPrograms() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "SELECT p FROM Patient p WHERE " +
                    "(SELECT COUNT(DISTINCT r.therapyProgram.id) FROM Registration r WHERE r.patient = p) = " +
                    "(SELECT COUNT(tp) FROM TherapyProgram tp)";
            return session.createQuery(hql, Patient.class).getResultList();
        } finally {
            session.close();
        }
    }
    @Override
    public String generateNextId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String lastId = session.createQuery(
                            "SELECT p.id FROM Patient p ORDER BY p.id DESC", String.class)
                    .setMaxResults(1)
                    .uniqueResult();
            if (lastId == null) return "P001";
            int num = Integer.parseInt(lastId.substring(1)) + 1;
            return String.format("P%03d", num);
        } finally {
            session.close();
        }
    }
}