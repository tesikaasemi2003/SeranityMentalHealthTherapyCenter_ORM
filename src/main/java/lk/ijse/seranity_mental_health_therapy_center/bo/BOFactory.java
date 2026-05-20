package lk.ijse.seranity_mental_health_therapy_center.bo;

import lk.ijse.seranity_mental_health_therapy_center.bo.custom.impl.*;

public class BOFactory {

    private static BOFactory boFactory;

    private BOFactory() {}

    public static BOFactory getInstance() {
        if (boFactory == null) {
            boFactory = new BOFactory();
        }
        return boFactory;
    }

    public SuperBO getBO(BOTypes boType) {
        switch (boType) {
            case PATIENT:         return new PatientBOImpl();
            case THERAPIST:       return new TherapistBOImpl();
            case THERAPY_PROGRAM: return new TherapyProgramBOImpl();
            case REGISTRATION:    return new RegistrationBOImpl();
            case THERAPY_SESSION: return new TherapySessionBOImpl();
            case PAYMENT:         return new PaymentBOImpl();
            case USER:            return new UserBOImpl();
            case QUERY:           return new QueryBOImpl();   // Part A — HQL
            default:              return null;
        }
    }
}
