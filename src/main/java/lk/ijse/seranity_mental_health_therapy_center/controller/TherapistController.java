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
import lk.ijse.seranity_mental_health_therapy_center.dto.tm.TherapistTM;
import lk.ijse.seranity_mental_health_therapy_center.entity.Therapist;
import lk.ijse.seranity_mental_health_therapy_center.util.ValidationUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TherapistController implements Initializable {

    @FXML private TextField txtId;
    @FXML private TextField txtName;
    @FXML private ComboBox<String> cmbSpecialization;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> cmbAvailability;
    @FXML private Label lblMessage;
    @FXML private Button btnSave;
    @FXML private TextField txtSearch;

    @FXML private TableView<TherapistTM> tblTherapist;
    @FXML private TableColumn<TherapistTM, String> colId;
    @FXML private TableColumn<TherapistTM, String> colName;
    @FXML private TableColumn<TherapistTM, String> colSpecialization;
    @FXML private TableColumn<TherapistTM, String> colPhone;
    @FXML private TableColumn<TherapistTM, String> colEmail;
    @FXML private TableColumn<TherapistTM, Void>   colAction;

    private final TherapistBO therapistBO =
            (TherapistBO) BOFactory.getInstance().getBO(BOTypes.THERAPIST);

    private final ObservableList<TherapistTM> masterList = FXCollections.observableArrayList();
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBoxes();
        setupTable();
        setupSearch();
        loadTableData();
        generateNextId();
    }

    private void generateNextId() {
        try {
            txtId.setText(therapistBO.generateNextTherapistId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupComboBoxes() {
        cmbSpecialization.getItems().addAll(
                "Cognitive Behavioral Therapy",
                "Mindfulness-Based Stress Reduction",
                "Dialectical Behavior Therapy",
                "Group Therapy",
                "Family Counseling"
        );
        cmbAvailability.getItems().addAll("Available", "Busy", "On Leave");
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSpecialization.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

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
        FilteredList<TherapistTM> filtered = new FilteredList<>(masterList, p -> true);
        txtSearch.textProperty().addListener((obs, o, val) -> filtered.setPredicate(row -> {
            if (val == null || val.isEmpty()) return true;
            String lower = val.toLowerCase();
            return row.getName().toLowerCase().contains(lower)
                    || row.getSpecialization().toLowerCase().contains(lower)
                    || row.getPhone().toLowerCase().contains(lower)
                    || row.getEmail().toLowerCase().contains(lower);
        }));
        tblTherapist.setItems(filtered);
    }

    private void loadTableData() {
        masterList.clear();
        try {
            List<Therapist> list = therapistBO.getAllTherapists();
            for (Therapist t : list) {
                masterList.add(new TherapistTM(
                        t.getId(), t.getName(),
                        t.getEmail(), t.getPhone(),
                        t.getSpecialization()
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
            Therapist therapist = new Therapist();
            therapist.setId(txtId.getText().trim());
            therapist.setName(txtName.getText().trim());
            therapist.setEmail(txtEmail.getText().trim());
            therapist.setPhone(txtPhone.getText().trim());
            therapist.setSpecialization(cmbSpecialization.getValue());

            boolean result = isEditMode
                    ? therapistBO.updateTherapist(therapist)
                    : therapistBO.saveTherapist(therapist);

            if (result) {
                showSuccess(isEditMode ? "Therapist updated!" : "Therapist saved!");
                loadTableData();
                handleClear();
            }
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    private void populateForm(TherapistTM row) {
        isEditMode = true;
        txtId.setText(row.getId());
        txtName.setText(row.getName());
        txtEmail.setText(row.getEmail());
        txtPhone.setText(row.getPhone());
        cmbSpecialization.setValue(row.getSpecialization());
        btnSave.setText("Update");
        txtName.requestFocus();
    }

    private void handleDelete(TherapistTM row) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete \"" + row.getName() + "\"?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Confirm Delete");
        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    if (therapistBO.deleteTherapist(row.getId())) {
                        masterList.remove(row);
                        showSuccess("Therapist deleted.");
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
        txtName.clear(); txtPhone.clear(); txtEmail.clear();
        cmbSpecialization.setValue(null);
        cmbAvailability.setValue(null);
        btnSave.setText("Save");
        clearFieldStyles();
        generateNextId();
        hideMessage();
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private boolean validateInputs() {
        clearFieldStyles();

        // Name
        if (!ValidationUtil.isValidName(txtName.getText())) {
            showError(ValidationUtil.nameError());
            highlight(txtName); txtName.requestFocus(); return false;
        }

        // Specialization
        if (cmbSpecialization.getValue() == null) {
            showError(ValidationUtil.requiredError("Specialization")); return false;
        }

        // Phone
        if (!ValidationUtil.isValidPhone(txtPhone.getText())) {
            showError(ValidationUtil.phoneError());
            highlight(txtPhone); txtPhone.requestFocus(); return false;
        }

        // Email
        if (!ValidationUtil.isValidEmail(txtEmail.getText())) {
            showError(ValidationUtil.emailError());
            highlight(txtEmail); txtEmail.requestFocus(); return false;
        }

        return true;
    }

    // ── UI Helpers ────────────────────────────────────────────────────────────

    private void highlight(TextField field) {
        field.setStyle("-fx-border-color: #e74c3c; -fx-border-radius: 4;");
    }

    private void clearFieldStyles() {
        txtName.setStyle(""); txtPhone.setStyle(""); txtEmail.setStyle("");
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
