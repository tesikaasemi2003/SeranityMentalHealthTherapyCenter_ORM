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
import lk.ijse.seranity_mental_health_therapy_center.bo.custom.TherapySessionBO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Patient;
import lk.ijse.seranity_mental_health_therapy_center.entity.Therapist;
import lk.ijse.seranity_mental_health_therapy_center.entity.TherapySession;
import lk.ijse.seranity_mental_health_therapy_center.util.ValidationUtil;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class TherapySessionController implements Initializable {

    @FXML private TextField txtId;
    @FXML private ComboBox<String> cmbPatient;
    @FXML private ComboBox<String> cmbTherapist;
    @FXML private DatePicker dpSessionDate;
    @FXML private TextField txtStartTime;
    @FXML private TextField txtEndTime;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private TextArea txtNotes;
    @FXML private Label lblMessage;
    @FXML private Button btnSave;
    @FXML private TextField txtSearch;

    @FXML private TableView<TherapySession> tblSession;
    @FXML private TableColumn<TherapySession, String>    colId;
    @FXML private TableColumn<TherapySession, String>    colPatient;
    @FXML private TableColumn<TherapySession, String>    colTherapist;
    @FXML private TableColumn<TherapySession, LocalDate> colDate;
    @FXML private TableColumn<TherapySession, LocalTime> colStartTime;
    @FXML private TableColumn<TherapySession, LocalTime> colEndTime;
    @FXML private TableColumn<TherapySession, String>    colStatus;
    @FXML private TableColumn<TherapySession, Void>      colAction;

    private final TherapySessionBO sessionBO =
            (TherapySessionBO) BOFactory.getInstance().getBO(BOTypes.THERAPY_SESSION);
    private final PatientBO patientBO =
            (PatientBO) BOFactory.getInstance().getBO(BOTypes.PATIENT);
    private final TherapistBO therapistBO =
            (TherapistBO) BOFactory.getInstance().getBO(BOTypes.THERAPIST);

    private final ObservableList<TherapySession> masterList = FXCollections.observableArrayList();
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tblSession.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cmbStatus.getItems().addAll("SCHEDULED", "COMPLETED", "CANCELLED");
        setupTable();
        setupSearch();
        loadCombos();
        loadTableData();
        generateNextId();
        dpSessionDate.setValue(LocalDate.now());
    }

    private void generateNextId() {
        try { txtId.setText(sessionBO.generateNextSessionId()); }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void loadCombos() {
        try {
            cmbPatient.getItems().clear();
            for (Patient p : patientBO.getAllPatients())
                cmbPatient.getItems().add(p.getId() + " - " + p.getName());

            cmbTherapist.getItems().clear();
            for (Therapist t : therapistBO.getAllTherapists())
                cmbTherapist.getItems().add(t.getId() + " - " + t.getName());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("sessionDate"));
        colStartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        colEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colPatient.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) { setText(null); return; }
                TherapySession s = (TherapySession) getTableRow().getItem();
                setText(s.getPatient() != null ? s.getPatient().getName() : "");
            }
        });

        colTherapist.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) { setText(null); return; }
                TherapySession s = (TherapySession) getTableRow().getItem();
                setText(s.getTherapist() != null ? s.getTherapist().getName() : "");
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
        FilteredList<TherapySession> filtered = new FilteredList<>(masterList, p -> true);
        txtSearch.textProperty().addListener((obs, o, val) -> filtered.setPredicate(row -> {
            if (val == null || val.isEmpty()) return true;
            String lower = val.toLowerCase();
            boolean matchId     = row.getId().toLowerCase().contains(lower);
            boolean matchStatus = row.getStatus() != null && row.getStatus().toLowerCase().contains(lower);
            boolean matchPat    = row.getPatient() != null && row.getPatient().getName().toLowerCase().contains(lower);
            boolean matchTher   = row.getTherapist() != null && row.getTherapist().getName().toLowerCase().contains(lower);
            return matchId || matchStatus || matchPat || matchTher;
        }));
        tblSession.setItems(filtered);
    }

    private void loadTableData() {
        masterList.clear();
        try { masterList.addAll(sessionBO.getAllTherapySessions()); }
        catch (Exception e) { showError("Failed to load: " + e.getMessage()); }
    }

    @FXML
    private void handleSave() {
        hideMessage();
        if (!validateInputs()) return;
        try {
            String patientId   = cmbPatient.getValue().split(" - ")[0];
            String therapistId = cmbTherapist.getValue().split(" - ")[0];

            TherapySession session = new TherapySession();
            session.setId(txtId.getText().trim());
            session.setSessionDate(dpSessionDate.getValue());
            session.setStartTime(LocalTime.parse(txtStartTime.getText().trim()));
            session.setEndTime(LocalTime.parse(txtEndTime.getText().trim()));
            session.setStatus(cmbStatus.getValue() != null ? cmbStatus.getValue() : "SCHEDULED");
            session.setNotes(txtNotes.getText().trim());
            session.setPatient(patientBO.searchPatient(patientId));
            session.setTherapist(therapistBO.searchTherapist(therapistId));

            boolean result = isEditMode
                    ? sessionBO.updateTherapySession(session)
                    : sessionBO.saveTherapySession(session);

            if (result) {
                showSuccess(isEditMode ? "Session updated!" : "Session saved!");
                loadTableData();
                handleClear();
            }
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateForm(TherapySession row) {
        isEditMode = true;
        txtId.setText(row.getId());
        dpSessionDate.setValue(row.getSessionDate());
        txtStartTime.setText(row.getStartTime() != null ? row.getStartTime().toString() : "");
        txtEndTime.setText(row.getEndTime() != null ? row.getEndTime().toString() : "");
        cmbStatus.setValue(row.getStatus());
        txtNotes.setText(row.getNotes());
        if (row.getPatient() != null)
            cmbPatient.getItems().stream().filter(s -> s.startsWith(row.getPatient().getId()))
                    .findFirst().ifPresent(cmbPatient::setValue);
        if (row.getTherapist() != null)
            cmbTherapist.getItems().stream().filter(s -> s.startsWith(row.getTherapist().getId()))
                    .findFirst().ifPresent(cmbTherapist::setValue);
        btnSave.setText("Update");
    }

    private void handleDelete(TherapySession row) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete session \"" + row.getId() + "\"?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Confirm Delete");
        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    if (sessionBO.deleteTherapySession(row.getId())) {
                        masterList.remove(row);
                        showSuccess("Session deleted.");
                        generateNextId();
                    }
                } catch (Exception e) { showError("Delete failed: " + e.getMessage()); }
            }
        });
    }

    @FXML
    private void handleClear() {
        isEditMode = false;
        cmbPatient.setValue(null); cmbTherapist.setValue(null);
        dpSessionDate.setValue(LocalDate.now());
        txtStartTime.clear(); txtEndTime.clear();
        cmbStatus.setValue(null); txtNotes.clear();
        btnSave.setText("Save");
        clearFieldStyles();
        generateNextId(); hideMessage();
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private boolean validateInputs() {
        clearFieldStyles();

        // Patient
        if (cmbPatient.getValue() == null) {
            showError(ValidationUtil.requiredError("Patient")); return false;
        }

        // Therapist
        if (cmbTherapist.getValue() == null) {
            showError(ValidationUtil.requiredError("Therapist")); return false;
        }

        // Session Date
        if (dpSessionDate.getValue() == null) {
            showError(ValidationUtil.requiredError("Session Date")); return false;
        }

        // Start Time
        if (!ValidationUtil.isValidTime(txtStartTime.getText())) {
            showError(ValidationUtil.timeError());
            highlight(txtStartTime); txtStartTime.requestFocus(); return false;
        }

        // End Time
        if (!ValidationUtil.isValidTime(txtEndTime.getText())) {
            showError(ValidationUtil.timeError());
            highlight(txtEndTime); txtEndTime.requestFocus(); return false;
        }

        // End time must be after start time
        if (!ValidationUtil.isEndTimeAfterStartTime(txtStartTime.getText(), txtEndTime.getText())) {
            showError(ValidationUtil.endTimeError());
            highlight(txtEndTime); txtEndTime.requestFocus(); return false;
        }

        // Notes length
        if (!ValidationUtil.isWithinLimit(txtNotes.getText(), 1000)) {
            showError("⚠ Notes cannot exceed 1000 characters."); return false;
        }

        return true;
    }

    // ── UI Helpers ────────────────────────────────────────────────────────────

    private void highlight(TextField field) {
        field.setStyle("-fx-border-color: #e74c3c; -fx-border-radius: 4;");
    }

    private void clearFieldStyles() {
        txtStartTime.setStyle(""); txtEndTime.setStyle("");
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
