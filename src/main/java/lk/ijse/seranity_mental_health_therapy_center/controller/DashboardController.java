package lk.ijse.seranity_mental_health_therapy_center.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lk.ijse.seranity_mental_health_therapy_center.bo.BOFactory;
import lk.ijse.seranity_mental_health_therapy_center.bo.BOTypes;
import lk.ijse.seranity_mental_health_therapy_center.bo.custom.*;
import lk.ijse.seranity_mental_health_therapy_center.entity.User;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;
    @FXML private Label lblPageTitle;
    @FXML private Label lblDateTime;
    @FXML private Label lblTotalPatients;
    @FXML private Label lblTotalTherapists;
    @FXML private Label lblTotalSessions;
    @FXML private Label lblTotalRevenue;

    @FXML private StackPane contentArea;
    @FXML private VBox dashboardHome;

    // Sidebar buttons
    @FXML private Button btnDashboard;
    @FXML private Button btnPatients;
    @FXML private Button btnTherapists;
    @FXML private Button btnPrograms;
    @FXML private Button btnRegistrations;
    @FXML private Button btnSessions;
    @FXML private Button btnPayments;
    @FXML private Button btnUsers;

    private Button activeButton;
    private User loggedUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        activeButton = btnDashboard;

        // Set date time
        lblDateTime.setText(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm")));

        // Hover effects for all nav buttons
        Button[] navButtons = {btnPatients, btnTherapists, btnPrograms,
                btnRegistrations, btnSessions, btnPayments, btnUsers};
        for (Button btn : navButtons) {
            btn.setOnMouseEntered(e -> {
                if (btn != activeButton)
                    btn.setStyle(btn.getStyle()
                            .replace("-fx-text-fill: #8899CC", "-fx-text-fill: white")
                            .replace("transparent", "#3344AA"));
            });
            btn.setOnMouseExited(e -> {
                if (btn != activeButton)
                    btn.setStyle(btn.getStyle()
                            .replace("-fx-text-fill: white", "-fx-text-fill: #8899CC")
                            .replace("#3344AA", "transparent"));
            });
        }
    }

    public void setLoggedUser(User user) {
        this.loggedUser = user;
        lblUserName.setText(user.getUsername());
        lblUserRole.setText(user.getRole());

        // Hide Users button for RECEPTIONIST
        if (user.getRole().equalsIgnoreCase("RECEPTIONIST")) {
            btnUsers.setVisible(false);
            btnUsers.setManaged(false);
        }

        loadDashboardStats();
    }

    private void loadDashboardStats() {
        try {
            PatientBO patientBO = (PatientBO) BOFactory.getInstance().getBO(BOTypes.PATIENT);
            TherapistBO therapistBO = (TherapistBO) BOFactory.getInstance().getBO(BOTypes.THERAPIST);
            TherapySessionBO sessionBO = (TherapySessionBO) BOFactory.getInstance().getBO(BOTypes.THERAPY_SESSION);
            PaymentBO paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOTypes.PAYMENT);

            lblTotalPatients.setText(String.valueOf(patientBO.getAllPatients().size()));
            lblTotalTherapists.setText(String.valueOf(therapistBO.getAllTherapists().size()));
            lblTotalSessions.setText(String.valueOf(sessionBO.getAllTherapySessions().size()));

            double totalRevenue = paymentBO.getAllPayments()
                    .stream()
                    .mapToDouble(p -> p.getAmount())
                    .sum();
            lblTotalRevenue.setText(String.format("Rs. %.2f", totalRevenue));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleNavigation(ActionEvent event) {
        Button clicked = (Button) event.getSource();
        setActiveButton(clicked);

        if (clicked == btnDashboard || clicked == btnQuickPatient
                || clicked == btnQuickSession || clicked == btnQuickReg) {
            lblPageTitle.setText("Dashboard");
            loadPage(null);
            return;
        }

        String fxml = null;
        String title = "";

        if (clicked == btnPatients || clicked == btnQuickPatient) {
            fxml = "Patient.fxml"; title = "Patients";
        } else if (clicked == btnTherapists) {
            fxml = "Therapist.fxml"; title = "Therapists";
        } else if (clicked == btnPrograms) {
            fxml = "TherapyProgram.fxml"; title = "Therapy Programs";
        } else if (clicked == btnRegistrations || clicked == btnQuickReg) {
            fxml = "Registration.fxml"; title = "Registrations";
        } else if (clicked == btnSessions || clicked == btnQuickSession) {
            fxml = "TherapySession.fxml"; title = "Therapy Sessions";
        } else if (clicked == btnPayments) {
            fxml = "Payment.fxml"; title = "Payments";
        } else if (clicked == btnUsers) {
            fxml = "User.fxml"; title = "User Management";
        }

        lblPageTitle.setText(title);
        loadPage(fxml);
    }

    private void loadPage(String fxmlName) {
        contentArea.getChildren().clear();

        if (fxmlName == null) {
            contentArea.getChildren().add(dashboardHome);
            loadDashboardStats();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/lk/ijse/seranity_mental_health_therapy_center/view/" + fxmlName));
            contentArea.getChildren().add(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button btn) {
        // Reset previous active
        if (activeButton != null) {
            activeButton.setStyle(
                    activeButton.getStyle()
                            .replace("-fx-background-color: #3DC4A8", "-fx-background-color: transparent")
                            .replace("-fx-text-fill: white", "-fx-text-fill: #8899CC"));
        }
        // Set new active
        btn.setStyle(
                btn.getStyle()
                        .replace("-fx-background-color: transparent", "-fx-background-color: #3DC4A8")
                        .replace("-fx-text-fill: #8899CC", "-fx-text-fill: white"));
        activeButton = btn;
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/lk/ijse/seranity_mental_health_therapy_center/view/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Serenity — Login");
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Quick action buttons
    @FXML private Button btnQuickPatient;
    @FXML private Button btnQuickSession;
    @FXML private Button btnQuickReg;
    @FXML private Button btnLogout;
}