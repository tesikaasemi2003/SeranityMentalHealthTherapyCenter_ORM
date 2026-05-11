package lk.ijse.seranity_mental_health_therapy_center.dao.custom;

import lk.ijse.seranity_mental_health_therapy_center.dao.CrudDAO;
import lk.ijse.seranity_mental_health_therapy_center.entity.TherapySession;

import java.time.LocalDate;
import java.util.List;

public interface TherapySessionDAO extends CrudDAO<TherapySession> {

    // Patient ID ගෙන් sessions ගන්න
    List<TherapySession> getSessionsByPatientId(String patientId) throws Exception;

    // Therapist ට same date & time session conflict check
    boolean hasConflict(String therapistId, LocalDate date, String time) throws Exception;

    // Status update කරන්න (SCHEDULED → COMPLETED / CANCELLED)
    boolean updateStatus(String sessionId, String status) throws Exception;
}