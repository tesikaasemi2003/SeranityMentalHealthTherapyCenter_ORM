package lk.ijse.seranity_mental_health_therapy_center.dao.custom.impl;

import lk.ijse.seranity_mental_health_therapy_center.dao.SuperDAO;
import lk.ijse.seranity_mental_health_therapy_center.db.FactoryConfiguration;
import lk.ijse.seranity_mental_health_therapy_center.entity.Patient;
import org.hibernate.Session;

import java.util.List;

// Part A — Special HQL Queries
public class QueryDAOImpl implements SuperDAO {

    // Part A Q2 — සියලු therapy programs වලට enrolled patients (Join Query)
    public List<Patient> getPatientsEnrolledInAllPrograms() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "SELECT p FROM Patient p WHERE " +
                    "(SELECT COUNT(DISTINCT r.therapyProgram.id) " +
                    "FROM Registration r WHERE r.patient = p) = " +
                    "(SELECT COUNT(tp) FROM TherapyProgram tp)";

            return session.createQuery(hql, Patient.class).getResultList();
        } finally {
            session.close();
        }
    }

    // Part A Q4 — Patients + their enrolled Therapy Programs (HQL Join Fetch)
    public List<Object[]> getPatientsWithTheirPrograms() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "SELECT p, tp FROM Patient p " +
                    "JOIN p.registrations r " +
                    "JOIN r.therapyProgram tp";

            return session.createQuery(hql, Object[].class).getResultList();
        } finally {
            session.close();
        }
    }

}