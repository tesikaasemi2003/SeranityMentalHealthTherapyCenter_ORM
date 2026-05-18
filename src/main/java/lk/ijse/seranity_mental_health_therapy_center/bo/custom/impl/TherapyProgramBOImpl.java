package lk.ijse.seranity_mental_health_therapy_center.bo.custom.impl;

import lk.ijse.seranity_mental_health_therapy_center.bo.custom.TherapyProgramBO;
import lk.ijse.seranity_mental_health_therapy_center.dao.DAOFactory;
import lk.ijse.seranity_mental_health_therapy_center.dao.DAOTypes;
import lk.ijse.seranity_mental_health_therapy_center.dao.custom.TherapyProgramDAO;
import lk.ijse.seranity_mental_health_therapy_center.entity.TherapyProgram;

import java.util.List;

public class TherapyProgramBOImpl implements TherapyProgramBO {

    private final TherapyProgramDAO therapyProgramDAO =
            (TherapyProgramDAO) DAOFactory.getInstance().getDAO(DAOTypes.THERAPY_PROGRAM);

    @Override
    public boolean saveTherapyProgram(TherapyProgram therapyProgram) throws Exception {
        return therapyProgramDAO.save(therapyProgram);
    }

    @Override
    public boolean updateTherapyProgram(TherapyProgram therapyProgram) throws Exception {
        return therapyProgramDAO.update(therapyProgram);
    }

    @Override
    public boolean deleteTherapyProgram(String programId) throws Exception {
        return therapyProgramDAO.delete(programId);
    }

    @Override
    public TherapyProgram searchTherapyProgram(String programId) throws Exception {
        return therapyProgramDAO.search(programId);
    }

    @Override
    public List<TherapyProgram> getAllTherapyPrograms() throws Exception {
        return therapyProgramDAO.getAll();
    }

    @Override
    public String generateNextProgramId() throws Exception {
        return therapyProgramDAO.generateNextId();
    }
}