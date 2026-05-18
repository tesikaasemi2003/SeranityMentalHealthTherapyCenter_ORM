package lk.ijse.seranity_mental_health_therapy_center.bo.custom.impl;

import lk.ijse.seranity_mental_health_therapy_center.bo.custom.TherapySessionBO;
import lk.ijse.seranity_mental_health_therapy_center.bo.exception.SchedulingConflictException;
import lk.ijse.seranity_mental_health_therapy_center.dao.DAOFactory;
import lk.ijse.seranity_mental_health_therapy_center.dao.DAOTypes;
import lk.ijse.seranity_mental_health_therapy_center.dao.custom.TherapySessionDAO;
import lk.ijse.seranity_mental_health_therapy_center.entity.TherapySession;

import java.time.LocalDate;
import java.util.List;

public class TherapySessionBOImpl implements TherapySessionBO {

    private final TherapySessionDAO therapySessionDAO =
            (TherapySessionDAO) DAOFactory.getInstance().getDAO(DAOTypes.THERAPY_SESSION);

    @Override
    public boolean saveTherapySession(TherapySession session) throws Exception {
        if (session.getSessionDate().isBefore(LocalDate.now())) {
            throw new SchedulingConflictException(
                    "Cannot schedule a session for a past date: " + session.getSessionDate()
            );
        }
        if (session.getEndTime().isBefore(session.getStartTime())) {
            throw new SchedulingConflictException(
                    "End time must be after start time."
            );
        }
        return therapySessionDAO.save(session);
    }

    @Override
    public boolean updateTherapySession(TherapySession therapySession) throws Exception {
        return therapySessionDAO.update(therapySession);
    }

    @Override
    public boolean deleteTherapySession(String sessionId) throws Exception {
        return therapySessionDAO.delete(sessionId);
    }

    @Override
    public TherapySession searchTherapySession(String sessionId) throws Exception {
        return therapySessionDAO.search(sessionId);
    }

    @Override
    public List<TherapySession> getAllTherapySessions() throws Exception {
        return therapySessionDAO.getAll();
    }

    @Override
    public String generateNextSessionId() throws Exception {
        return therapySessionDAO.generateNextId();
    }
}