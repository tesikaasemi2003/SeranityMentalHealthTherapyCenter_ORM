package lk.ijse.seranity_mental_health_therapy_center.bo.custom;

import lk.ijse.seranity_mental_health_therapy_center.bo.SuperBO;
import lk.ijse.seranity_mental_health_therapy_center.entity.TherapySession;

import java.util.List;

public interface TherapySessionBO extends SuperBO {
    boolean saveTherapySession(TherapySession therapySession) throws Exception;
    boolean updateTherapySession(TherapySession therapySession) throws Exception;
    boolean deleteTherapySession(String sessionId) throws Exception;
    TherapySession searchTherapySession(String sessionId) throws Exception;
    List<TherapySession> getAllTherapySessions() throws Exception;
    String generateNextSessionId() throws Exception;
}