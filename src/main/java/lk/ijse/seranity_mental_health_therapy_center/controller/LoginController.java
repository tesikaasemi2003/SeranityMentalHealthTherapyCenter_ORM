package lk.ijse.seranity_mental_health_therapy_center.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.seranity_mental_health_therapy_center.bo.BOFactory;
import lk.ijse.seranity_mental_health_therapy_center.bo.BOTypes;
import lk.ijse.seranity_mental_health_therapy_center.bo.custom.UserBO;
import lk.ijse.seranity_mental_health_therapy_center.bo.exception.InvalidCredentialsException;
import lk.ijse.seranity_mental_health_therapy_center.entity.User;
import lk.ijse.seranity_mental_health_therapy_center.util.ValidationUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField     txtPasswordVisible;   // shown when toggle ON
    @FXML private Button        btnShowPass;
    @FXML private ComboBox<String> cmbRole;
    @FXML private Label         lblError;
    @FXML private Button        btnLogin;

    private final UserBO userBO =
            (UserBO) BOFactory.getInstance().getBO(BOTypes.USER);

    private boolean showPassword = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbRole.setItems(FXCollections.observableArrayList("ADMIN", "RECEPTIONIST"));

        // Enter key on password field triggers login
        txtPassword.setOnAction(this::handleLogin);
        txtPasswordVisible.setOnAction(this::handleLogin);

        // Keep both fields in sync while typing
        txtPasswordVisible.textProperty().addListener((obs, oldVal, newVal) -> {
            if (showPassword) txtPassword.setText(newVal);
        });
        txtPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!showPassword) txtPasswordVisible.setText(newVal);
        });
    }

    /**
     * Password show/hide toggle
     * Assignment requirement — guideline #3:
     * "The user should be able to view their password on the login screen."
     */
    @FXML
    private void handleTogglePassword() {
        showPassword = !showPassword;
        if (showPassword) {
            // Show plain text field
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            btnShowPass.setText("🙈");
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());
        } else {
            // Show password field (masked)
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
            btnShowPass.setText("👁");
            txtPassword.requestFocus();
            txtPassword.positionCaret(txtPassword.getText().length());
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        clearError();

        String username = txtUsername.getText().trim();
        String password = showPassword
                ? txtPasswordVisible.getText().trim()
                : txtPassword.getText().trim();
        String role = cmbRole.getValue();

        // Basic validation
        if (!ValidationUtil.isValidUsername(username)) {
            showError("Please enter a valid username.");
            txtUsername.requestFocus();
            return;
        }
        if (!ValidationUtil.isValidPassword(password)) {
            showError("Please enter your password.");
            return;
        }
        if (role == null) {
            showError("Please select your role.");
            return;
        }

        try {
            User user = userBO.getUserByUsername(username);

            if (user == null) {
                throw new InvalidCredentialsException("Invalid username or password.");
            }
            if (!BCrypt.checkpw(password, user.getPassword())) {
                throw new InvalidCredentialsException("Invalid username or password.");
            }
            if (!user.getRole().equalsIgnoreCase(role)) {
                throw new InvalidCredentialsException(
                        "Role mismatch. Please select the correct role.");
            }

            navigateToDashboard(user);

        } catch (InvalidCredentialsException e) {
            // Custom exception — assignment requirement
            showError(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("System error. Please try again.");
        }
    }

    private void navigateToDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/lk/ijse/seranity_mental_health_therapy_center/view/Dashboard.fxml"));
            Scene scene = new Scene(loader.load());

            DashboardController dc = loader.getController();
            dc.setLoggedUser(user);

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Serenity — Dashboard");
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load dashboard.");
        }
    }

    private void showError(String message) {
        lblError.setStyle("-fx-text-fill: #E53935; -fx-font-size: 12px;");
        lblError.setText("⚠ " + message);
    }

    private void clearError() {
        lblError.setText("");
    }
}
