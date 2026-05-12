package lk.ijse.seranity_mental_health_therapy_center.bo.custom.impl;

import lk.ijse.seranity_mental_health_therapy_center.bo.custom.TherapistBO;
import lk.ijse.seranity_mental_health_therapy_center.dao.DAOFactory;
import lk.ijse.seranity_mental_health_therapy_center.dao.DAOTypes;
import lk.ijse.seranity_mental_health_therapy_center.dao.custom.TherapistDAO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Therapist;

import java.util.List;

public class TherapistBOImpl implements TherapistBO {

    private final TherapistDAO therapistDAO =
            (TherapistDAO) DAOFactory.getInstance().getDAO(DAOTypes.THERAPIST);

    @Override
    public boolean saveTherapist(Therapist therapist) throws Exception {
        return therapistDAO.save(therapist);
    }

    @Override
    public boolean updateTherapist(Therapist therapist) throws Exception {
        return therapistDAO.update(therapist);
    }

    @Override
    public boolean deleteTherapist(String therapistId) throws Exception {
        return therapistDAO.delete(therapistId);
    }

    @Override
    public Therapist searchTherapist(String therapistId) throws Exception {
        return therapistDAO.search(therapistId);
    }

    @Override
    public List<Therapist> getAllTherapists() throws Exception {
        return therapistDAO.getAll();
    }

    @Override
    public String generateNextTherapistId() throws Exception {
        return therapistDAO.generateNextId();
    }
}