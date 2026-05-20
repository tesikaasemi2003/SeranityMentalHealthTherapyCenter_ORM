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
import lk.ijse.seranity_mental_health_therapy_center.bo.custom.PaymentBO;
import lk.ijse.seranity_mental_health_therapy_center.bo.custom.RegistrationBO;
import lk.ijse.seranity_mental_health_therapy_center.bo.exception.PaymentProcessingException;
import lk.ijse.seranity_mental_health_therapy_center.entity.Payment;
import lk.ijse.seranity_mental_health_therapy_center.entity.Registration;
import lk.ijse.seranity_mental_health_therapy_center.util.ValidationUtil;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    @FXML private TextField        txtId;
    @FXML private ComboBox<String> cmbRegistration;
    @FXML private DatePicker       dpPaymentDate;
    @FXML private TextField        txtAmount;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private Label            lblMessage;
    @FXML private Button           btnSave;
    @FXML private TextField        txtSearch;

    @FXML private TableView<Payment>              tblPayment;
    @FXML private TableColumn<Payment, String>    colId;
    @FXML private TableColumn<Payment, String>    colRegistration;
    @FXML private TableColumn<Payment, LocalDate> colDate;
    @FXML private TableColumn<Payment, Double>    colAmount;
    @FXML private TableColumn<Payment, String>    colStatus;
    @FXML private TableColumn<Payment, Void>      colAction;

    private final PaymentBO paymentBO =
            (PaymentBO) BOFactory.getInstance().getBO(BOTypes.PAYMENT);
    private final RegistrationBO registrationBO =
            (RegistrationBO) BOFactory.getInstance().getBO(BOTypes.REGISTRATION);

    private final ObservableList<Payment> masterList = FXCollections.observableArrayList();
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbStatus.getItems().addAll("PAID", "PENDING");
        setupTable();
        setupSearch();
        loadRegistrationCombo();
        loadTableData();
        generateNextId();
        dpPaymentDate.setValue(LocalDate.now());
    }

    private void generateNextId() {
        try { txtId.setText(paymentBO.generateNextPaymentId()); }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void loadRegistrationCombo() {
        try {
            cmbRegistration.getItems().clear();
            for (Registration r : registrationBO.getAllRegistrations()) {
                String patientName = r.getPatient() != null ? r.getPatient().getName() : "";
                cmbRegistration.getItems().add(r.getId() + " - " + patientName);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colRegistration.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) { setText(null); return; }
                Payment p = (Payment) getTableRow().getItem();
                setText(p.getRegistration() != null ? p.getRegistration().getId() : "");
            }
        });

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
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void setupSearch() {
        FilteredList<Payment> filtered = new FilteredList<>(masterList, p -> true);
        txtSearch.textProperty().addListener((obs, o, val) -> filtered.setPredicate(row -> {
            if (val == null || val.isEmpty()) return true;
            String lower = val.toLowerCase();
            boolean matchId     = row.getId().toLowerCase().contains(lower);
            boolean matchStatus = row.getStatus() != null && row.getStatus().toLowerCase().contains(lower);
            boolean matchReg    = row.getRegistration() != null &&
                    row.getRegistration().getId().toLowerCase().contains(lower);
            return matchId || matchStatus || matchReg;
        }));
        tblPayment.setItems(filtered);
    }

    private void loadTableData() {
        masterList.clear();
        try { masterList.addAll(paymentBO.getAllPayments()); }
        catch (Exception e) { showError("Failed to load: " + e.getMessage()); }
    }

    @FXML
    private void handleSave() {
        hideMessage();
        if (!validateInputs()) return;
        try {
            String regId = cmbRegistration.getValue().split(" - ")[0];
            Registration reg = registrationBO.searchRegistration(regId);

            Payment payment = new Payment();
            payment.setId(txtId.getText().trim());
            payment.setPaymentDate(dpPaymentDate.getValue());
            payment.setAmount(Double.parseDouble(txtAmount.getText().trim()));
            payment.setStatus(cmbStatus.getValue());
            payment.setRegistration(reg);

            boolean result = isEditMode
                    ? paymentBO.updatePayment(payment)
                    : paymentBO.savePayment(payment);

            if (result) {
                showSuccess(isEditMode ? "Payment updated!" : "Payment saved!");
                loadTableData();
                handleClear();
            }
        } catch (PaymentProcessingException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateForm(Payment row) {
        isEditMode = true;
        txtId.setText(row.getId());
        dpPaymentDate.setValue(row.getPaymentDate());
        txtAmount.setText(String.valueOf(row.getAmount()));
        cmbStatus.setValue(row.getStatus());
        if (row.getRegistration() != null)
            cmbRegistration.getItems().stream()
                    .filter(s -> s.startsWith(row.getRegistration().getId()))
                    .findFirst().ifPresent(cmbRegistration::setValue);
        btnSave.setText("Update");
    }

    private void handleDelete(Payment row) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete payment \"" + row.getId() + "\"?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Confirm Delete");
        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    if (paymentBO.deletePayment(row.getId())) {
                        masterList.remove(row);
                        showSuccess("Payment deleted.");
                        generateNextId();
                    }
                } catch (Exception e) { showError("Delete failed: " + e.getMessage()); }
            }
        });
    }

    @FXML
    private void handleClear() {
        isEditMode = false;
        cmbRegistration.setValue(null);
        dpPaymentDate.setValue(LocalDate.now());
        txtAmount.clear(); cmbStatus.setValue(null);
        btnSave.setText("Save");
        clearFieldStyles();
        generateNextId(); hideMessage();
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private boolean validateInputs() {
        clearFieldStyles();

        // Registration
        if (cmbRegistration.getValue() == null) {
            showError(ValidationUtil.requiredError("Registration")); return false;
        }

        // Payment Date
        if (dpPaymentDate.getValue() == null) {
            showError(ValidationUtil.requiredError("Payment Date")); return false;
        }
        if (dpPaymentDate.getValue().isAfter(LocalDate.now())) {
            showError("⚠ Payment date cannot be a future date."); return false;
        }

        // Amount
        if (!ValidationUtil.isValidAmount(txtAmount.getText())) {
            showError(ValidationUtil.amountError());
            highlight(txtAmount); txtAmount.requestFocus(); return false;
        }
        // Amount > 0 check
        double amount = Double.parseDouble(txtAmount.getText().trim());
        if (amount <= 0) {
            showError("⚠ Amount must be greater than zero.");
            highlight(txtAmount); txtAmount.requestFocus(); return false;
        }

        // Status
        if (cmbStatus.getValue() == null) {
            showError(ValidationUtil.requiredError("Payment Status")); return false;
        }

        return true;
    }

    // ── UI Helpers ────────────────────────────────────────────────────────────

    private void highlight(TextField field) {
        field.setStyle("-fx-border-color: #e74c3c; -fx-border-radius: 4;");
    }

    private void clearFieldStyles() {
        txtAmount.setStyle("");
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