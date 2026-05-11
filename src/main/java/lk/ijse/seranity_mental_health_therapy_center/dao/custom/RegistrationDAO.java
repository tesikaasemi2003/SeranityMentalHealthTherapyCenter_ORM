package lk.ijse.seranity_mental_health_therapy_center.dao.custom;

import lk.ijse.seranity_mental_health_therapy_center.dao.CrudDAO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Registration;

import java.util.List;

public interface RegistrationDAO extends CrudDAO<Registration> {

    // Patient ID ගෙන් registrations ගන්න
    List<Registration> getRegistrationsByPatientId(String patientId) throws Exception;

    // Patient already registered ද check කරන්න
    boolean isAlreadyRegistered(String patientId, String programId) throws Exception;
}