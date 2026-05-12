package lk.ijse.seranity_mental_health_therapy_center.bo.custom;

import lk.ijse.seranity_mental_health_therapy_center.bo.SuperBO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Registration;

import java.util.List;

public interface RegistrationBO extends SuperBO {
    boolean saveRegistration(Registration registration) throws Exception;
    boolean updateRegistration(Registration registration) throws Exception;
    boolean deleteRegistration(String registrationId) throws Exception;
    Registration searchRegistration(String registrationId) throws Exception;
    List<Registration> getAllRegistrations() throws Exception;
    String generateNextRegistrationId() throws Exception;
}