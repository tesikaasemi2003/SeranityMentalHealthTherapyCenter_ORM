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
import lk.ijse.seranity_mental_health_therapy_center.bo.custom.TherapistBO;
import lk.ijse.seranity_mental_health_therapy_center.bo.custom.TherapyProgramBO;
import lk.ijse.seranity_mental_health_therapy_center.dto.tm.TherapyProgramTM;
import lk.ijse.seranity_mental_health_therapy_center.entity.Therapist;
import lk.ijse.seranity_mental_health_therapy_center.entity.TherapyProgram;
import lk.ijse.seranity_mental_health_therapy_center.util.ValidationUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TherapyProgramController implements Initializable {

    @FXML private TextField     txtId;
    @FXML private TextField     txtName;
    @FXML private TextArea      txtDescription;
    @FXML private TextField     txtDurationWeeks;
    @FXML private TextField     txtFee;
    @FXML private ComboBox<String> cmbTherapist;
    @FXML private Label         lblMessage;
    @FXML private Button        btnSave;
    @FXML private TextField     txtSearch;

    @FXML private TableView<TherapyProgramTM>            tblProgram;
    @FXML private TableColumn<TherapyProgramTM, String>  colId;
    @FXML private TableColumn<TherapyProgramTM, String>  colName;
    @FXML private TableColumn<TherapyProgramTM, Integer> colDuration;
    @FXML private TableColumn<TherapyProgramTM, Double>  colFee;
    @FXML private TableColumn<TherapyProgramTM, String>  colTherapist;
    @FXML private TableColumn<TherapyProgramTM, Void>    colAction;

    private final TherapyProgramBO programBO =
            (TherapyProgramBO) BOFactory.getInstance().getBO(BOTypes.THERAPY_PROGRAM);
    private final TherapistBO therapistBO =
            (TherapistBO) BOFactory.getInstance().getBO(BOTypes.THERAPIST);

    private final ObservableList<TherapyProgramTM> masterList = FXCollections.observableArrayList();
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupSearch();
        loadTherapistCombo();
        loadTableData();
        generateNextId();
    }

    private void generateNextId() {
        try {
            txtId.setText(programBO.generateNextProgramId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTherapistCombo() {
        try {
            cmbTherapist.getItems().clear();
            List<Therapist> list = therapistBO.getAllTherapists();
            for (Therapist t : list) {
                cmbTherapist.getItems().add(t.getId() + " - " + t.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("durationWeeks"));
        colFee.setCellValueFactory(new PropertyValueFactory<>("fee"));
        colTherapist.setCellValueFactory(new PropertyValueFactory<>("therapistId"));

        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final HBox   box       = new HBox(6, btnEdit, btnDelete);
            {
                btnEdit.setStyle("-fx-background-color:#2B3990;-fx-text-fill:white;" +
                        "-fx-background-radius:5;-fx-padding:4 10;-fx-cursor:hand;-fx-font-size:11px;");
                btnDelete.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;" +
                        "-fx-background-radius:5;-fx-padding:4 10;-fx-cursor:hand;-fx-font-size:11px;");
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

    private void setupSearch() {
        FilteredList<TherapyProgramTM> filtered = new FilteredList<>(masterList, p -> true);
        txtSearch.textProperty().addListener((obs, o, val) -> filtered.setPredicate(row -> {
            if (val == null || val.isEmpty()) return true;
            String lower = val.toLowerCase();
            return row.getId().toLowerCase().contains(lower)
                    || row.getName().toLowerCase().contains(lower);
        }));
        tblProgram.setItems(filtered);
    }

    private void loadTableData() {
        masterList.clear();
        try {
            List<TherapyProgram> list = programBO.getAllTherapyPrograms();
            for (TherapyProgram p : list) {
                String therapistId = p.getTherapist() != null ? p.getTherapist().getId() : "";
                masterList.add(new TherapyProgramTM(
                        p.getId(), p.getName(), p.getDescription(),
                        p.getDurationWeeks(), p.getFee(), therapistId
                ));
            }
        } catch (Exception e) {
            showError("Failed to load: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        hideMessage();
        if (!validateInputs()) return;
        try {
            TherapyProgram program = new TherapyProgram();
            program.setId(txtId.getText().trim());
            program.setName(txtName.getText().trim());
            program.setDescription(txtDescription.getText().trim());
            program.setDurationWeeks(Integer.parseInt(txtDurationWeeks.getText().trim()));
            program.setFee(Double.parseDouble(txtFee.getText().trim()));

            if (cmbTherapist.getValue() != null) {
                String therapistId = cmbTherapist.getValue().split(" - ")[0];
                Therapist t = therapistBO.searchTherapist(therapistId);
                program.setTherapist(t);
            }

            boolean result = isEditMode
                    ? programBO.updateTherapyProgram(program)
                    : programBO.saveTherapyProgram(program);

            if (result) {
                showSuccess(isEditMode ? "Program updated!" : "Program saved!");
                loadTableData();
                handleClear();
            }
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    private void populateForm(TherapyProgramTM row) {
        isEditMode = true;
        txtId.setText(row.getId());
        txtId.setDisable(true);
        txtName.setText(row.getName());
        txtDescription.setText(row.getDescription());
        txtDurationWeeks.setText(String.valueOf(row.getDurationWeeks()));
        txtFee.setText(String.valueOf(row.getFee()));
        cmbTherapist.getItems().stream()
                .filter(s -> s.startsWith(row.getTherapistId()))
                .findFirst()
                .ifPresent(cmbTherapist::setValue);
        btnSave.setText("Update");
        txtName.requestFocus();
    }

    private void handleDelete(TherapyProgramTM row) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete \"" + row.getName() + "\"?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Confirm Delete");
        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    if (programBO.deleteTherapyProgram(row.getId())) {
                        masterList.remove(row);
                        showSuccess("Program deleted.");
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
        txtId.setDisable(false);
        txtName.clear(); txtDescription.clear();
        txtDurationWeeks.clear(); txtFee.clear();
        cmbTherapist.setValue(null);
        btnSave.setText("Save");
        clearFieldStyles();
        generateNextId();
        hideMessage();
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private boolean validateInputs() {
        clearFieldStyles();

        // Program Name
        if (!ValidationUtil.isNotEmpty(txtName.getText())) {
            showError(ValidationUtil.requiredError("Program Name"));
            highlight(txtName); txtName.requestFocus(); return false;
        }
        if (!ValidationUtil.isWithinLimit(txtName.getText(), 100)) {
            showError("⚠ Program Name cannot exceed 100 characters.");
            highlight(txtName); return false;
        }

        // Duration
        if (!ValidationUtil.isValidDuration(txtDurationWeeks.getText())) {
            showError(ValidationUtil.durationError());
            highlight(txtDurationWeeks); txtDurationWeeks.requestFocus(); return false;
        }

        // Fee
        if (!ValidationUtil.isValidAmount(txtFee.getText())) {
            showError(ValidationUtil.amountError());
            highlight(txtFee); txtFee.requestFocus(); return false;
        }

        // Description length (optional)
        if (!ValidationUtil.isWithinLimit(txtDescription.getText(), 500)) {
            showError("⚠ Description cannot exceed 500 characters."); return false;
        }

        // Therapist required
        if (cmbTherapist.getValue() == null) {
            showError(ValidationUtil.requiredError("Therapist")); return false;
        }

        return true;
    }

    // ── UI Helpers ────────────────────────────────────────────────────────────

    private void highlight(TextField field) {
        field.setStyle("-fx-border-color: #e74c3c; -fx-border-radius: 4;");
    }

    private void clearFieldStyles() {
        txtName.setStyle(""); txtDurationWeeks.setStyle(""); txtFee.setStyle("");
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
    private void hideMessage() {
        lblMessage.setVisible(false); lblMessage.setManaged(false);
    }
}