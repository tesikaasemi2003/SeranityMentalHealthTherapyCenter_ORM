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

    @FXML private TableView<TherapyProgramTM>           tblProgram;
    @FXML private TableColumn<TherapyProgramTM, String> colId;
    @FXML private TableColumn<TherapyProgramTM, String> colName;
    @FXML private TableColumn<TherapyProgramTM, Integer> colDuration;
    @FXML private TableColumn<TherapyProgramTM, Double> colFee;
    @FXML private TableColumn<TherapyProgramTM, String> colTherapist;
    @FXML private TableColumn<TherapyProgramTM, Void>   colAction;

    private final TherapyProgramBO programBO =
            (TherapyProgramBO) BOFactory.getInstance().getBO(BOTypes.THERAPY_PROGRAM);
    private final TherapistBO therapistBO =
            (TherapistBO) BOFactory.getInstance().getBO(BOTypes.THERAPIST);

    private static final String FEE_REGEX      = "^[0-9]+(\\.[0-9]{1,2})?$";
    private static final String DURATION_REGEX = "^[0-9]{1,3}$";

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
            e.printStackTrace();
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

            // Therapist set කරන්න
            if (cmbTherapist.getValue() != null) {
                String therapistId = cmbTherapist.getValue().split(" - ")[0];
                Therapist t = therapistBO.searchTherapist(therapistId);
                program.setTherapist(t);
            }

            boolean result;
            if (!isEditMode) {
                result = programBO.saveTherapyProgram(program);
            } else {
                result = programBO.updateTherapyProgram(program);
            }

            if (result) {
                showSuccess(isEditMode ? "Program updated!" : "Program saved!");
                loadTableData();
                handleClear();
            }
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
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
        // Therapist combo set
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
                    e.printStackTrace();
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
        generateNextId();
        hideMessage();
    }

    private boolean validateInputs() {
        if (txtName.getText().trim().isEmpty()) {
            showError("Program Name is required."); txtName.requestFocus(); return false;
        }
        if (!txtDurationWeeks.getText().trim().matches(DURATION_REGEX)) {
            showError("Duration must be a number (weeks)."); txtDurationWeeks.requestFocus(); return false;
        }
        if (!txtFee.getText().trim().matches(FEE_REGEX)) {
            showError("Fee must be a valid number (e.g. 80000)."); txtFee.requestFocus(); return false;
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
    private void hideMessage() {
        lblMessage.setVisible(false); lblMessage.setManaged(false);
    }
}