package lk.ijse.seranity_mental_health_therapy_center.bo.custom;

import lk.ijse.seranity_mental_health_therapy_center.bo.SuperBO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Patient;
import lk.ijse.seranity_mental_health_therapy_center.entity.TherapyProgram;

import java.util.List;
import java.util.Map;

/**
 * QueryBO — Business layer for special HQL queries
 * Part A Q2 & Q4
 */
public interface QueryBO extends SuperBO {

    /**
     * Part A — Q2
     * Returns patients enrolled in ALL available therapy programs.
     */
    List<Patient> getPatientsEnrolledInAllPrograms() throws Exception;

    /**
     * Part A — Q4
     * Returns each patient mapped to their enrolled therapy programs.
     * Map key = Patient, value = list of TherapyPrograms they enrolled in.
     */
    Map<Patient, List<TherapyProgram>> getPatientsWithTheirPrograms() throws Exception;

    /**
     * Bonus — Returns all programs a specific patient is enrolled in.
     */
    List<TherapyProgram> getProgramsByPatient(String patientId) throws Exception;
}
