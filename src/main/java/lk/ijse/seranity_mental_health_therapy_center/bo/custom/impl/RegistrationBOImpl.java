package lk.ijse.seranity_mental_health_therapy_center.bo.custom.impl;

import lk.ijse.seranity_mental_health_therapy_center.bo.custom.RegistrationBO;
import lk.ijse.seranity_mental_health_therapy_center.dao.DAOFactory;
import lk.ijse.seranity_mental_health_therapy_center.dao.DAOTypes;
import lk.ijse.seranity_mental_health_therapy_center.dao.custom.RegistrationDAO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Registration;

import java.util.List;

public class RegistrationBOImpl implements RegistrationBO {

    private final RegistrationDAO registrationDAO =
            (RegistrationDAO) DAOFactory.getInstance().getDAO(DAOTypes.REGISTRATION);

    @Override
    public boolean saveRegistration(Registration registration) throws Exception {
        return registrationDAO.save(registration);
    }

    @Override
    public boolean updateRegistration(Registration registration) throws Exception {
        return registrationDAO.update(registration);
    }

    @Override
    public boolean deleteRegistration(String registrationId) throws Exception {
        return registrationDAO.delete(registrationId);
    }

    @Override
    public Registration searchRegistration(String registrationId) throws Exception {
        return registrationDAO.search(registrationId);
    }

    @Override
    public List<Registration> getAllRegistrations() throws Exception {
        return registrationDAO.getAll();
    }

    @Override
    public String generateNextRegistrationId() throws Exception {
        return registrationDAO.generateNextId();
    }
}