package lk.ijse.seranity_mental_health_therapy_center.dao.custom;

import lk.ijse.seranity_mental_health_therapy_center.dao.CrudDAO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Patient;

import java.util.List;

public interface PatientDAO extends CrudDAO<Patient> {

    // NIC ගෙන් patient search කරන්න
    Patient searchByNic(String nic) throws Exception;

    // සියලු therapy programs වලට enrolled patients
    List<Patient> getPatientsEnrolledInAllPrograms() throws Exception;
}