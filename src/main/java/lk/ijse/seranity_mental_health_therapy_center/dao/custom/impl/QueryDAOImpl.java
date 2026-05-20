package lk.ijse.seranity_mental_health_therapy_center.dao.custom.impl;

import lk.ijse.seranity_mental_health_therapy_center.dao.custom.QueryDAO;
import lk.ijse.seranity_mental_health_therapy_center.db.FactoryConfiguration;
import lk.ijse.seranity_mental_health_therapy_center.entity.Patient;
import lk.ijse.seranity_mental_health_therapy_center.entity.TherapyProgram;
import org.hibernate.Session;

import java.util.List;

/**
 * ================================================================
 *  QueryDAOImpl.java
 *  Seranity Mental Health Therapy Center
 * ----------------------------------------------------------------
 *  Special HQL queries — Part A Q2 & Q4
 * ================================================================
 */
public class QueryDAOImpl implements QueryDAO {

    /**
     * Part A — Q2
     * ─────────────────────────────────────────────────────────────
     * HQL Join Query: Patients enrolled in ALL therapy programs.
     *
     * Logic:
     *   Count distinct therapy programs a patient is registered for
     *   and compare it with the total number of therapy programs.
     *   If equal → patient is enrolled in every program.
     *
     * HQL used (Subquery approach — required by coursework):
     *   SELECT p FROM Patient p
     *   WHERE
     *     (SELECT COUNT(DISTINCT r.therapyProgram.id)
     *      FROM Registration r WHERE r.patient = p)
     *     =
     *     (SELECT COUNT(tp) FROM TherapyProgram tp)
     * ─────────────────────────────────────────────────────────────
     */
    @Override
    public List<Patient> getPatientsEnrolledInAllPrograms() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql =
                    "SELECT p FROM Patient p " +
                            "WHERE " +
                            "  (SELECT COUNT(DISTINCT r.therapyProgram.id) " +
                            "   FROM Registration r WHERE r.patient = p) " +
                            "= " +
                            "  (SELECT COUNT(tp) FROM TherapyProgram tp)";

            return session.createQuery(hql, Patient.class).getResultList();

        } finally {
            session.close();
        }
    }

    /**
     * Part A — Q4
     * ─────────────────────────────────────────────────────────────
     * HQL Join Fetch: Each patient with their enrolled programs.
     *
     * Logic:
     *   JOIN Patient → Registration → TherapyProgram
     *   Returns Object[] pairs: [0] = Patient, [1] = TherapyProgram
     *
     * HQL used (Explicit JOIN — required by coursework):
     *   SELECT p, tp FROM Patient p
     *   JOIN p.registrations r
     *   JOIN r.therapyProgram tp
     *   ORDER BY p.name ASC
     *
     * Note: Uses JOIN (not LEFT JOIN) → only patients with
     *       at least one registration are returned.
     * ─────────────────────────────────────────────────────────────
     */
    @Override
    public List<Object[]> getPatientsWithTheirPrograms() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql =
                    "SELECT p, tp FROM Patient p " +
                            "JOIN p.registrations r " +
                            "JOIN r.therapyProgram tp " +
                            "ORDER BY p.name ASC";

            return session.createQuery(hql, Object[].class).getResultList();

        } finally {
            session.close();
        }
    }

    /**
     * Bonus — All programs a specific patient is enrolled in.
     * ─────────────────────────────────────────────────────────────
     * HQL:
     *   SELECT tp FROM TherapyProgram tp
     *   JOIN tp.registrations r
     *   WHERE r.patient.id = :patientId
     * ─────────────────────────────────────────────────────────────
     */
    @Override
    public List<TherapyProgram> getProgramsByPatient(String patientId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql =
                    "SELECT tp FROM TherapyProgram tp " +
                            "JOIN tp.registrations r " +
                            "WHERE r.patient.id = :patientId " +
                            "ORDER BY tp.name ASC";

            return session.createQuery(hql, TherapyProgram.class)
                    .setParameter("patientId", patientId)
                    .getResultList();

        } finally {
            session.close();
        }
    }
}
