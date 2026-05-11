package lk.ijse.seranity_mental_health_therapy_center.db;

import lk.ijse.seranity_mental_health_therapy_center.entity.*;
import lk.ijse.hibernate.Session;
import lk.ijse.hibernate.SessionFactory;
import lk.ijse.hibernate.cfg.Configuration;

public class FactoryConfiguration {

    private static FactoryConfiguration factoryConfiguration;
    private final SessionFactory sessionFactory;

    private FactoryConfiguration() {
        // hibernate.properties file automatically read කරනවා
        // (classpath ඒකේ hibernate.properties තිබ්බොත් auto-detect)
        Configuration configuration = new Configuration();

        // Entity classes manually add කරනවා
        // (hibernate.cfg.xml නැති නිසා මෙහෙම කරන්න ඕනේ)
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Patient.class);
        configuration.addAnnotatedClass(Therapist.class);
        configuration.addAnnotatedClass(TherapyProgram.class);
        configuration.addAnnotatedClass(Registration.class);
        configuration.addAnnotatedClass(TherapySession.class);
        configuration.addAnnotatedClass(Payment.class);

        // hibernate.properties file ඒකෙන් properties load කරනවා
        configuration.configure("/hibernate.properties");

        // SessionFactory build කරනවා
        this.sessionFactory = configuration.buildSessionFactory();
    }

    // Singleton pattern — instance එකක් විතරයි
    public static FactoryConfiguration getInstance() {
        if (factoryConfiguration == null) {
            factoryConfiguration = new FactoryConfiguration();
        }
        return factoryConfiguration;
    }

    // DAO classes වලට session ගන්න
    public Session getSession() {
        return sessionFactory.openSession();
    }
}