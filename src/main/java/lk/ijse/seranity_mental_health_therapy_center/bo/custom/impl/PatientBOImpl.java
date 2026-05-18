package lk.ijse.seranity_mental_health_therapy_center.bo.custom.impl;

import lk.ijse.seranity_mental_health_therapy_center.bo.custom.PatientBO;
import lk.ijse.seranity_mental_health_therapy_center.dao.DAOFactory;
import lk.ijse.seranity_mental_health_therapy_center.dao.DAOTypes;
import lk.ijse.seranity_mental_health_therapy_center.dao.custom.PatientDAO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Patient;
import lk.ijse.seranity_mental_health_therapy_center.bo.exception.DuplicateEntryException;

import java.util.List;

public class PatientBOImpl implements PatientBO {

    private final PatientDAO patientDAO =
            (PatientDAO) DAOFactory.getInstance().getDAO(DAOTypes.PATIENT);

    @Override
    public boolean savePatient(Patient patient) throws Exception {
        List<Patient> all = patientDAO.getAll();
        for (Patient p : all) {
            if (p.getNic().equals(patient.getNic())) {
                throw new DuplicateEntryException(
                        "Patient with NIC " + patient.getNic() + " is already registered."
                );
            }
        }
        return patientDAO.save(patient);
    }

    @Override
    public boolean updatePatient(Patient patient) throws Exception {
        return patientDAO.update(patient);
    }

    @Override
    public boolean deletePatient(String patientId) throws Exception {
        return patientDAO.delete(patientId);
    }

    @Override
    public Patient searchPatient(String patientId) throws Exception {
        return patientDAO.search(patientId);
    }

    @Override
    public List<Patient> getAllPatients() throws Exception {
        return patientDAO.getAll();
    }

    @Override
    public String generateNextPatientId() throws Exception {
        return patientDAO.generateNextId();
    }
}