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

    @FXML private Button btnDashboard;
    @FXML private Button btnPatients;
    @FXML private Button btnTherapists;
    @FXML private Button btnPrograms;
    @FXML private Button btnRegistrations;
    @FXML private Button btnSessions;
    @FXML private Button btnPayments;
    @FXML private Button btnUsers;
    @FXML private Button btnQuickPatient;
    @FXML private Button btnQuickSession;
    @FXML private Button btnQuickReg;
    @FXML private Button btnLogout;

    private Button activeButton;
    private User loggedUser;

    private static final String VIEW_PATH =
            "/lk/ijse/seranity_mental_health_therapy_center/view/";

    private static final String STYLE_INACTIVE =
            "-fx-background-color: transparent;" +
                    "-fx-text-fill: #8899CC;" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 12 16;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;" +
                    "-fx-border-width: 0;" +
                    "-fx-alignment: CENTER_LEFT;";

    private static final String STYLE_ACTIVE =
            "-fx-background-color: #3DC4A8;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 12 16;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;" +
                    "-fx-border-width: 0;" +
                    "-fx-alignment: CENTER_LEFT;";

    private static final String STYLE_HOVER =
            "-fx-background-color: #2a4a8a;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 12 16;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;" +
                    "-fx-border-width: 0;" +
                    "-fx-alignment: CENTER_LEFT;";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        activeButton = btnDashboard;
        lblDateTime.setText(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm")));
        setupHoverEffects();
    }

    private void setupHoverEffects() {
        Button[] navButtons = {btnPatients, btnTherapists, btnPrograms,
                btnRegistrations, btnSessions, btnPayments, btnUsers};
        for (Button btn : navButtons) {
            btn.setOnMouseEntered(e -> { if (btn != activeButton) btn.setStyle(STYLE_HOVER); });
            btn.setOnMouseExited(e  -> { if (btn != activeButton) btn.setStyle(STYLE_INACTIVE); });
        }
    }

    public void setLoggedUser(User user) {
        this.loggedUser = user;
        lblUserName.setText(user.getUsername());
        lblUserRole.setText(user.getRole());

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
                    .stream().mapToDouble(p -> p.getAmount()).sum();
            lblTotalRevenue.setText(String.format("Rs. %.2f", totalRevenue));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleNavigation(ActionEvent event) {
        Button clicked = (Button) event.getSource();

        // Dashboard home
        if (clicked == btnDashboard) {
            setActiveButton(btnDashboard);
            lblPageTitle.setText("Dashboard");
            showDashboardHome();
            return;
        }

        // Quick action buttons
        if (clicked == btnQuickPatient) {
            setActiveButton(btnPatients);
            lblPageTitle.setText("Patients");
            loadPage("PatientRegistration.fxml");
            return;
        }
        if (clicked == btnQuickSession) {
            setActiveButton(btnSessions);
            lblPageTitle.setText("Therapy Sessions");
            loadPage("TherapySession.fxml");
            return;
        }
        if (clicked == btnQuickReg) {
            setActiveButton(btnRegistrations);
            lblPageTitle.setText("Registrations");
            loadPage("Registration.fxml");
            return;
        }

        // Sidebar nav buttons
        String fxml  = null;
        String title = "";

        if      (clicked == btnPatients)      { fxml = "PatientRegistration.fxml"; title = "Patients"; }
        else if (clicked == btnTherapists)    { fxml = "Therapist.fxml";           title = "Therapists"; }
        else if (clicked == btnPrograms)      { fxml = "TherapyProgram.fxml";      title = "Therapy Programs"; }
        else if (clicked == btnRegistrations) { fxml = "Registration.fxml";        title = "Registrations"; }
        else if (clicked == btnSessions)      { fxml = "TherapySession.fxml";      title = "Therapy Sessions"; }
        else if (clicked == btnPayments)      { fxml = "Payment.fxml";             title = "Payments"; }
        else if (clicked == btnUsers)         { fxml = "User.fxml";                title = "User Management"; }

        if (fxml != null) {
            setActiveButton(clicked);
            lblPageTitle.setText(title);
            loadPage(fxml);
        }
    }

    private void loadPage(String fxmlName) {
        contentArea.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(VIEW_PATH + fxmlName));
            contentArea.getChildren().add(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
            Label err = new Label("⚠ Could not load: " + fxmlName);
            err.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");
            contentArea.getChildren().add(err);
        }
    }

    private void showDashboardHome() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboardHome);
        loadDashboardStats();
    }

    private void setActiveButton(Button btn) {
        if (activeButton != null) activeButton.setStyle(STYLE_INACTIVE);
        btn.setStyle(STYLE_ACTIVE);
        activeButton = btn;
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(VIEW_PATH + "login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Serenity Mental Health Therapy Center");
            stage.setMaximized(false);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}