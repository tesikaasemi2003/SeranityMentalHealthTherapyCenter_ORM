package lk.ijse.seranity_mental_health_therapy_center.bo.custom;

import lk.ijse.seranity_mental_health_therapy_center.bo.SuperBO;
import lk.ijse.seranity_mental_health_therapy_center.entity.TherapyProgram;

import java.util.List;

public interface TherapyProgramBO extends SuperBO {
    boolean saveTherapyProgram(TherapyProgram therapyProgram) throws Exception;
    boolean updateTherapyProgram(TherapyProgram therapyProgram) throws Exception;
    boolean deleteTherapyProgram(String programId) throws Exception;
    TherapyProgram searchTherapyProgram(String programId) throws Exception;
    List<TherapyProgram> getAllTherapyPrograms() throws Exception;
    String generateNextProgramId() throws Exception;
}