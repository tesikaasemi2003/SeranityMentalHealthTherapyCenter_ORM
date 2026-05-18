package lk.ijse.seranity_mental_health_therapy_center.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import lk.ijse.seranity_mental_health_therapy_center.bo.exception.DuplicateEntryException;


public class PatientRegistrationController implements Initializable {

    @FXML private TextField txtFullName;
    @FXML private TextField txtNIC;
    @FXML private DatePicker dpDOB;
    @FXML private ComboBox<String> cmbGender;
    @FXML private ComboBox<String> cmbBloodGroup;
    @FXML private ComboBox<String> cmbMaritalStatus;

    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtEmergencyName;
    @FXML private TextField txtEmergencyPhone;
    @FXML private TextField txtAddress;

    @FXML private ComboBox<String> cmbTherapist;
    @FXML private ComboBox<String> cmbTherapyType;
    @FXML private DatePicker dpRegistrationDate;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private TextArea txtMedicalNotes;

    @FXML private Label lblError;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbGender.getItems().addAll("Male", "Female", "Other", "Prefer not to say");
        cmbBloodGroup.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        cmbMaritalStatus.getItems().addAll("Single", "Married", "Divorced", "Widowed");
        cmbTherapyType.getItems().addAll(
                "Cognitive Behavioral Therapy (CBT)",
                "Dialectical Behavior Therapy (DBT)",
                "Psychodynamic Therapy",
                "Humanistic Therapy",
                "Group Therapy",
                "Family Therapy"
        );
        cmbStatus.getItems().addAll("Active", "Inactive", "On Hold");
        dpRegistrationDate.setValue(LocalDate.now());

        // TODO: Load therapists from DB → cmbTherapist.getItems().addAll(...)
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) return;

        String fullName      = txtFullName.getText().trim();
        String nic           = txtNIC.getText().trim();
        LocalDate dob        = dpDOB.getValue();
        String gender        = cmbGender.getValue();
        String bloodGroup    = cmbBloodGroup.getValue();
        String maritalStatus = cmbMaritalStatus.getValue();
        String phone         = txtPhone.getText().trim();
        String email         = txtEmail.getText().trim();
        String emergName     = txtEmergencyName.getText().trim();
        String emergPhone    = txtEmergencyPhone.getText().trim();
        String address       = txtAddress.getText().trim();
        String therapist     = cmbTherapist.getValue();
        String therapyType   = cmbTherapyType.getValue();
        LocalDate regDate    = dpRegistrationDate.getValue();
        String status        = cmbStatus.getValue();
        String notes         = txtMedicalNotes.getText().trim();

        // TODO: Build model and save to DB

        lblError.setStyle("-fx-text-fill: #2e7d32; -fx-font-size: 12px;");
        lblError.setText("✔  Patient registered successfully!");
    }

    @FXML
    private void handleClear() {
        txtFullName.clear(); txtNIC.clear(); dpDOB.setValue(null);
        cmbGender.getSelectionModel().clearSelection();
        cmbBloodGroup.getSelectionModel().clearSelection();
        cmbMaritalStatus.getSelectionModel().clearSelection();
        txtPhone.clear(); txtEmail.clear();
        txtEmergencyName.clear(); txtEmergencyPhone.clear(); txtAddress.clear();
        cmbTherapist.getSelectionModel().clearSelection();
        cmbTherapyType.getSelectionModel().clearSelection();
        dpRegistrationDate.setValue(LocalDate.now());
        cmbStatus.getSelectionModel().clearSelection();
        txtMedicalNotes.clear();
        lblError.setStyle("-fx-text-fill: #E53935; -fx-font-size: 12px;");
        lblError.setText("");
    }

    private boolean validateForm() {
        lblError.setStyle("-fx-text-fill: #E53935; -fx-font-size: 12px;");
        if (txtFullName.getText().trim().isEmpty()) {
            lblError.setText("⚠  Full name is required."); txtFullName.requestFocus(); return false;
        }
        if (txtNIC.getText().trim().isEmpty()) {
            lblError.setText("⚠  NIC number is required."); txtNIC.requestFocus(); return false;
        }
        if (dpDOB.getValue() == null) {
            lblError.setText("⚠  Date of birth is required."); return false;
        }
        if (cmbGender.getValue() == null) {
            lblError.setText("⚠  Please select a gender."); return false;
        }
        if (txtPhone.getText().trim().isEmpty()) {
            lblError.setText("⚠  Phone number is required."); txtPhone.requestFocus(); return false;
        }
        if (!txtPhone.getText().trim().matches("^[0-9]{10}$")) {
            lblError.setText("⚠  Phone number must be 10 digits."); txtPhone.requestFocus(); return false;
        }
        if (!txtEmail.getText().trim().isEmpty() &&
                !txtEmail.getText().trim().matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            lblError.setText("⚠  Invalid email address."); txtEmail.requestFocus(); return false;
        }
        if (dpRegistrationDate.getValue() == null) {
            lblError.setText("⚠  Registration date is required."); return false;
        }
        lblError.setText("");
        return true;
    }
}