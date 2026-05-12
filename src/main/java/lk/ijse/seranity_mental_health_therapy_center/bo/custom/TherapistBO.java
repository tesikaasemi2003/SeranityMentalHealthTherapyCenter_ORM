package lk.ijse.seranity_mental_health_therapy_center.bo.custom;

import lk.ijse.seranity_mental_health_therapy_center.bo.SuperBO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Therapist;

import java.util.List;

public interface TherapistBO extends SuperBO {
    boolean saveTherapist(Therapist therapist) throws Exception;
    boolean updateTherapist(Therapist therapist) throws Exception;
    boolean deleteTherapist(String therapistId) throws Exception;
    Therapist searchTherapist(String therapistId) throws Exception;
    List<Therapist> getAllTherapists() throws Exception;
    String generateNextTherapistId() throws Exception;
}