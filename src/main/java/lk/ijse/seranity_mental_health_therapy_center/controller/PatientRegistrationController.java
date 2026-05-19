package lk.ijse.seranity_mental_health_therapy_center.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lk.ijse.seranity_mental_health_therapy_center.bo.BOFactory;
import lk.ijse.seranity_mental_health_therapy_center.bo.BOTypes;
import lk.ijse.seranity_mental_health_therapy_center.bo.custom.PatientBO;
import lk.ijse.seranity_mental_health_therapy_center.bo.custom.TherapistBO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Patient;
import lk.ijse.seranity_mental_health_therapy_center.entity.Therapist;
import lk.ijse.seranity_mental_health_therapy_center.bo.exception.DuplicateEntryException;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

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

    private final PatientBO patientBO =
            (PatientBO) BOFactory.getInstance().getBO(BOTypes.PATIENT);
    private final TherapistBO therapistBO =
            (TherapistBO) BOFactory.getInstance().getBO(BOTypes.THERAPIST);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbGender.getItems().addAll("Male", "Female", "Other", "Prefer not to say");
        cmbBloodGroup.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        cmbMaritalStatus.getItems().addAll("Single", "Married", "Divorced", "Widowed");
        cmbTherapyType.getItems().addAll(
                "MT1001 - Cognitive Behavioral Therapy",
                "MT1002 - Mindfulness-Based Stress Reduction",
                "MT1003 - Dialectical Behavior Therapy",
                "MT1004 - Group Therapy Sessions",
                "MT1005 - Family Counseling"
        );
        cmbStatus.getItems().addAll("Active", "Inactive", "On Hold");
        dpRegistrationDate.setValue(LocalDate.now());
        loadTherapists();
    }

    private void loadTherapists() {
        try {
            List<Therapist> list = therapistBO.getAllTherapists();
            cmbTherapist.getItems().clear();
            for (Therapist t : list) {
                cmbTherapist.getItems().add(t.getId() + " - " + t.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) return;

        try {
            Patient patient = new Patient();
            patient.setId(patientBO.generateNextPatientId());
            patient.setName(txtFullName.getText().trim());
            patient.setNic(txtNIC.getText().trim());
            patient.setEmail(txtEmail.getText().trim());
            patient.setPhone(txtPhone.getText().trim());
            patient.setMedicalHistory(
                    "DOB: " + dpDOB.getValue() +
                            " | Gender: " + cmbGender.getValue() +
                            " | Blood: " + (cmbBloodGroup.getValue() != null ? cmbBloodGroup.getValue() : "") +
                            " | Address: " + txtAddress.getText().trim() +
                            " | Emergency: " + txtEmergencyName.getText().trim() +
                            " (" + txtEmergencyPhone.getText().trim() + ")" +
                            " | Notes: " + txtMedicalNotes.getText().trim()
            );

            if (patientBO.savePatient(patient)) {
                showSuccess("✔ Patient registered! ID: " + patient.getId());
                handleClear();
            }

        } catch (DuplicateEntryException e) {
            showError("⚠ " + e.getMessage());
        } catch (Exception e) {
            showError("⚠ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClear() {
        txtFullName.clear(); txtNIC.clear(); dpDOB.setValue(null);
        cmbGender.setValue(null); cmbBloodGroup.setValue(null);
        cmbMaritalStatus.setValue(null);
        txtPhone.clear(); txtEmail.clear();
        txtEmergencyName.clear(); txtEmergencyPhone.clear();
        txtAddress.clear();
        cmbTherapist.setValue(null); cmbTherapyType.setValue(null);
        dpRegistrationDate.setValue(LocalDate.now());
        cmbStatus.setValue(null); txtMedicalNotes.clear();
        lblError.setText("");
    }

    private boolean validateForm() {
        lblError.setStyle("-fx-text-fill: #E53935; -fx-font-size: 12px;");

        if (txtFullName.getText().trim().isEmpty()) {
            showError("⚠ Full name is required."); txtFullName.requestFocus(); return false;
        }
        if (!txtNIC.getText().trim().matches("^([0-9]{9}[vVxX]|[0-9]{12})$")) {
            showError("⚠ Invalid NIC. Use 9 digits + V/X or 12 digits."); txtNIC.requestFocus(); return false;
        }
        if (dpDOB.getValue() == null) {
            showError("⚠ Date of birth is required."); return false;
        }
        if (cmbGender.getValue() == null) {
            showError("⚠ Please select a gender."); return false;
        }
        if (!txtPhone.getText().trim().matches("^(\\+94|0)[0-9]{9}$")) {
            showError("⚠ Invalid phone (e.g. 0771234567)."); txtPhone.requestFocus(); return false;
        }
        if (!txtEmail.getText().trim().isEmpty() &&
                !txtEmail.getText().trim().matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            showError("⚠ Invalid email address."); txtEmail.requestFocus(); return false;
        }
        if (dpRegistrationDate.getValue() == null) {
            showError("⚠ Registration date is required."); return false;
        }
        return true;
    }

    private void showError(String msg) {
        lblError.setStyle("-fx-text-fill: #E53935; -fx-font-size: 12px;");
        lblError.setText(msg);
    }

    private void showSuccess(String msg) {
        lblError.setStyle("-fx-text-fill: #2e7d32; -fx-font-size: 12px;");
        lblError.setText(msg);
    }
}