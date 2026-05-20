package lk.ijse.seranity_mental_health_therapy_center.bo.custom.impl;

import lk.ijse.seranity_mental_health_therapy_center.bo.custom.QueryBO;
import lk.ijse.seranity_mental_health_therapy_center.dao.DAOFactory;
import lk.ijse.seranity_mental_health_therapy_center.dao.DAOTypes;
import lk.ijse.seranity_mental_health_therapy_center.dao.custom.QueryDAO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Patient;
import lk.ijse.seranity_mental_health_therapy_center.entity.TherapyProgram;

import java.util.*;

/**
 * ================================================================
 *  QueryBOImpl.java
 *  Seranity Mental Health Therapy Center
 * ----------------------------------------------------------------
 *  Business logic layer for HQL queries.
 *  Part A Q2 & Q4 of the coursework.
 * ================================================================
 */
public class QueryBOImpl implements QueryBO {

    private final QueryDAO queryDAO =
            (QueryDAO) DAOFactory.getInstance().getDAO(DAOTypes.QUERY);

    /**
     * Part A — Q2
     * ─────────────────────────────────────────────────────────────
     * Delegates to QueryDAO to fetch patients enrolled in ALL programs.
     * Throws exception if no patients found (not an error, just empty).
     * ─────────────────────────────────────────────────────────────
     */
    @Override
    public List<Patient> getPatientsEnrolledInAllPrograms() throws Exception {
        List<Patient> result = queryDAO.getPatientsEnrolledInAllPrograms();
        return result != null ? result : Collections.emptyList();
    }

    /**
     * Part A — Q4
     * ─────────────────────────────────────────────────────────────
     * Converts Object[] pairs from DAO into a readable Map:
     *   Patient → List<TherapyProgram>
     *
     * DAO returns: [ [Patient, TherapyProgram], [Patient, TherapyProgram], ... ]
     * Multiple rows for the same patient (one per program) are grouped here.
     * ─────────────────────────────────────────────────────────────
     */
    @Override
    public Map<Patient, List<TherapyProgram>> getPatientsWithTheirPrograms() throws Exception {
        List<Object[]> rawList = queryDAO.getPatientsWithTheirPrograms();

        // Use LinkedHashMap to preserve patient order (sorted by name from HQL)
        Map<Patient, List<TherapyProgram>> resultMap = new LinkedHashMap<>();

        for (Object[] row : rawList) {
            Patient       patient = (Patient)       row[0];
            TherapyProgram program = (TherapyProgram) row[1];

            // Group by patient — check if patient already exists in map
            boolean found = false;
            for (Patient key : resultMap.keySet()) {
                if (key.getId().equals(patient.getId())) {
                    resultMap.get(key).add(program);
                    found = true;
                    break;
                }
            }
            if (!found) {
                List<TherapyProgram> programs = new ArrayList<>();
                programs.add(program);
                resultMap.put(patient, programs);
            }
        }

        return resultMap;
    }

    /**
     * Bonus — All programs a specific patient is enrolled in.
     */
    @Override
    public List<TherapyProgram> getProgramsByPatient(String patientId) throws Exception {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty.");
        }
        List<TherapyProgram> result = queryDAO.getProgramsByPatient(patientId.trim());
        return result != null ? result : Collections.emptyList();
    }
}
