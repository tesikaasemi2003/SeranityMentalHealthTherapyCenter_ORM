package lk.ijse.seranity_mental_health_therapy_center.dao.custom;

import lk.ijse.seranity_mental_health_therapy_center.dao.CrudDAO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Therapist;

import java.util.List;

public interface TherapistDAO extends CrudDAO<Therapist> {

    // Specialization ගෙන් therapists search කරන්න
    List<Therapist> searchBySpecialization(String specialization) throws Exception;
}