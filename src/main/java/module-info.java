module lk.ijse.seranity_mental_health_therapy_center {
    requires javafx.controls;
    requires javafx.fxml;

    requires static lombok;

    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.sql;
    requires java.naming;

    requires jbcrypt;
    requires jakarta.mail;
    requires jasperreports;
    requires java.desktop;
    requires java.xml;


    opens lk.ijse.seranity_mental_health_therapy_center to javafx.fxml;
    opens lk.ijse.seranity_mental_health_therapy_center.dto.tm to javafx.base, javafx.fxml;
    opens lk.ijse.seranity_mental_health_therapy_center.entity to org.hibernate.orm.core, javafx.base;
    opens lk.ijse.seranity_mental_health_therapy_center.controller to javafx.fxml;
    opens lk.ijse.seranity_mental_health_therapy_center.db to org.hibernate.orm.core;
    opens lk.ijse.seranity_mental_health_therapy_center.dao.custom.impl to org.hibernate.orm.core;
    opens lk.ijse.seranity_mental_health_therapy_center.util to javafx.fxml, jasperreports;
    opens lk.ijse.seranity_mental_health_therapy_center.report to jasperreports;


    exports lk.ijse.seranity_mental_health_therapy_center;
}