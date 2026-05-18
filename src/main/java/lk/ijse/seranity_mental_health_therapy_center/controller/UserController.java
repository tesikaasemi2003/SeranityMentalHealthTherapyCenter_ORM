package lk.ijse.seranity_mental_health_therapy_center.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lk.ijse.seranity_mental_health_therapy_center.bo.BOFactory;
import lk.ijse.seranity_mental_health_therapy_center.bo.BOTypes;
import lk.ijse.seranity_mental_health_therapy_center.bo.custom.UserBO;
import lk.ijse.seranity_mental_health_therapy_center.dto.tm.UserTM;
import lk.ijse.seranity_mental_health_therapy_center.entity.User;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML private TextField     txtId;
    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField     txtPasswordVisible;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private PasswordField txtNewPassword;
    @FXML private ComboBox<String> cmbRole;
    @FXML private Label         lblMessage;
    @FXML private Button        btnSave;
    @FXML private Button        btnShowPass;
    @FXML private VBox          vboxChangePass;
    @FXML private TextField     txtSearch;

    @FXML private TableView<UserTM>           tblUser;
    @FXML private TableColumn<UserTM, String> colId;
    @FXML private TableColumn<UserTM, String> colUsername;
    @FXML private TableColumn<UserTM, String> colRole;
    @FXML private TableColumn<UserTM, Void>   colAction;

    private final UserBO userBO =
            (UserBO) BOFactory.getInstance().getBO(BOTypes.USER);

    private final ObservableList<UserTM> masterList = FXCollections.observableArrayList();
    private boolean isEditMode    = false;
    private boolean showPassword  = false;
    private String  editingUserId = null;

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9._]{3,30}$";
    private static final String PASSWORD_REGEX = "^.{6,}$";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbRole.getItems().addAll("ADMIN", "RECEPTIONIST");
        setupTable();
        setupSearch();
        loadTableData();
        generateNextId();

        // Password visible field sync
        txtPasswordVisible.textProperty().addListener((obs, o, val) -> {
            if (showPassword) txtPassword.setText(val);
        });
    }

    private void generateNextId() {
        try {
            User last = userBO.getAllUsers().stream()
                    .max((a, b) -> a.getId().compareTo(b.getId()))
                    .orElse(null);
            if (last == null) { txtId.setText("U001"); return; }
            int num = Integer.parseInt(last.getId().substring(1)) + 1;
            txtId.setText(String.format("U%03d", num));
        } catch (Exception e) { txtId.setText("U001"); }
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final HBox box = new HBox(6, btnEdit, btnDelete);
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
        FilteredList<UserTM> filtered = new FilteredList<>(masterList, p -> true);
        txtSearch.textProperty().addListener((obs, o, val) -> filtered.setPredicate(row -> {
            if (val == null || val.isEmpty()) return true;
            String lower = val.toLowerCase();
            return row.getUsername().toLowerCase().contains(lower)
                    || row.getRole().toLowerCase().contains(lower);
        }));
        tblUser.setItems(filtered);
    }

    private void loadTableData() {
        masterList.clear();
        try {
            for (User u : userBO.getAllUsers()) {
                masterList.add(new UserTM(u.getId(), u.getUsername(), u.getRole()));
            }
        } catch (Exception e) {
            showError("Failed to load: " + e.getMessage());
        }
    }

    @FXML
    private void handleTogglePassword() {
        showPassword = !showPassword;
        if (showPassword) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            btnShowPass.setText("🙈");
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
            btnShowPass.setText("👁");
        }
    }

    @FXML
    private void handleSave() {
        hideMessage();
        if (!validateInputs()) return;

        try {
            String rawPassword = showPassword
                    ? txtPasswordVisible.getText().trim()
                    : txtPassword.getText().trim();

            User user = new User();
            user.setId(txtId.getText().trim());
            user.setUsername(txtUsername.getText().trim());
            user.setRole(cmbRole.getValue());

            if (!isEditMode) {
                // BCrypt encrypt — coursework requirement
                user.setPassword(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
                if (userBO.saveUser(user)) {
                    showSuccess("User saved successfully!");
                    loadTableData();
                    handleClear();
                }
            } else {
                // Edit mode — new password typed නම් update, නැත්නම් existing keep
                String newPass = txtNewPassword.getText().trim();
                if (!newPass.isEmpty()) {
                    if (!newPass.matches(PASSWORD_REGEX)) {
                        showError("New password must be at least 6 characters."); return;
                    }
                    user.setPassword(BCrypt.hashpw(newPass, BCrypt.gensalt()));
                } else {
                    // Existing password keep කරන්න
                    User existing = userBO.searchUser(editingUserId);
                    user.setPassword(existing.getPassword());
                }
                if (userBO.updateUser(user)) {
                    showSuccess("User updated successfully!");
                    loadTableData();
                    handleClear();
                }
            }
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateForm(UserTM row) {
        isEditMode    = true;
        editingUserId = row.getId();
        txtId.setText(row.getId());
        txtUsername.setText(row.getUsername());
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        cmbRole.setValue(row.getRole());
        vboxChangePass.setVisible(true);
        vboxChangePass.setManaged(true);
        btnSave.setText("Update");
        txtUsername.requestFocus();
    }

    private void handleDelete(UserTM row) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete user \"" + row.getUsername() + "\"?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Confirm Delete");
        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    if (userBO.deleteUser(row.getId())) {
                        masterList.remove(row);
                        showSuccess("User deleted.");
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
        isEditMode    = false;
        editingUserId = null;
        txtUsername.clear();
        txtPassword.clear();
        txtPasswordVisible.clear();
        txtConfirmPassword.clear();
        txtNewPassword.clear();
        cmbRole.setValue(null);
        vboxChangePass.setVisible(false);
        vboxChangePass.setManaged(false);
        btnSave.setText("Save");

        // Reset password field visibility
        if (showPassword) handleTogglePassword();

        generateNextId();
        hideMessage();
    }

    private boolean validateInputs() {
        if (!txtUsername.getText().trim().matches(USERNAME_REGEX)) {
            showError("Username must be 3-30 chars, letters/numbers/._");
            txtUsername.requestFocus(); return false;
        }

        // Edit mode ලා password fields skip කරන්න (change pass section use කරනවා)
        if (!isEditMode) {
            String pass = showPassword
                    ? txtPasswordVisible.getText().trim()
                    : txtPassword.getText().trim();
            String confirm = txtConfirmPassword.getText().trim();

            if (!pass.matches(PASSWORD_REGEX)) {
                showError("Password must be at least 6 characters.");
                txtPassword.requestFocus(); return false;
            }
            if (!pass.equals(confirm)) {
                showError("Passwords do not match.");
                txtConfirmPassword.requestFocus(); return false;
            }
        }

        if (cmbRole.getValue() == null) {
            showError("Please select a Role."); return false;
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