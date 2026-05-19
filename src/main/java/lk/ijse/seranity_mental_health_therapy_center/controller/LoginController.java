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
import lk.ijse.seranity_mental_health_therapy_center.entity.User;
import lk.ijse.seranity_mental_health_therapy_center.bo.exception.InvalidCredentialsException;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbRole;
    @FXML private Label lblError;
    @FXML private Button btnLogin;

    private UserBO userBO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userBO = (UserBO) BOFactory.getInstance().getBO(BOTypes.USER);
        cmbRole.setItems(FXCollections.observableArrayList("ADMIN", "RECEPTIONIST"));
        txtPassword.setOnAction(this::handleLogin);
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String role     = cmbRole.getValue();

        if (username.isEmpty()) { showError("Please enter your username."); return; }
        if (password.isEmpty()) { showError("Please enter your password."); return; }
        if (role == null)       { showError("Please select your role.");    return; }

        try {
            User user = userBO.getUserByUsername(username);

            // Invalid credentials
            if (user == null) {
                throw new InvalidCredentialsException("Invalid username or password.");
            }
            if (!BCrypt.checkpw(password, user.getPassword())) {
                throw new InvalidCredentialsException("Invalid username or password.");
            }
            if (!user.getRole().equalsIgnoreCase(role)) {
                throw new InvalidCredentialsException(
                        "Role mismatch. Please select the correct role."
                );
            }

            navigateToDashboard(user);

        } catch (InvalidCredentialsException e) {
            // Custom exception catch — assignment requirement
            showError(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("System error. Please try again.");
        }
    }
    private void navigateToDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/lk/ijse/seranity_mental_health_therapy_center/view/Dashboard.fxml"));
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
        lblError.setText(message);
    }
}