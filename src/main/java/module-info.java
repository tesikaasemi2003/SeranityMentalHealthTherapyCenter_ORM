module lk.ijse.seranity_mental_health_therapy_center {
    requires javafx.controls;
    requires javafx.fxml;


    opens lk.ijse.seranity_mental_health_therapy_center to javafx.fxml;
    exports lk.ijse.seranity_mental_health_therapy_center;
}