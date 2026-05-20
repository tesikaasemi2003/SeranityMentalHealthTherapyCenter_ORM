package lk.ijse.seranity_mental_health_therapy_center.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import lk.ijse.seranity_mental_health_therapy_center.bo.BOFactory;
import lk.ijse.seranity_mental_health_therapy_center.bo.BOTypes;
import lk.ijse.seranity_mental_health_therapy_center.bo.custom.PatientBO;
import lk.ijse.seranity_mental_health_therapy_center.bo.custom.TherapistBO;
import lk.ijse.seranity_mental_health_therapy_center.dto.tm.PatientTM;
import lk.ijse.seranity_mental_health_therapy_center.entity.Patient;
import lk.ijse.seranity_mental_health_therapy_center.entity.Therapist;
import lk.ijse.seranity_mental_health_therapy_center.util.ValidationUtil;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class PatientRegistrationController implements Initializable {

    // Form Fields
    @FXML private TextField txtFullName, txtNIC, txtPhone, txtEmail,
            txtEmergencyName, txtEmergencyPhone, txtAddress, txtSearch;
    @FXML private DatePicker dpDOB, dpRegistrationDate;
    @FXML private ComboBox<String> cmbGender, cmbBloodGroup, cmbMaritalStatus,
            cmbTherapist, cmbTherapyType, cmbStatus;
    @FXML private TextArea txtMedicalNotes;
    @FXML private Label lblError;
    @FXML private Button btnSave;

    // Table Fields
    @FXML private TableView<PatientTM> tblPatients;
    @FXML private TableColumn<PatientTM, String> colId, colName, colNIC, colPhone, colEmail;
    @FXML private TableColumn<PatientTM, Void> colAction;

    private final PatientBO patientBO =
            (PatientBO) BOFactory.getInstance().getBO(BOTypes.PATIENT);
    private final TherapistBO therapistBO =
            (TherapistBO) BOFactory.getInstance().getBO(BOTypes.THERAPIST);

    private final ObservableList<PatientTM> masterList = FXCollections.observableArrayList();
    private boolean isEditMode = false;
    private String selectedPatientId = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupComboBoxes();
        setupTable();
        loadTherapists();
        loadTableData();
        setupSearch();
        dpRegistrationDate.setValue(LocalDate.now());
    }

    private void setupComboBoxes() {
        cmbGender.getItems().addAll("Male", "Female", "Other");
        cmbBloodGroup.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        cmbMaritalStatus.getItems().addAll("Single", "Married", "Divorced", "Widowed");
        cmbTherapyType.getItems().addAll("CBT", "MBSR", "DBT", "Group Therapy", "Family Counseling");
        cmbStatus.getItems().addAll("Active", "Inactive", "On Hold");
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colNIC.setCellValueFactory(new PropertyValueFactory<>("nic"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit   = new Button("Edit");
            private final Button btnDelete = new Button("Del");
            private final HBox box = new HBox(5, btnEdit, btnDelete);
            {
                btnEdit.setStyle("-fx-background-color: #2B3990; -fx-text-fill: white; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                btnEdit.setOnAction(e   -> populateForm(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void loadTableData() {
        masterList.clear();
        try {
            List<Patient> list = patientBO.getAllPatients();
            for (Patient p : list) {
                masterList.add(new PatientTM(
                        p.getId(), p.getName(), p.getNic(),
                        p.getEmail(), p.getPhone(), p.getMedicalHistory()));
            }
            tblPatients.setItems(masterList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSearch() {
        FilteredList<PatientTM> filtered = new FilteredList<>(masterList, p -> true);
        txtSearch.textProperty().addListener((obs, old, val) ->
                filtered.setPredicate(patient -> {
                    if (val == null || val.isEmpty()) return true;
                    String lower = val.toLowerCase();
                    return patient.getName().toLowerCase().contains(lower)
                            || patient.getNic().contains(val);
                }));
        tblPatients.setItems(filtered);
    }

    private void loadTherapists() {
        try {
            List<Therapist> list = therapistBO.getAllTherapists();
            cmbTherapist.getItems().clear();
            for (Therapist t : list)
                cmbTherapist.getItems().add(t.getId() + " - " + t.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) return;
        try {
            Patient patient = new Patient();
            patient.setId(isEditMode
                    ? selectedPatientId
                    : patientBO.generateNextPatientId());
            patient.setName(txtFullName.getText().trim());
            patient.setNic(txtNIC.getText().trim());
            patient.setEmail(txtEmail.getText().trim());
            patient.setPhone(txtPhone.getText().trim());
            patient.setMedicalHistory("DOB:" + dpDOB.getValue()
                    + "|G:" + cmbGender.getValue()
                    + "|Notes:" + txtMedicalNotes.getText());

            boolean result = isEditMode
                    ? patientBO.updatePatient(patient)
                    : patientBO.savePatient(patient);

            if (result) {
                showSuccess(isEditMode ? "✔ Updated!" : "✔ Registered!");
                loadTableData();
                handleClear();
            }
        } catch (Exception e) {
            showError("⚠ Error: " + e.getMessage());
        }
    }

    private void populateForm(PatientTM tm) {
        isEditMode = true;
        selectedPatientId = tm.getId();
        txtFullName.setText(tm.getName());
        txtNIC.setText(tm.getNic());
        txtPhone.setText(tm.getPhone());
        txtEmail.setText(tm.getEmail());
        btnSave.setText("Update Patient");
    }

    private void handleDelete(PatientTM tm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + tm.getName() + "?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    if (patientBO.deletePatient(tm.getId())) {
                        showSuccess("Deleted!");
                        loadTableData();
                    }
                } catch (Exception e) {
                    showError(e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleClear() {
        isEditMode = false;
        selectedPatientId = "";
        txtFullName.clear();
        txtNIC.clear();
        dpDOB.setValue(null);
        txtPhone.clear();
        txtEmail.clear();
        txtEmergencyName.clear();
        txtEmergencyPhone.clear();
        txtAddress.clear();
        txtMedicalNotes.clear();
        cmbGender.setValue(null);
        cmbBloodGroup.setValue(null);
        cmbMaritalStatus.setValue(null);
        cmbTherapist.setValue(null);
        cmbTherapyType.setValue(null);
        cmbStatus.setValue(null);
        dpRegistrationDate.setValue(LocalDate.now());
        btnSave.setText("Register Patient");
        lblError.setText("");
        clearFieldStyles();
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private boolean validateForm() {
        clearFieldStyles();

        // Full Name
        if (!ValidationUtil.isValidName(txtFullName.getText())) {
            showError(ValidationUtil.nameError());
            highlight(txtFullName);
            txtFullName.requestFocus();
            return false;
        }

        // NIC
        if (!ValidationUtil.isValidNIC(txtNIC.getText())) {
            showError(ValidationUtil.nicError());
            highlight(txtNIC);
            txtNIC.requestFocus();
            return false;
        }

        // Date of Birth
        if (dpDOB.getValue() == null) {
            showError(ValidationUtil.requiredError("Date of Birth"));
            return false;
        }
        if (dpDOB.getValue().isAfter(LocalDate.now())) {
            showError("⚠ Date of Birth cannot be a future date.");
            return false;
        }

        // Gender
        if (cmbGender.getValue() == null) {
            showError(ValidationUtil.requiredError("Gender"));
            return false;
        }

        // Phone
        if (!ValidationUtil.isValidPhone(txtPhone.getText())) {
            showError(ValidationUtil.phoneError());
            highlight(txtPhone);
            txtPhone.requestFocus();
            return false;
        }

        // Email (optional — validate only if entered)
        if (ValidationUtil.isNotEmpty(txtEmail.getText())) {
            if (!ValidationUtil.isValidEmail(txtEmail.getText())) {
                showError(ValidationUtil.emailError());
                highlight(txtEmail);
                txtEmail.requestFocus();
                return false;
            }
        }

        // Emergency Phone (optional — validate only if entered)
        if (ValidationUtil.isNotEmpty(txtEmergencyPhone.getText())) {
            if (!ValidationUtil.isValidPhone(txtEmergencyPhone.getText())) {
                showError("⚠ Invalid emergency contact phone number.");
                highlight(txtEmergencyPhone);
                txtEmergencyPhone.requestFocus();
                return false;
            }
        }

        // Address (optional — validate only if entered)
        if (ValidationUtil.isNotEmpty(txtAddress.getText())) {
            if (!ValidationUtil.isValidAddress(txtAddress.getText())) {
                showError(ValidationUtil.requiredError("Valid Address (min 5 characters)"));
                highlight(txtAddress);
                return false;
            }
        }

        // Medical Notes length check
        if (!ValidationUtil.isWithinLimit(txtMedicalNotes.getText(), 1000)) {
            showError("⚠ Medical notes cannot exceed 1000 characters.");
            return false;
        }

        return true;
    }

    // ── UI Helpers ────────────────────────────────────────────────────────────

    /** Highlight invalid field with red border */
    private void highlight(TextField field) {
        field.setStyle("-fx-border-color: #e74c3c; -fx-border-radius: 4;");
    }

    /** Clear all field highlight styles */
    private void clearFieldStyles() {
        txtFullName.setStyle("");
        txtNIC.setStyle("");
        txtPhone.setStyle("");
        txtEmail.setStyle("");
        txtEmergencyPhone.setStyle("");
        txtAddress.setStyle("");
    }

    private void showError(String msg) {
        lblError.setStyle("-fx-text-fill: #E53935;");
        lblError.setText(msg);
    }

    private void showSuccess(String msg) {
        lblError.setStyle("-fx-text-fill: #2e7d32;");
        lblError.setText(msg);
    }
}
