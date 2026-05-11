package lk.ijse.seranity_mental_health_therapy_center.dao.custom;

import lk.ijse.seranity_mental_health_therapy_center.dao.CrudDAO;
import lk.ijse.seranity_mental_health_therapy_center.entity.TherapyProgram;

public interface TherapyProgramDAO extends CrudDAO<TherapyProgram> {

    // Programs total count (HQL query සඳහා ඕනේ)
    long countAllPrograms() throws Exception;
}