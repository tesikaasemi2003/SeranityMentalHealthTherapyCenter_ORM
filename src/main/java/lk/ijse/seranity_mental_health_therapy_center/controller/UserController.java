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
import lk.ijse.seranity_mental_health_therapy_center.util.ValidationUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    // Form fields
    @FXML private TextField        txtId;
    @FXML private TextField        txtUsername;
    @FXML private PasswordField    txtPassword;
    @FXML private TextField        txtPasswordVisible;
    @FXML private Button           btnShowPass;
    @FXML private PasswordField    txtConfirmPassword;
    @FXML private ComboBox<String> cmbRole;

    // Change password section (edit mode only)
    @FXML private VBox             vboxChangePass;
    @FXML private PasswordField    txtNewPassword;
    @FXML private TextField        txtNewPasswordVisible;
    @FXML private Button           btnShowNewPass;
    @FXML private PasswordField    txtConfirmNewPassword;

    // UI
    @FXML private Label            lblMessage;
    @FXML private Button           btnSave;
    @FXML private TextField        txtSearch;

    // Table
    @FXML private TableView<UserTM>           tblUser;
    @FXML private TableColumn<UserTM, String> colId;
    @FXML private TableColumn<UserTM, String> colUsername;
    @FXML private TableColumn<UserTM, String> colRole;
    @FXML private TableColumn<UserTM, Void>   colAction;

    private final UserBO userBO =
            (UserBO) BOFactory.getInstance().getBO(BOTypes.USER);

    private final ObservableList<UserTM> masterList = FXCollections.observableArrayList();

    private boolean isEditMode    = false;
    private boolean showPass      = false;
    private boolean showNewPass   = false;
    private String  editingUserId = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbRole.getItems().addAll("ADMIN", "RECEPTIONIST");
        setupTable();
        setupSearch();
        loadTableData();
        generateNextId();
        syncPasswordFields();
    }

    private void syncPasswordFields() {
        txtPasswordVisible.textProperty().addListener((obs, o, val) -> {
            if (showPass) txtPassword.setText(val);
        });
        txtPassword.textProperty().addListener((obs, o, val) -> {
            if (!showPass) txtPasswordVisible.setText(val);
        });
        txtNewPasswordVisible.textProperty().addListener((obs, o, val) -> {
            if (showNewPass) txtNewPassword.setText(val);
        });
        txtNewPassword.textProperty().addListener((obs, o, val) -> {
            if (!showNewPass) txtNewPasswordVisible.setText(val);
        });
    }

    // Password toggle — create
    @FXML
    private void handleTogglePassword() {
        showPass = !showPass;
        if (showPass) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);  txtPasswordVisible.setManaged(true);
            txtPassword.setVisible(false);         txtPassword.setManaged(false);
            btnShowPass.setText("🙈");
            txtPasswordVisible.requestFocus();
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);          txtPassword.setManaged(true);
            txtPasswordVisible.setVisible(false);  txtPasswordVisible.setManaged(false);
            btnShowPass.setText("👁");
            txtPassword.requestFocus();
        }
    }

    // Password toggle — change (edit mode)
    @FXML
    private void handleToggleNewPassword() {
        showNewPass = !showNewPass;
        if (showNewPass) {
            txtNewPasswordVisible.setText(txtNewPassword.getText());
            txtNewPasswordVisible.setVisible(true);  txtNewPasswordVisible.setManaged(true);
            txtNewPassword.setVisible(false);         txtNewPassword.setManaged(false);
            btnShowNewPass.setText("🙈");
            txtNewPasswordVisible.requestFocus();
        } else {
            txtNewPassword.setText(txtNewPasswordVisible.getText());
            txtNewPassword.setVisible(true);          txtNewPassword.setManaged(true);
            txtNewPasswordVisible.setVisible(false);  txtNewPasswordVisible.setManaged(false);
            btnShowNewPass.setText("👁");
            txtNewPassword.requestFocus();
        }
    }

    @FXML
    private void handleSave() {
        hideMessage();
        if (!validateInputs()) return;
        try {
            User user = new User();
            user.setId(txtId.getText().trim());
            user.setUsername(txtUsername.getText().trim());
            user.setRole(cmbRole.getValue());

            if (!isEditMode) {
                String rawPass = showPass
                        ? txtPasswordVisible.getText().trim()
                        : txtPassword.getText().trim();
                user.setPassword(BCrypt.hashpw(rawPass, BCrypt.gensalt()));
                if (userBO.saveUser(user)) { showSuccess("User saved!"); loadTableData(); handleClear(); }
            } else {
                String newPass = showNewPass
                        ? txtNewPasswordVisible.getText().trim()
                        : txtNewPassword.getText().trim();
                if (!newPass.isEmpty()) {
                    if (!ValidationUtil.isStrongPassword(newPass)) {
                        showError(ValidationUtil.strongPasswordError()); return;
                    }
                    if (!ValidationUtil.doPasswordsMatch(newPass, txtConfirmNewPassword.getText().trim())) {
                        showError("New passwords do not match."); return;
                    }
                    user.setPassword(BCrypt.hashpw(newPass, BCrypt.gensalt()));
                } else {
                    user.setPassword(userBO.searchUser(editingUserId).getPassword());
                }
                if (userBO.updateUser(user)) { showSuccess("User updated!"); loadTableData(); handleClear(); }
            }
        } catch (Exception e) { showError("Error: " + e.getMessage()); e.printStackTrace(); }
    }

    private void populateForm(UserTM row) {
        isEditMode = true; editingUserId = row.getId();
        txtId.setText(row.getId()); txtUsername.setText(row.getUsername());
        txtPassword.clear(); txtPasswordVisible.clear(); txtConfirmPassword.clear();
        txtNewPassword.clear(); txtNewPasswordVisible.clear(); txtConfirmNewPassword.clear();
        cmbRole.setValue(row.getRole());
        vboxChangePass.setVisible(true); vboxChangePass.setManaged(true);
        btnSave.setText("Update"); txtUsername.requestFocus(); hideMessage();
    }

    private void handleDelete(UserTM row) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete \"" + row.getUsername() + "\"?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText("Confirm Delete");
        a.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    if (userBO.deleteUser(row.getId())) { masterList.remove(row); showSuccess("Deleted."); generateNextId(); }
                } catch (Exception e) { showError(e.getMessage()); }
            }
        });
    }

    @FXML
    private void handleClear() {
        isEditMode = false; editingUserId = null;
        txtUsername.clear(); txtPassword.clear(); txtPasswordVisible.clear();
        txtConfirmPassword.clear(); txtNewPassword.clear();
        txtNewPasswordVisible.clear(); txtConfirmNewPassword.clear();
        cmbRole.setValue(null);
        vboxChangePass.setVisible(false); vboxChangePass.setManaged(false);
        btnSave.setText("Save");
        if (showPass)    handleTogglePassword();
        if (showNewPass) handleToggleNewPassword();
        clearFieldStyles(); generateNextId(); hideMessage();
    }

    private boolean validateInputs() {
        clearFieldStyles();
        if (!ValidationUtil.isValidUsername(txtUsername.getText())) {
            showError(ValidationUtil.usernameError()); highlight(txtUsername); txtUsername.requestFocus(); return false;
        }
        if (!isEditMode) {
            String pass    = showPass ? txtPasswordVisible.getText().trim() : txtPassword.getText().trim();
            String confirm = txtConfirmPassword.getText().trim();
            if (!ValidationUtil.isStrongPassword(pass)) {
                showError(ValidationUtil.strongPasswordError()); return false;
            }
            if (!ValidationUtil.doPasswordsMatch(pass, confirm)) {
                showError(ValidationUtil.passwordMismatchError()); txtConfirmPassword.requestFocus(); return false;
            }
        }
        if (cmbRole.getValue() == null) { showError(ValidationUtil.requiredError("Role")); return false; }
        return true;
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button e = new Button("Edit");
            private final Button d = new Button("Delete");
            private final HBox   b = new HBox(6, e, d);
            {
                e.setStyle("-fx-background-color:#2B3990;-fx-text-fill:white;-fx-background-radius:5;-fx-padding:4 10;-fx-cursor:hand;-fx-font-size:11px;");
                d.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;-fx-background-radius:5;-fx-padding:4 10;-fx-cursor:hand;-fx-font-size:11px;");
                e.setOnAction(ev -> populateForm(getTableView().getItems().get(getIndex())));
                d.setOnAction(ev -> handleDelete(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) { super.updateItem(item, empty); setGraphic(empty ? null : b); }
        });
    }

    private void setupSearch() {
        FilteredList<UserTM> f = new FilteredList<>(masterList, p -> true);
        txtSearch.textProperty().addListener((obs, o, val) -> f.setPredicate(row -> {
            if (val == null || val.isEmpty()) return true;
            String lower = val.toLowerCase();
            return row.getUsername().toLowerCase().contains(lower) || row.getRole().toLowerCase().contains(lower);
        }));
        tblUser.setItems(f);
    }

    private void loadTableData() {
        masterList.clear();
        try { for (User u : userBO.getAllUsers()) masterList.add(new UserTM(u.getId(), u.getUsername(), u.getRole())); }
        catch (Exception e) { showError("Failed to load: " + e.getMessage()); }
    }

    private void generateNextId() {
        try {
            User last = userBO.getAllUsers().stream().max((a, b) -> a.getId().compareTo(b.getId())).orElse(null);
            if (last == null) { txtId.setText("U001"); return; }
            txtId.setText(String.format("U%03d", Integer.parseInt(last.getId().substring(1)) + 1));
        } catch (Exception e) { txtId.setText("U001"); }
    }

    private void highlight(TextField f) { f.setStyle("-fx-border-color:#e74c3c;-fx-border-radius:4;"); }
    private void clearFieldStyles() { txtUsername.setStyle(""); txtPassword.setStyle(""); txtConfirmPassword.setStyle(""); }
    private void showError(String m) { lblMessage.setText("⚠ "+m); lblMessage.setStyle("-fx-text-fill:#c0392b;-fx-font-size:13px;-fx-font-weight:bold;"); lblMessage.setVisible(true); lblMessage.setManaged(true); }
    private void showSuccess(String m) { lblMessage.setText("✔ "+m); lblMessage.setStyle("-fx-text-fill:#27ae60;-fx-font-size:13px;-fx-font-weight:bold;"); lblMessage.setVisible(true); lblMessage.setManaged(true); }
    private void hideMessage() { lblMessage.setVisible(false); lblMessage.setManaged(false); }
}
