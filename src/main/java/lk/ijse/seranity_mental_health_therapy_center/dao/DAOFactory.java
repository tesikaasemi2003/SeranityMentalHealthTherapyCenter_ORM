package lk.ijse.seranity_mental_health_therapy_center.dao;

import lk.ijse.seranity_mental_health_therapy_center.dao.custom.impl.*;

public class DAOFactory {

    private static DAOFactory daoFactory;

    private DAOFactory() {}

    public static DAOFactory getInstance() {
        if (daoFactory == null) {
            daoFactory = new DAOFactory();
        }
        return daoFactory;
    }

    public <T> T getDAO(DAOTypes types) {
        switch (types) {
            case PATIENT:
                return (T) new PatientDAOImpl();
            case THERAPIST:
                return (T) new TherapistDAOImpl();
            case THERAPY_PROGRAM:
                return (T) new TherapyProgramDAOImpl();
            case REGISTRATION:
                return (T) new RegistrationDAOImpl();
            case THERAPY_SESSION:
                return (T) new TherapySessionDAOImpl();
            case PAYMENT:
                return (T) new PaymentDAOImpl();
            case USER:
                return (T) new UserDAOImpl();
            case QUERY:
                return (T) new QueryDAOImpl();
            default:
                return null;
        }
    }
}