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
import lk.ijse.seranity_mental_health_therapy_center.bo.custom.RegistrationBO;
import lk.ijse.seranity_mental_health_therapy_center.bo.custom.TherapyProgramBO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Patient;
import lk.ijse.seranity_mental_health_therapy_center.entity.Registration;
import lk.ijse.seranity_mental_health_therapy_center.entity.TherapyProgram;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable {

    @FXML private TextField txtId;
    @FXML private ComboBox<String> cmbPatient;
    @FXML private ComboBox<String> cmbProgram;
    @FXML private DatePicker dpRegistrationDate;
    @FXML private TextField txtAmountPaid;
    @FXML private Label lblMessage;
    @FXML private Button btnSave;
    @FXML private TextField txtSearch;

    @FXML private TableView<Registration> tblRegistration;
    @FXML private TableColumn<Registration, String> colId;
    @FXML private TableColumn<Registration, String> colPatient;
    @FXML private TableColumn<Registration, String> colProgram;
    @FXML private TableColumn<Registration, LocalDate> colDate;
    @FXML private TableColumn<Registration, Double> colAmount;
    @FXML private TableColumn<Registration, Void> colAction;

    private final RegistrationBO registrationBO =
            (RegistrationBO) BOFactory.getInstance().getBO(BOTypes.REGISTRATION);
    private final PatientBO patientBO =
            (PatientBO) BOFactory.getInstance().getBO(BOTypes.PATIENT);
    private final TherapyProgramBO programBO =
            (TherapyProgramBO) BOFactory.getInstance().getBO(BOTypes.THERAPY_PROGRAM);

    private final ObservableList<Registration> masterList = FXCollections.observableArrayList();
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupSearch();
        loadCombos();
        loadTableData();
        generateNextId();
        dpRegistrationDate.setValue(LocalDate.now());
    }

    private void generateNextId() {
        try { txtId.setText(registrationBO.generateNextRegistrationId()); }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void loadCombos() {
        try {
            cmbPatient.getItems().clear();
            for (Patient p : patientBO.getAllPatients())
                cmbPatient.getItems().add(p.getId() + " - " + p.getName());

            cmbProgram.getItems().clear();
            for (TherapyProgram p : programBO.getAllTherapyPrograms())
                cmbProgram.getItems().add(p.getId() + " - " + p.getName());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));

        // Patient name column
        colPatient.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) { setText(null); return; }
                Registration r = (Registration) getTableRow().getItem();
                setText(r.getPatient() != null ? r.getPatient().getName() : "");
            }
        });

        // Program name column
        colProgram.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) { setText(null); return; }
                Registration r = (Registration) getTableRow().getItem();
                setText(r.getTherapyProgram() != null ? r.getTherapyProgram().getName() : "");
            }
        });

        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final HBox box = new HBox(6, btnEdit, btnDelete);
            {
                btnEdit.setStyle("-fx-background-color:#2B3990;-fx-text-fill:white;-fx-background-radius:5;-fx-padding:4 10;-fx-cursor:hand;-fx-font-size:11px;");
                btnDelete.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;-fx-background-radius:5;-fx-padding:4 10;-fx-cursor:hand;-fx-font-size:11px;");
                btnEdit.setOnAction(e   -> populateForm(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void setupSearch() {
        FilteredList<Registration> filtered = new FilteredList<>(masterList, p -> true);
        txtSearch.textProperty().addListener((obs, o, val) -> filtered.setPredicate(row -> {
            if (val == null || val.isEmpty()) return true;
            String lower = val.toLowerCase();
            boolean matchId = row.getId().toLowerCase().contains(lower);
            boolean matchPatient = row.getPatient() != null && row.getPatient().getName().toLowerCase().contains(lower);
            boolean matchProgram = row.getTherapyProgram() != null && row.getTherapyProgram().getName().toLowerCase().contains(lower);
            return matchId || matchPatient || matchProgram;
        }));
        tblRegistration.setItems(filtered);
    }

    private void loadTableData() {
        masterList.clear();
        try {
            masterList.addAll(registrationBO.getAllRegistrations());
        } catch (Exception e) {
            showError("Failed to load: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        hideMessage();
        if (!validateInputs()) return;
        try {
            String patientId = cmbPatient.getValue().split(" - ")[0];
            String programId = cmbProgram.getValue().split(" - ")[0];

            Registration reg = new Registration();
            reg.setId(txtId.getText().trim());
            reg.setRegistrationDate(dpRegistrationDate.getValue());
            reg.setAmountPaid(Double.parseDouble(txtAmountPaid.getText().trim()));
            reg.setPatient(patientBO.searchPatient(patientId));
            reg.setTherapyProgram(programBO.searchTherapyProgram(programId));

            boolean result = isEditMode
                    ? registrationBO.updateRegistration(reg)
                    : registrationBO.saveRegistration(reg);

            if (result) {
                showSuccess(isEditMode ? "Registration updated!" : "Registration saved!");
                loadTableData();
                handleClear();
            }
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateForm(Registration row) {
        isEditMode = true;
        txtId.setText(row.getId());
        dpRegistrationDate.setValue(row.getRegistrationDate());
        txtAmountPaid.setText(String.valueOf(row.getAmountPaid()));
        if (row.getPatient() != null)
            cmbPatient.getItems().stream()
                    .filter(s -> s.startsWith(row.getPatient().getId()))
                    .findFirst().ifPresent(cmbPatient::setValue);
        if (row.getTherapyProgram() != null)
            cmbProgram.getItems().stream()
                    .filter(s -> s.startsWith(row.getTherapyProgram().getId()))
                    .findFirst().ifPresent(cmbProgram::setValue);
        btnSave.setText("Update");
    }

    private void handleDelete(Registration row) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete registration \"" + row.getId() + "\"?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Confirm Delete");
        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    if (registrationBO.deleteRegistration(row.getId())) {
                        masterList.remove(row);
                        showSuccess("Registration deleted.");
                        generateNextId();
                    }
                } catch (Exception e) {
                    showError("Delete failed: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleClear() {
        isEditMode = false;
        cmbPatient.setValue(null); cmbProgram.setValue(null);
        dpRegistrationDate.setValue(LocalDate.now());
        txtAmountPaid.clear();
        btnSave.setText("Save");
        generateNextId(); hideMessage();
    }

    private boolean validateInputs() {
        if (cmbPatient.getValue() == null) { showError("Please select a Patient."); return false; }
        if (cmbProgram.getValue() == null) { showError("Please select a Program."); return false; }
        if (dpRegistrationDate.getValue() == null) { showError("Registration Date is required."); return false; }
        if (!txtAmountPaid.getText().trim().matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            showError("Amount must be a valid number."); txtAmountPaid.requestFocus(); return false;
        }
        return true;
    }

    private void showError(String msg) {
        lblMessage.setText("⚠ " + msg);
        lblMessage.setStyle("-fx-text-fill:#c0392b;-fx-font-size:13px;-fx-font-weight:bold;");
        lblMessage.setVisible(true); lblMessage.setManaged(true);
    }
    private void showSuccess(String msg) {
        lblMessage.setText("✔ " + msg);
        lblMessage.setStyle("-fx-text-fill:#27ae60;-fx-font-size:13px;-fx-font-weight:bold;");
        lblMessage.setVisible(true); lblMessage.setManaged(true);
    }
    private void hideMessage() { lblMessage.setVisible(false); lblMessage.setManaged(false); }
}