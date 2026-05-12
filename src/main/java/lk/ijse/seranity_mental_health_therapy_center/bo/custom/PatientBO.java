package lk.ijse.seranity_mental_health_therapy_center.bo.custom;

import lk.ijse.seranity_mental_health_therapy_center.bo.SuperBO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Patient;

import java.util.List;

public interface PatientBO extends SuperBO {
    boolean savePatient(Patient patient) throws Exception;
    boolean updatePatient(Patient patient) throws Exception;
    boolean deletePatient(String patientId) throws Exception;
    Patient searchPatient(String patientId) throws Exception;
    List<Patient> getAllPatients() throws Exception;
    String generateNextPatientId() throws Exception;
}