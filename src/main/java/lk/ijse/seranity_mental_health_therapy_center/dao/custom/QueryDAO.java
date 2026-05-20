package lk.ijse.seranity_mental_health_therapy_center.dao.custom;

import lk.ijse.seranity_mental_health_therapy_center.dao.SuperDAO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Patient;
import lk.ijse.seranity_mental_health_therapy_center.entity.TherapyProgram;

import java.util.List;
import java.util.Map;

/**
 * QueryDAO — Special HQL query interface
 * Part A Q2 & Q4 of the coursework
 */
public interface QueryDAO extends SuperDAO {

    /**
     * Part A — Q2
     * HQL Join Query: Retrieve patients who have registered
     * for EVERY available therapy program.
     */
    List<Patient> getPatientsEnrolledInAllPrograms() throws Exception;

    /**
     * Part A — Q4
     * HQL Join Fetch: Retrieve each patient along with
     * their enrolled therapy programs.
     * Returns Object[] where [0]=Patient, [1]=TherapyProgram
     */
    List<Object[]> getPatientsWithTheirPrograms() throws Exception;

    /**
     * Bonus — fetch all therapy programs a single patient is enrolled in.
     */
    List<TherapyProgram> getProgramsByPatient(String patientId) throws Exception;
}
